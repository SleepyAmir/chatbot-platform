package com.example.platform.modules.qa.dto.response;

import java.util.List;

public record QaPairDetailResponse(
        Integer id,
        String question,
        String answer,
        Integer courseId,
        String courseName,
        List<IntentResponse> intents,
        QaEmbeddingResponse embedding
) {
}