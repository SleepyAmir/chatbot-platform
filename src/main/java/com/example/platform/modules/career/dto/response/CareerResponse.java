package com.example.platform.modules.career.dto.response;

import java.time.OffsetDateTime;

public record CareerResponse(
        Integer id,
        String title,
        String description,
        String sourceUrl,
        OffsetDateTime createdAt
) {
}
