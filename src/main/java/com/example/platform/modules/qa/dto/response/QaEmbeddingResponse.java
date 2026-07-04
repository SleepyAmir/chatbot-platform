package com.example.platform.modules.qa.dto.response;

/**
 * Represents metadata for a stored QA embedding.
 * Hides the full vector and returns only safe embedding information.
 *
 * <p>Used by embedding endpoints and QA detail responses.
 * DB: qa_embeddings | Phase 1: Read only | Vector dimension: 384</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.model.QaEmbedding
 */
public record QaEmbeddingResponse(
        Integer id,
        Integer qaId,
        String modelName,
        Boolean hasEmbedding,
        Integer dimension
) {
}