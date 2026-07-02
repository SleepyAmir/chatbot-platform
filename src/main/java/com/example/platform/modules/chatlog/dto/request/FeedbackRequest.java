package com.example.platform.modules.chatlog.dto.request;

import jakarta.validation.constraints.NotNull;

public record FeedbackRequest(

        @NotNull(message = "logId is required")
        Integer logId,

        /**
         * Must be -1 or 1 - checked in the service layer (see
         * FeedbackServiceImpl) to mirror the DB's chk_feedback_rating constraint
         * with a clear error message instead of a raw DataIntegrityViolationException.
         */
        @NotNull(message = "rating is required")
        Short rating,

        String comment
) {
}
