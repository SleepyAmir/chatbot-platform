package com.example.platform.modules.career.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CourseCareerRequest(
        @NotNull(message = "Career id is required")
        Integer careerId,
        @NotNull(message = "Relevance is required")
        @DecimalMin(value = "0.0", message = "Relevance must be between 0 and 1")
        @DecimalMax(value = "1.0", message = "Relevance must be between 0 and 1")
        Float relevance
) {
}
