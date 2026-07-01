package com.example.platform.modules.qa.repository;

/**
 * Represents the projection returned by the semantic QA search query.
 * Mirrors the columns returned from the PostgresSQL search_qa function.
 *
 * <p>Used to map native search results into QaSearchResponse.
 * DB function: search_qa | Phase 1: Search support | Score: similarity</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.dto.response.QaSearchResponse
 */
public interface QaSearchProjection {

    /**
     * QA pair id returned by search_qa.
     */
    Integer getQaId();

    /**
     * Matched question text.
     */
    String getQuestion();

    /**
     * Prepared answer text.
     */
    String getAnswer();

    /**
     * Optional related course id.
     */
    Integer getCourseId();

    /**
     * Cosine similarity score.
     */
    Double getSimilarity();

    /**
     * Embedding model that produced the vector.
     */
    String getModelName();
}
