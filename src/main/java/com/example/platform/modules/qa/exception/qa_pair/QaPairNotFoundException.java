package com.example.platform.modules.qa.exception.qa_pair;

/**
 * Represents a not-found error for QA pair resources.
 * Provides a factory method for id-based lookup failures.
 *
 * <p>Used by QaPairReadService when no QA pair matches the requested id.
 * DB: qa_pairs | Phase 1: Read only | Error type: RuntimeException</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.QaPairReadService
 */
public class QaPairNotFoundException extends RuntimeException {

    /**
     * Create exception with a custom message.
     */
    private QaPairNotFoundException(String message) {
        super(message);
    }

    /**
     * Factory for missing QA pair by id.
     */
    public static QaPairNotFoundException byId(Integer id) {
        return new QaPairNotFoundException("QaPair not found with id: " + id);
    }
}