package com.example.platform.modules.qa.service;

import com.example.platform.modules.qa.dto.response.IntentResponse;
import com.example.platform.modules.qa.dto.response.QaPairDetailResponse;
import com.example.platform.modules.qa.dto.response.QaPairResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Read operations for QaPair module.
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

    public List<IntentResponse> getIntentsByQaId(Integer qaId);
}