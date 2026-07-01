package com.example.platform.modules.qa.exception.qa_embedding;

/**
 * Represents a not-found error for QA embedding resources.
 * Provides factory methods for embedding id and QA pair id lookups.
 *
 * <p>Used by QaEmbeddingReadService when embedding metadata is missing.
 * DB: qa_embeddings | Phase 1: Read only | Error type: RuntimeException</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.QaEmbeddingReadService
 */
public class QaEmbeddingNotFoundException extends RuntimeException {

    /**
     * Create exception with a custom message.
     */
    private QaEmbeddingNotFoundException(String message) {
        super(message);
    }

    /**
     * Factory for missing embedding by id.
     */
    public static QaEmbeddingNotFoundException byId(Integer id) {
        return new QaEmbeddingNotFoundException("QaEmbedding not found with id: " + id);
    }

    /**
     * Factory for missing embedding by QA pair id.
     */
    public static QaEmbeddingNotFoundException byQaId(Integer qaId) {
        return new QaEmbeddingNotFoundException("QaEmbedding not found for QA pair id: " + qaId);
    }
}