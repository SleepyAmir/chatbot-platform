package com.example.platform.orchestration.service;

import com.example.platform.common.constant.ChatIntents;
import com.example.platform.common.util.StepTimer;
import com.example.platform.modules.cache.service.SemanticCacheService;
import com.example.platform.modules.chatlog.client.LLMClient;
import com.example.platform.modules.chatlog.document.MessageDoc;
import com.example.platform.modules.chatlog.dto.ChatRequest;
import com.example.platform.modules.chatlog.dto.ChatResponse;
import com.example.platform.common.exception.ResourceNotFoundException;
import com.example.platform.modules.chatlog.dto.request.ChatLogRequest;
import com.example.platform.modules.chatlog.service.ChatLogService;
import com.example.platform.modules.chatlog.service.ChatSessionService;
import com.example.platform.modules.course.dto.response.CourseDetailResponse;
import com.example.platform.modules.course.dto.response.CourseResponse;
import com.example.platform.modules.course.service.CourseDetailService;
import com.example.platform.modules.course.service.CourseService;
import com.example.platform.modules.qa.client.IntentClient;
import com.example.platform.modules.qa.dto.QaMatch;
import com.example.platform.modules.qa.service.QaSearchService;
import com.example.platform.orchestration.util.CourseQueryNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * نقطه‌ی مرکزی orchestration چت: کش معنایی -> تشخیص نیت -> مسیریابی (FAQ/قیمت/کلاس/LLM) -> ذخیره‌ی
 * تاریخچه (Mongo, برای شفاف‌سازی follow-up) و لاگ تحلیلی (Postgres chat_logs, برای گزارش‌گیری/فیدبک).
 *
 * <p>این نسخه با ماژول‌های نهایی‌شده‌ی qa/course/chatlog روی master هماهنگ شده:</p>
 * <ul>
 *     <li>FAQService مستقل حذف شد — qa_pairs (از طریق {@link QaSearchService}) خودش منبع FAQ است،
 *     پس نیت FAQ هم از همان مسیر qa_pairs+LLM رد می‌شود، نه یک لیست FAQ جدای هاردکد.</li>
 *     <li>بعد از تولید هر پاسخ موفق، یک رکورد در chat_logs (از طریق {@link ChatLogService}) هم
 *     ذخیره می‌شود تا گزارش‌ها/فیدبک روی همان داده‌ی واقعی orchestration کار کنند، نه جدا از آن.</li>
 *     <li>نرمال‌سازی نام دوره از یک if/else هاردکد به {@link CourseQueryNormalizer} منتقل شد.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrchestratorService {

    /** کلید MDC که در application.yaml (logging.pattern.console) با %X{traceId} نمایش داده می‌شود. */
    private static final String MDC_TRACE_ID = "traceId";

    private final IntentClient intentClient;
    private final QaSearchService qaSearchService;
    private final CourseService courseService;
    private final CourseDetailService courseDetailService;
    private final LLMClient llmClient;
    private final SemanticCacheService cacheService;
    private final ChatSessionService chatSessionService;
    private final ChatLogService chatLogService;

    /**
     * متد اصلی ورود اطلاعات که تاریخچه، کش و جستجو را ترکیب می‌کند.
     *
     * هر فراخوانی این متد یک traceId یکتا می‌گیرد که هم در MDC (پس در تمام لاگ‌های زیرسیستم‌ها
     * مثل QaSearchService/SemanticCacheService/Real-Fake Clientها) و هم در پاسخ نهایی به کلاینت
     * (ChatResponse.traceId) قرار می‌گیرد — برای ردیابی end-to-end یک درخواست.
     * هم‌چنین مدت‌زمان هر مرحله با StepTimer اندازه‌گیری و در یک خط خلاصه لاگ می‌شود.
     */
    public ChatResponse chat(ChatRequest request) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put(MDC_TRACE_ID, traceId);
        StepTimer timer = new StepTimer(log, "chat-orchestration");

        try {
            String rawQuestion = extractQuery(request);

            if (rawQuestion == null || rawQuestion.isBlank()) {
                log.warn("Rejected request: blank question. sessionId={}", request.getSessionId());
                return attachMeta(ChatResponse.error("متن سوال نمی‌تواند خالی باشد."), traceId, timer);
            }
            log.info("Incoming query: '{}' (Session: {})", rawQuestion, request.getSessionId());

            // ۱. آماده‌سازی sessionId و history
            final String[] resolvedSessionIdHolder = new String[1];
            List<MessageDoc> history = timer.time("session_resolution", () -> {
                String sid = request.getSessionId();
                List<MessageDoc> h;

                if (sid == null || sid.isBlank()) {
                    log.info("No sessionId provided. Creating a new session.");
                    String userId = request.getUserId();
                    if (userId == null || userId.isBlank()) {
                        userId = "guest-user";
                    }

                    var newSession = chatSessionService.createSession(userId);
                    resolvedSessionIdHolder[0] = newSession.getSessionId();
                    h = new ArrayList<>();
                    log.info("New session created: {} for user: {}", resolvedSessionIdHolder[0], userId);

                } else {
                    try {
                        h = chatSessionService.getHistory(sid);
                        resolvedSessionIdHolder[0] = sid;
                        log.info("Loaded existing session: {} ({} previous messages)", sid, h.size());
                    } catch (RuntimeException e) {
                        log.info("Session {} not found. Creating a new session for user: {}", sid, request.getUserId());

                        String userId = request.getUserId();
                        if (userId == null || userId.isBlank()) {
                            userId = "guest-user";
                        }

                        var newSession = chatSessionService.createSession(userId);
                        resolvedSessionIdHolder[0] = newSession.getSessionId();
                        h = new ArrayList<>();
                        log.info("New session created: {} for user: {}", resolvedSessionIdHolder[0], userId);
                    }
                }
                return h;
            });
            String sessionId = resolvedSessionIdHolder[0];

            // ۲. شفاف‌سازی سوال با استفاده از تاریخچه
            String resolvedQuestion = timer.time("followup_resolution",
                    () -> resolveFollowUp(rawQuestion, history));

            if (!rawQuestion.equals(resolvedQuestion)) {
                log.info("Follow-up resolved: '{}' -> '{}'", rawQuestion, resolvedQuestion);
            } else {
                log.debug("No follow-up resolution needed for: '{}'", rawQuestion);
            }

            // ۳. بررسی کش معنایی با سوال شفاف‌شده
            String cachedAnswer = timer.time("cache_lookup", () -> {
                try {
                    return cacheService.find(resolvedQuestion);
                } catch (Exception e) {
                    log.warn("Cache lookup failed", e);
                    return null;
                }
            });

            if (cachedAnswer != null) {
                log.info("Semantic cache HIT — skipping intent detection and downstream services entirely.");
                ChatResponse cachedResponse = ChatResponse.success(cachedAnswer);
                cachedResponse.setModelUsed("cache");

                timer.timeVoid("history_save", () -> saveToHistory(sessionId, rawQuestion, cachedAnswer));
                timer.timeVoid("chatlog_save", () -> saveChatLog(sessionId, rawQuestion, cachedResponse, timer.totalMs()));
                timer.logSummary();
                return attachMeta(cachedResponse, traceId, timer, sessionId);
            }
            log.info("Semantic cache MISS — proceeding to intent detection.");

            // ۴. تشخیص نیت کاربر
            String intent = timer.time("intent_detection", () -> detectIntent(resolvedQuestion));
            log.info("Detected intent: '{}'", intent);

            // ۵. مسیریابی و تولید پاسخ
            List<MessageDoc> finalHistory = history;
            ChatResponse response = timer.time("handle_" + intent, () -> switch (intent) {
                // نکته: FAQService مستقل حذف شد؛ qa_pairs از طریق QaSearchService خودش منبع FAQ است،
                // پس نیت FAQ هم از همان مسیر qa_pairs+LLM رد می‌شود.
                case ChatIntents.FAQ -> handleLLMWithContext(resolvedQuestion, finalHistory);
                case ChatIntents.PRICING -> handlePricing(resolvedQuestion);
                case ChatIntents.CLASS_SEARCH -> handleClassSearch(resolvedQuestion);
                default -> handleLLMWithContext(resolvedQuestion, finalHistory);
            });

            log.info("Response generated. success={}", response.isSuccess());

            // ۶. ذخیره در تاریخچه، کش، و لاگ تحلیلی
            if (response.isSuccess()) {
                timer.timeVoid("history_save", () -> saveToHistory(sessionId, rawQuestion, response.getAnswer()));

                timer.timeVoid("cache_save", () -> {
                    try {
                        cacheService.save(resolvedQuestion, response.getAnswer());
                    } catch (Exception e) {
                        log.warn("Cache save failed", e);
                    }
                });

                timer.timeVoid("chatlog_save", () -> saveChatLog(sessionId, rawQuestion, response, timer.totalMs()));
            } else {
                log.warn("Response was not successful, skipping history/cache/chatlog save. error={}", response.getError());
            }

            timer.logSummary();
            return attachMeta(response, traceId, timer, sessionId);

        } catch (Exception e) {
            log.error("Orchestration failed", e);
            timer.logSummary();
            return attachMeta(ChatResponse.error("خطا در پردازش درخواست: " + e.getMessage()), traceId, timer);
        } finally {
            MDC.remove(MDC_TRACE_ID);
        }
    }

    private ChatResponse attachMeta(ChatResponse response, String traceId, StepTimer timer) {
        response.setTraceId(traceId);
        response.setElapsedMs(timer.totalMs());
        return response;
    }

    private ChatResponse attachMeta(ChatResponse response, String traceId, StepTimer timer, String sessionId) {
        response.setTraceId(traceId);
        response.setElapsedMs(timer.totalMs());
        if (sessionId != null) {
            response.setSessionId(sessionId);
        }
        return response;
    }

    // متد کمکی برای شفاف‌سازی سوالات کوتاه مثل "چند؟"
    private String resolveFollowUp(String question, List<MessageDoc> history) {
        if (history.isEmpty() || question.length() > 20) return question;

        String lastTopic = "";
        for (int i = history.size() - 1; i >= 0; i--) {
            if ("user".equalsIgnoreCase(history.get(i).getRole())) {
                lastTopic = history.get(i).getContent();
                break;
            }
        }

        String q = question.trim();
        if (q.matches(".*(چند|قیمت|هزینه|چقدر|زمان|کی|کجاست).*")) {
            return lastTopic + " " + q;
        }
        return question;
    }

    private void saveToHistory(String sessionId, String userMsg, String assistantMsg) {
        chatSessionService.addMessage(sessionId, "user", userMsg);
        chatSessionService.addMessage(sessionId, "assistant", assistantMsg);
        log.debug("History saved for session={}", sessionId);
    }

    /**
     * لاگ تحلیلی در Postgres (chat_logs) — کاملاً مجزا و مستقل از تاریخچه‌ی Mongo بالا.
     * عمداً fail-open: یک خطای دیتابیس در ذخیره‌ی لاگ تحلیلی نباید پاسخ کاربر را خراب کند.
     */
    private void saveChatLog(String sessionId, String question, ChatResponse response, long elapsedMs) {
        try {
            chatLogService.saveLog(new ChatLogRequest(
                    sessionId,
                    question,
                    response.getMatchedQaId(),
                    response.getAnswer(),
                    response.getConfidence() != null ? response.getConfidence().floatValue() : null,
                    response.getModelUsed(),
                    (int) elapsedMs
            ));
        } catch (Exception e) {
            log.warn("Failed to persist chat log for analytics (non-fatal). sessionId={}", sessionId, e);
        }
    }

    private String detectIntent(String query) {
        try {
            return intentClient.detectIntent(query);
        } catch (Exception e) {
            log.warn("Intent detection failed, fallback to LLM", e);
            return ChatIntents.LLM;
        }
    }

    private ChatResponse handlePricing(String query) {
        String keyword = CourseQueryNormalizer.normalize(query);
        List<CourseResponse> matchedCourses = findCoursesByKeyword(keyword);
        log.info("Pricing lookup for keyword='{}' -> {} course(s) found.", keyword, matchedCourses.size());

        ChatResponse response;
        if (matchedCourses.isEmpty()) {
            response = ChatResponse.success("متأسفانه قیمت دوره‌ای با عنوان '" + keyword + "' را پیدا نکردم.");
        } else {
            String answer = matchedCourses.stream()
                    .map(c -> {
                        // getDetailByCourseId throws ResourceNotFoundException (not null!) when a
                        // course has no detail row yet, so it must be guarded, not null-checked.
                        if (!courseDetailService.existsByCourseId(c.id())) {
                            return "قیمت دوره " + c.name() + " ثبت نشده است.";
                        }
                        var d = courseDetailService.getDetailByCourseId(c.id());
                        return (d.price() != null) ? "💰 قیمت " + c.name() + ": " + d.price() + " تومان"
                                : "قیمت دوره " + c.name() + " ثبت نشده است.";
                    }).collect(Collectors.joining("\n"));
            response = ChatResponse.success(answer);
        }
        response.setModelUsed("course_lookup");
        return response;
    }
    private ChatResponse handleClassSearch(String query) {
        String keyword = CourseQueryNormalizer.normalize(query);
        List<CourseResponse> matchedCourses = findCoursesByKeyword(keyword);
        log.info("Class search for keyword='{}' -> {} course(s) found.", keyword, matchedCourses.size());

        ChatResponse response;
        if (matchedCourses.isEmpty()) {
            response = ChatResponse.success("دوره‌ای برای '" + keyword + "' پیدا نشد.");
        } else {
            String answer = matchedCourses.stream()
                    .map(this::formatCourse)
                    .collect(Collectors.joining("\n\n"));
            response = ChatResponse.success(answer);
        }
        response.setModelUsed("course_lookup");
        return response;
    }
    /**
     * courseService.searchCourses does a plain substring LIKE match, so a keyword
     * like "Java" also matches "JavaScript" (which starts with "Java") - fine for
     * a general course browser, but wrong for "what's the price of X" where the
     * user means exactly one course. CourseQueryNormalizer already produces exact
     * canonical names for known aliases (Java, JavaScript, Python, ...), so try an
     * exact match first and only fall back to the fuzzy multi-result search for
     * free-form keywords that aren't a recognized alias.
     */
    private List<CourseResponse> findCoursesByKeyword(String keyword) {
        try {
            return List.of(courseService.getCourseByName(keyword));
        } catch (ResourceNotFoundException e) {
            return courseService.searchCourses(keyword, PageRequest.of(0, 5)).getContent();
        }
    }

    private ChatResponse handleLLMWithContext(String question, List<MessageDoc> history) {
        // قبل از رفتن سراغ LLM واقعی: آیا سوال مشابهی قبلاً در qa_pairs آماده شده؟
        // (جست‌وجوی برداری pgvector + rerank — نگاه کنید به QaSearchService)
        Optional<QaMatch> qaMatch = qaSearchService.findBestMatch(question);
        if (qaMatch.isPresent()) {
            QaMatch match = qaMatch.get();
            log.info("Answered from qa_pairs via semantic search + rerank. qaId={}, vectorScore={}, rerankScore={}",
                    match.qaId(), match.vectorScore(), match.rerankScore());

            ChatResponse response = ChatResponse.success(match.answer());
            response.setMatchedQaId(match.qaId());
            response.setConfidence(match.rerankScore());
            response.setModelUsed("qa_pairs");
            return response;
        }

        log.info("No qa_pairs match above threshold, calling LLM client.");
        String prompt = buildPrompt(question, history);
        String answer = llmClient.ask(prompt);

        ChatResponse response = ChatResponse.success(answer);
        response.setModelUsed("llm");
        return response;
    }

    private String buildPrompt(String question, List<MessageDoc> history) {
        String context = history.stream()
                .skip(Math.max(0, history.size() - 6))
                .map(m -> m.getRole() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));

        return "تاریخچه گفتگو:\n" + context + "\n\nسوال کاربر: " + question + "\nپاسخ کوتاه و صمیمی:";
    }

    private String extractQuery(ChatRequest request) {
        if (request.getQuestion() != null && !request.getQuestion().isBlank()) return request.getQuestion().trim();
        return "";
    }

    private String formatCourse(CourseResponse course) {
        if (!courseDetailService.existsByCourseId(course.id())) {
            return "📚 " + course.name();
        }
        CourseDetailResponse d = courseDetailService.getDetailByCourseId(course.id());
        return String.format("📚 %s\n💰 قیمت: %s\n👨‍🏫 مدرس: %s\n⏳ مدت: %s",
                course.name(), d.price(), d.teacher(), d.duration());
    }
}
