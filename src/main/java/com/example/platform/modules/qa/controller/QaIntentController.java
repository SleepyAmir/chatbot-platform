package com.example.platform.modules.qa.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.modules.qa.dto.response.QaIntentResponse;
import com.example.platform.modules.qa.dto.response.QaPairResponse;
import com.example.platform.modules.qa.service.QaIntentReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes read-only API endpoints for QA and intent relationships.
 * Supports browsing links from QA pairs to intents and from intents to QA pairs.
 *
 * <p>Used to inspect the many-to-many classification layer.
 * Base path: /api/qa-intents | Phase 1: Read only | Join table: qa_intents</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.QaIntentReadService
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qa-intents")
public class QaIntentController {

    private final QaIntentReadService qaIntentReadService;

    /**
     * Return all QA-intent relationships.
     */
    @GetMapping("/all")
    public ApiResponse<List<QaIntentResponse>> getAllQaIntents() {
        return ApiResponse.ok(qaIntentReadService.getAllQaIntents());
    }

    /**
     * Return all intents assigned to a QA pair.
     */
    @GetMapping("/by-qa/{qaId}")
    public ApiResponse<List<QaIntentResponse>> getIntentsByQaId(@PathVariable Integer qaId) {
        return ApiResponse.ok(qaIntentReadService.getIntentsByQaId(qaId));
    }

    /**
     * Return all QA pairs assigned to an intent.
     */
    @GetMapping("/by-intent/{intentId}")
    public ApiResponse<List<QaPairResponse>> getQaPairsByIntentId(@PathVariable Integer intentId) {
        return ApiResponse.ok(qaIntentReadService.getQaPairsByIntentId(intentId));
    }

    /**
     * Return total number of QA-intent links.
     */
    @GetMapping("/count")
    public ApiResponse<Long> countQaIntents() {
        return ApiResponse.ok(qaIntentReadService.countQaIntents());
    }

    /**
     * Check whether a specific QA-intent link exists.
     */
    @GetMapping("/exists")
    public ApiResponse<Boolean> existsByQaIdAndIntentId(
            @RequestParam Integer qaId,
            @RequestParam Integer intentId
    ) {
        return ApiResponse.ok(qaIntentReadService.existsByQaIdAndIntentId(qaId, intentId));
    }

    /**
     * Count intents assigned to one QA pair.
     */
    @GetMapping("/count/by-qa/{qaId}")
    public ApiResponse<Long> countIntentsByQaId(@PathVariable Integer qaId) {
        return ApiResponse.ok(qaIntentReadService.countIntentsByQaId(qaId));
    }

    /**
     * Count QA pairs assigned to one intent.
     */
    @GetMapping("/count/by-intent/{intentId}")
    public ApiResponse<Long> countQaPairsByIntentId(@PathVariable Integer intentId) {
        return ApiResponse.ok(qaIntentReadService.countQaPairsByIntentId(intentId));
    }

}
