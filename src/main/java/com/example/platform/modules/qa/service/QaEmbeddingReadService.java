package com.example.platform.modules.qa.service;

import com.example.platform.modules.qa.dto.response.QaEmbeddingResponse;

import java.util.List;


/**
 * Defines read-only operations for QA embedding metadata.
 * Covers listing, lookup by id, lookup by QA id, filtering, and counts.
 *
 * <p>Implemented by the embedding read service layer.
 * Module: QA/Embedding | Phase 1: Read only | DTO: QaEmbeddingResponse</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.dto.response.QaEmbeddingResponse
 */
public interface QaEmbeddingReadService {

    /**
     * Get all embeddings (non-paginated).
     */
    List<QaEmbeddingResponse> getAllEmbeddings();

    /**
     * Get an embedding by ID.
     */
    QaEmbeddingResponse getEmbeddingById(Integer id);

    /**
     * Get an embedding by QA pair ID.
     */
    QaEmbeddingResponse getEmbeddingByQaId(Integer qaId);

    /**
     * Get embeddings by model name.
     */
    List<QaEmbeddingResponse> getEmbeddingsByModelName(String modelName);

    /**
     * Check if an embedding exists for a QA pair.
     */
    boolean existsByQaId(Integer qaId);

    /**
     * Count total embeddings.
     */
    long countEmbeddings();
}
