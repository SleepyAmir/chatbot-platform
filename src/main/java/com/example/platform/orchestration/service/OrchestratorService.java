package com.example.platform.orchestration.service;

import com.example.platform.common.constant.ChatIntents;
import com.example.platform.common.util.StepTimer;
import com.example.platform.modules.cache.service.SemanticCacheService;
import com.example.platform.modules.chatlog.client.LLMClient;
import com.example.platform.modules.chatlog.document.MessageDoc;
import com.example.platform.modules.chatlog.dto.ChatRequest;
import com.example.platform.modules.chatlog.dto.ChatResponse;
import com.example.platform.modules.chatlog.service.ChatSessionService;
import com.example.platform.modules.course.dto.response.CourseDetailResponse;
import com.example.platform.modules.course.dto.response.CourseResponse;
import com.example.platform.modules.course.service.CourseDetailService;
import com.example.platform.modules.course.service.CourseService;
import com.example.platform.modules.qa.client.IntentClient;
import com.example.platform.modules.qa.dto.QaMatch;
import com.example.platform.modules.qa.service.FAQService;
import com.example.platform.modules.qa.service.QaSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrchestratorService {

    /** کلید MDC که در application.yaml (logging.pattern.console) با %X{traceId} نمایش داده می‌شود. */
    private static final String MDC_TRACE_ID = "traceId";

    private final IntentClient intentClient;
    private final FAQService faqService;
    private final QaSearchService qaSearchService;
    private final CourseService courseService;
    private final CourseDetailService courseDetailService;
    private final LLMClient llmClient;
    private final SemanticCacheService cacheService;
    private final ChatSessionService chatSessionService;

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
            String sessionId = request.getSessionId();
            String rawQuestion = extractQuery(request);

            if (rawQuestion == null || rawQuestion.isBlank()) {
                log.warn("Rejected request: blank question. sessionId={}", sessionId);
                return attachMeta(ChatResponse.error("متن سوال نمی‌تواند خالی باشد."), traceId, timer);
            }

            log.info("Incoming query: '{}' (Session: {})", rawQuestion, sessionId);

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
                    h = new java.util.ArrayList<>();
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
                        h = new java.util.ArrayList<>();
                        log.info("New session created: {} for user: {}", resolvedSessionIdHolder[0], userId);
                    }
                }
                return h;
            });
            sessionId = resolvedSessionIdHolder[0];

            // ۲. شفاف‌سازی سوال با استفاده از تاریخچه
            String finalSessionId = sessionId;
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
                timer.timeVoid("history_save", () -> saveToHistory(finalSessionId, rawQuestion, cachedAnswer));
                timer.logSummary();
                return attachMeta(ChatResponse.success(cachedAnswer), traceId, timer);
            }
            log.info("Semantic cache MISS — proceeding to intent detection.");

            // ۴. تشخیص نیت کاربر
            String intent = timer.time("intent_detection", () -> detectIntent(resolvedQuestion));
            log.info("Detected intent: '{}'", intent);

            // ۵. مسیریابی و تولید پاسخ
            List<MessageDoc> finalHistory = history;
            ChatResponse response = timer.time("handle_" + intent, () -> switch (intent) {
                case ChatIntents.FAQ -> handleFAQ(resolvedQuestion, finalHistory);
                case ChatIntents.PRICING -> handlePricing(resolvedQuestion);
                case ChatIntents.CLASS_SEARCH -> handleClassSearch(resolvedQuestion);
                default -> handleLLMWithContext(resolvedQuestion, finalHistory);
            });

            log.info("Response generated. success={}", response.isSuccess());

            // ۶. ذخیره در تاریخچه و کش
            if (response.isSuccess()) {
                timer.timeVoid("history_save", () -> saveToHistory(finalSessionId, rawQuestion, response.getAnswer()));

                timer.timeVoid("cache_save", () -> {
                    try {
                        cacheService.save(resolvedQuestion, response.getAnswer());
                    } catch (Exception e) {
                        log.warn("Cache save failed", e);
                    }
                });
            } else {
                log.warn("Response was not successful, skipping history/cache save. error={}", response.getError());
            }

            timer.logSummary();
            return attachMeta(response, traceId, timer);

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

    private String detectIntent(String query) {
        try {
            return intentClient.detectIntent(query);
        } catch (Exception e) {
            log.warn("Intent detection failed, fallback to LLM", e);
            return ChatIntents.LLM;
        }
    }

    private ChatResponse handleFAQ(String query, List<MessageDoc> history) {
        var faq = faqService.find(query);
        if (faq != null) {
            log.info("Answered from static FAQService.");
            return ChatResponse.success(faq.getAnswer());
        }
        log.info("No static FAQ match, falling through to LLM/QA pipeline.");
        return handleLLMWithContext(query, history);
    }

    private ChatResponse handlePricing(String query) {
        String keyword = normalizeCourseQuery(query);
        Page<CourseResponse> courses = courseService.searchCourses(keyword, PageRequest.of(0, 5));
        log.info("Pricing lookup for keyword='{}' -> {} course(s) found.", keyword, courses.getTotalElements());

        if (courses.isEmpty()) return ChatResponse.success("متأسفانه قیمت دوره‌ای با عنوان '" + keyword + "' را پیدا نکردم.");

        String answer = courses.getContent().stream()
                .map(c -> {
                    var d = courseDetailService.getDetailByCourseId(c.id());
                    return (d != null && d.price() != null) ? "💰 قیمت " + c.name() + ": " + d.price() + " تومان"
                            : "قیمت دوره " + c.name() + " ثبت نشده است.";
                }).collect(Collectors.joining("\n"));
        return ChatResponse.success(answer);
    }

    private ChatResponse handleClassSearch(String query) {
        String keyword = normalizeCourseQuery(query);
        Page<CourseResponse> courses = courseService.searchCourses(keyword, PageRequest.of(0, 5));
        log.info("Class search for keyword='{}' -> {} course(s) found.", keyword, courses.getTotalElements());

        if (courses.isEmpty()) return ChatResponse.success("دوره‌ای برای '" + keyword + "' پیدا نشد.");

        String answer = courses.getContent().stream()
                .map(this::formatCourse)
                .collect(Collectors.joining("\n\n"));
        return ChatResponse.success(answer);
    }

    private ChatResponse handleLLMWithContext(String question, List<MessageDoc> history) {
        // قبل از رفتن سراغ LLM واقعی: آیا سوال مشابهی قبلاً به qa_pairs "ارتقا" پیدا کرده؟
        // (جست‌وجوی برداری + rerank — نگاه کنید به QaSearchService)
        Optional<QaMatch> qaMatch = qaSearchService.findBestMatch(question);
        if (qaMatch.isPresent()) {
            QaMatch match = qaMatch.get();
            log.info("Answered from qa_pairs via semantic search + rerank. qaId={}, vectorScore={}, rerankScore={}",
                    match.qaId(), match.vectorScore(), match.rerankScore());
            return ChatResponse.success(match.answer());
        }

        log.info("No qa_pairs match above threshold, calling LLM client.");
        String prompt = buildPrompt(question, history);
        String answer = llmClient.ask(prompt);
        return ChatResponse.success(answer);
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
        CourseDetailResponse d = courseDetailService.getDetailByCourseId(course.id());
        if (d == null) return "📚 " + course.name();
        return String.format("📚 %s\n💰 قیمت: %s\n👨‍🏫 مدرس: %s\n⏳ مدت: %s",
                course.name(), d.price(), d.teacher(), d.duration());
    }

    private String normalizeCourseQuery(String query) {
        String q = query.toLowerCase().replace("؟", "").trim();
        if (q.contains("پایتون")) return "Python";
        if (q.contains("جاوا") && !q.contains("اسکریپت")) return "Java";
        if (q.contains("javascript") || q.contains("جاوااسکریپت")) return "JavaScript";
        return q.replace("کلاس", "").replace("دوره", "").trim();
    }
}