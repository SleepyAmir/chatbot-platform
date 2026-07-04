package com.example.platform.modules.chatlog.dto.response;

import java.time.OffsetDateTime;

public record FeedbackResponse(
        Integer id,
        Integer logId,
        Short rating,
        String comment,
        OffsetDateTime createdAt
) {
}
