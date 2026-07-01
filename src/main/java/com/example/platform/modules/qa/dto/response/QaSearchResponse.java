package com.example.platform.modules.qa.dto.response;

/**
 * Represents a candidate QA result returned from semantic search.
 * Includes the matched QA data and similarity score.
 *
 * <p>Used by the future orchestrator to choose the best chatbot answer.
 * Source: search_qa function | Phase 1: Search output | Score: cosine similarity</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.repository.QaSearchProjection
 */
public record QaSearchResponse(
        Integer qaId,
        String question,
        String answer,
        Integer courseId,
        String courseName,
        Double similarity,
        String modelName
) {
}