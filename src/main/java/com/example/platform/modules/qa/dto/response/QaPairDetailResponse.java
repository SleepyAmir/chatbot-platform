package com.example.platform.modules.qa.dto.response;

import java.util.List;

/**
 * Represents the detailed response for a QA pair.
 * Includes course summary, assigned intents, and embedding metadata.
 *
 * <p>Used when clients need the full QA context.
 * DB: qa_pairs | Phase 1: Read only | Includes related QA module data</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.dto.response.QaPairResponse
 */
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