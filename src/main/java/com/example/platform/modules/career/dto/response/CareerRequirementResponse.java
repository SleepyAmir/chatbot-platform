package com.example.platform.modules.career.dto.response;

import java.time.OffsetDateTime;

public record CareerRequirementResponse(
        Integer id,
        Integer careerId,
        Integer chunkIndex,
        String requirementText,
        boolean hasEmbedding,
        OffsetDateTime createdAt
) {
}
