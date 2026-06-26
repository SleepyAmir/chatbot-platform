package com.example.platform.modules.career.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CareerRequirementRequest(
        @NotNull(message = "Chunk index is required")
        @Min(value = 0, message = "Chunk index must be zero or greater")
        Integer chunkIndex,
        @NotBlank(message = "Requirement text is required")
        String requirementText,
        @Size(min = 384, max = 384, message = "Embedding must contain exactly 384 dimensions")
        List<Float> embedding
) {
}
