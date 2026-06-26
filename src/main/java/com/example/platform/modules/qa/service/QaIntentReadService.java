package com.example.platform.modules.qa.service;

import com.example.platform.modules.qa.dto.response.QaIntentResponse;
import com.example.platform.modules.qa.dto.response.QaPairResponse;

import java.util.List;

/**
 * Read operations for QaIntent module.
 */
public interface QaIntentReadService {

    // ==================== QA-INTENT RELATIONSHIPS ====================
    /**
     * Get all QA-intent relationships (non-paginated).
     */
    List<QaIntentResponse> getAllQaIntents();

    /**
     * Get all intents for a specific QA pair.
     */
    List<QaIntentResponse> getIntentsByQaId(Integer qaId);

    /**
     * Get all QA pairs for a specific intent.
     */
    List<QaPairResponse> getQaPairsByIntentId(Integer intentId);

    // ==================== UTILITY ====================

    /**
     * Check if a QA-intent relationship exists.
     */
    boolean existsByQaIdAndIntentId(Integer qaId, Integer intentId);

    /**
     * Count total QA-intent relationships.
     */
    long countQaIntents();

    /**
     * Count QA pairs for a specific intent.
     */
    long countQaPairsByIntentId(Integer intentId);

    /**
     * Count intents for a specific QA pair.
     */
    long countIntentsByQaId(Integer qaId);
}