package com.example.platform.modules.qa.dto.response;

public record QaEmbeddingResponse(
        Integer id,
        Integer qaId,
        String modelName,
        Boolean hasEmbedding,
        Integer dimension
) {
}