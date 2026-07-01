package com.example.platform.modules.qa.service;

import com.example.platform.modules.qa.dto.response.IntentResponse;

import java.util.List;

/**
 * Read operations for Intent module.
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