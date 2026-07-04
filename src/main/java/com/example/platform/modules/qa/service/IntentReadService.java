package com.example.platform.modules.qa.service;

import com.example.platform.modules.qa.dto.response.IntentResponse;

import java.util.List;

/**
 * Defines read-only operations for intent resources.
 * Covers listing, lookup, search, count, and existence checks.
 *
 * <p>Implemented by the intent read service layer.
 * Module: QA/Intent | Phase 1: Read only | DTO: IntentResponse</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.dto.response.IntentResponse
 */
public interface IntentReadService {

    /**
     * Get all intents (non-paginated).
     */
    List<IntentResponse> getAllIntents();

    /**
     * Get an intent by ID.
     */
    IntentResponse getIntentById(Integer id);

    /**
     * Get an intent by exact name (case-insensitive).
     */
    IntentResponse getIntentByName(String name);

    /**
     * Search intents by keyword (non-paginated).
     */
    List<IntentResponse> searchIntents(String keyword);

    /**
     * Count total number of intents.
     */
    long countIntents();

    /**
     * Check if an intent exists by name.
     */
    boolean existsByName(String name);
}