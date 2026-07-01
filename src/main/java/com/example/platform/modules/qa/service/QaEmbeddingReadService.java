package com.example.platform.modules.qa.service;

import com.example.platform.modules.qa.dto.response.QaEmbeddingResponse;

import java.util.List;


/**
 * Read operations for QaEmbedding module.
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
