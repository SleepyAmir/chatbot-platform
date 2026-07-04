package com.example.platform.modules.chatlog.dto.response;

import java.time.OffsetDateTime;

public record ChatLogResponse(
        Integer id,
        String sessionId,
        String userQuestion,
        Integer matchedQaId,
        String matchedQuestion,
        String answerReturned,
        Float confidence,
        String modelUsed,
        Integer responseTimeMs,
        OffsetDateTime createdAt
) {
}
