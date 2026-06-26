package com.example.platform.modules.career.dto.response;

import java.time.OffsetDateTime;

public record CareerSearchResponse(
        Integer requirementId,
        Integer careerId,
        String careerTitle,
        Integer chunkIndex,
        String requirementText,
        Double similarity,
        OffsetDateTime createdAt
) {
}
