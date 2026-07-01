package com.example.platform.modules.qa.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.modules.qa.dto.response.IntentResponse;
import com.example.platform.modules.qa.service.IntentReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes read-only API endpoints for intent resources.
 * Handles listing, lookup, search, count, and existence checks.
 *
 * <p>Used by clients to inspect intent categories.
 * Base path: /api/intents | Phase 1: Read only | Response wrapper: ApiResponse</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.IntentReadService
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/intents")
public class IntentController {

    private final IntentReadService intentReadService;

    /**
     * Return all intents without pagination.
     */
    @GetMapping("/all")
    public ApiResponse<List<IntentResponse>> getAllIntents() {
        return ApiResponse.ok(intentReadService.getAllIntents());
    }

    /**
     * Return one intent by id.
     */
    @GetMapping("/{id}")
    public ApiResponse<IntentResponse> getIntentById(@PathVariable Integer id) {
        return ApiResponse.ok(intentReadService.getIntentById(id));
    }

    /**
     * Return one intent by exact name.
     */
    @GetMapping("/by-name")
    public ApiResponse<IntentResponse> getIntentByName(@RequestParam String name) {
        return ApiResponse.ok(intentReadService.getIntentByName(name));
    }

    /**
     * Search intents; blank keyword falls back to all intents.
     */
    @GetMapping
    public ApiResponse<List<IntentResponse>> searchIntents(
            @RequestParam(required = false) String keyword
    ) {
        List<IntentResponse> result = (keyword == null || keyword.isBlank())
                ? intentReadService.getAllIntents()
                : intentReadService.searchIntents(keyword);
        return ApiResponse.ok(result);
    }

    /**
     * Return total number of intents.
     */
    @GetMapping("/count")
    public ApiResponse<Long> countIntents() {
        return ApiResponse.ok(intentReadService.countIntents());
    }

    /**
     * Check whether an intent name already exists.
     */
    @GetMapping("/exists")
    public ApiResponse<Boolean> existsByName(@RequestParam String name) {
        return ApiResponse.ok(intentReadService.existsByName(name));
    }
}
