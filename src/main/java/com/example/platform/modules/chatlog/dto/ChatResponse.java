package com.example.platform.modules.chatlog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private boolean success;
    private String answer;
    private String error;

    /**
     * Session id (echoed back so the client can send it in the next request's
     * ChatRequest.sessionId to continue the same conversation). Especially
     * important when the caller didn't send a sessionId and the orchestrator
     * had to create a new session - see OrchestratorService.chat().
     */
    private String sessionId;

    /** شناسه‌ی یکتای این درخواست — برای پیدا کردن همه‌ی لاگ‌های مربوط به آن (نگاه کنید به StepTimer/MDC traceId). */
    private String traceId;

    /** مدت‌زمان کل پردازش این درخواست در ارکستریشن، به میلی‌ثانیه. */
    private Long elapsedMs;

    // --- فیلدهای زیر فقط برای لاگ تحلیلی (ChatLogService -> جدول chat_logs) پر می‌شوند؛
    //     در پاسخ JSON به کلاینت هم بی‌ضرر هستند (اطلاعات اضافه‌ی مفیدی مثل منبع پاسخ) ---

    /** اگر پاسخ از qa_pairs آمده باشد، id همان QA pair؛ در غیر این صورت null. */
    private Integer matchedQaId;

    /** امتیاز اطمینان تطبیق (rerank score برای qa_pairs)؛ برای پاسخ LLM/دوره null است. */
    private Double confidence;

    /** منبع پاسخ: "cache" | "qa_pairs" | "llm" | "course_lookup". */
    private String modelUsed;

    public static ChatResponse success(String answer) {
        return ChatResponse.builder()
                .success(true)
                .answer(answer)
                .build();
    }

    public static ChatResponse error(String error) {
        return ChatResponse.builder()
                .success(false)
                .error(error)
                .build();
    }
}