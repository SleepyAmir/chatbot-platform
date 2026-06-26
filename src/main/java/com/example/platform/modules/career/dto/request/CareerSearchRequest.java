package com.example.platform.modules.career.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CareerSearchRequest(
        @NotNull(message = "Embedding is required")
        @Size(min = 384, max = 384, message = "Embedding must contain exactly 384 dimensions")
        List<Float> embedding,
        @Min(value = 1, message = "topK must be at least 1")
        @Max(value = 50, message = "topK cannot exceed 50")
        Integer topK,
        @DecimalMin(value = "0.0", message = "minSimilarity must be between 0 and 1")
        @DecimalMax(value = "1.0", message = "minSimilarity must be between 0 and 1")
        Double minSimilarity
) {
}
