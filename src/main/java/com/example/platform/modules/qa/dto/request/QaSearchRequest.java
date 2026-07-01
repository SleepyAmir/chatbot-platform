package com.example.platform.modules.qa.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Represents the request body for semantic QA search.
 * Contains the query embedding and optional search tuning parameters.
 *
 * <p>Used by future QA search endpoints to call vector search.
 * Phase 1: Read/Search only | Embedding dimension: 384</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.dto.response.QaSearchResponse
 */
public record QaSearchRequest(

        @NotNull(message = "Embedding is required")
        String embedding,

        @Positive(message = "topK must be positive")
        Integer topK,

        @DecimalMin(value = "0.0", message = "minSimilarity must be at least 0.0")
        @DecimalMax(value = "1.0", message = "minSimilarity must be at most 1.0")
        Double minSimilarity
) {
}
