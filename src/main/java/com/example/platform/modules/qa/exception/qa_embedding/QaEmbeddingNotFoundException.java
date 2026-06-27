package com.example.platform.modules.qa.exception.qa_embedding;

public class QaEmbeddingNotFoundException extends RuntimeException {

    private QaEmbeddingNotFoundException(String message) {
        super(message);
    }

    public static QaEmbeddingNotFoundException byId(Integer id) {
        return new QaEmbeddingNotFoundException("QaEmbedding not found with id: " + id);
    }

    public static QaEmbeddingNotFoundException byQaId(Integer qaId) {
        return new QaEmbeddingNotFoundException("QaEmbedding not found for QA pair id: " + qaId);
    }
}