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

    /** شناسه‌ی یکتای این درخواست — برای پیدا کردن همه‌ی لاگ‌های مربوط به آن (نگاه کنید به StepTimer/MDC traceId). */
    private String traceId;

    /** مدت‌زمان کل پردازش این درخواست در ارکستریشن، به میلی‌ثانیه. */
    private Long elapsedMs;

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