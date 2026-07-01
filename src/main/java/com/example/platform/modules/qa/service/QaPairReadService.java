package com.example.platform.modules.qa.service;

import com.example.platform.modules.qa.dto.response.IntentResponse;
import com.example.platform.modules.qa.dto.response.QaPairDetailResponse;
import com.example.platform.modules.qa.dto.response.QaPairResponse;

import java.util.List;

/**
 * Defines read-only operations for QA pair resources.
 * Covers list, detail, keyword search, filtering, count, and existence checks.
 *
 * <p>Implemented by the QA pair read service layer.
 * Module: QA Pair | Phase 1: Read/Search only | DTO: QaPairResponse</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.dto.response.QaPairResponse
 */
public interface QaPairReadService {

    // ==================== BASIC CRUD ====================

    /**
     * Get all QA pairs (non-paginated).
     */
    List<QaPairResponse> getAllQaPairs();

    /**
     * Get a single QA pair by ID.
     */
    QaPairResponse getQaPairById(Integer id);

    /**
     * Get QA pair details with intents and embedding metadata.
     */
    QaPairDetailResponse getQaPairDetailById(Integer id);

    // ==================== SEARCH ====================
    /**
     * Search QA pairs by keyword (non-paginated).
     */

    List<QaPairResponse> searchQaPairs(String keyword);

    // ==================== FILTERING ====================

    /**
     * Get QA pairs by course ID.
     */
    List<QaPairResponse> getQaPairsByCourseId(Integer courseId);

    /**
     * Get QA pairs by intent name.
     */
    List<QaPairResponse> getQaPairsByIntentName(String intentName);

    // ==================== UTILITY ====================

    /**
     * Count total QA pairs.
     */
    long countQaPairs();

    /**
     * Check if a QA pair exists by question.
     */
    boolean existsByQuestion(String question);

    /**
     * Get intents assigned to a specific QA pair.
     */
    List<IntentResponse> getIntentsByQaId(Integer qaId);
}