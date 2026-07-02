package com.example.platform.modules.chatlog.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ChatLogRequest(

        @NotBlank(message = "Session id is required")
        String sessionId,

        @NotBlank(message = "User question is required")
        String userQuestion,

        /**
         * Optional id of the QA pair the pipeline matched this question to.
         * Null when nothing matched above the confidence threshold.
         */
        Integer matchedQaId,

        @NotBlank(message = "Answer returned is required")
        String answerReturned,

        @DecimalMin(value = "0.0", message = "Confidence must be between 0 and 1")
        @DecimalMax(value = "1.0", message = "Confidence must be between 0 and 1")
        Float confidence,

        String modelUsed,

        @PositiveOrZero(message = "Response time cannot be negative")
        Integer responseTimeMs
) {
}
