package com.example.platform.modules.career.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CareerRequest(
        @NotBlank(message = "Career title is required")
        String title,
        String description,
        String sourceUrl
) {
}
