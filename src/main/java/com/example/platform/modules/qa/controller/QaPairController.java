package com.example.platform.modules.qa.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.modules.qa.dto.response.IntentResponse;
import com.example.platform.modules.qa.dto.response.QaPairDetailResponse;
import com.example.platform.modules.qa.dto.response.QaPairResponse;
import com.example.platform.modules.qa.service.QaPairReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes read-only API endpoints for QA pair resources.
 * Handles listing, lookup, detail view, keyword search, and filters.
 *
 * <p>Used by clients and future chatbot flow to read prepared answers.
 * Base path: /api/qa | Phase 1: Read/Search only | DB: qa_pairs</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.QaPairReadService
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qa")
public class QaPairController {

    private final QaPairReadService qaPairReadService;

    /**
     * Return all QA pairs without pagination.
     */
    @GetMapping("/all")
    public ApiResponse<List<QaPairResponse>> getAllQaPairs() {
        return ApiResponse.ok(qaPairReadService.getAllQaPairs());
    }

    /**
     * Return one QA pair by id.
     */
    @GetMapping("/{id}")
    public ApiResponse<QaPairResponse> getQaPairById(@PathVariable Integer id) {
        return ApiResponse.ok(qaPairReadService.getQaPairById(id));
    }

    /**
     * Return QA pair with intents and embedding metadata.
     */
    @GetMapping("/{id}/detail")
    public ApiResponse<QaPairDetailResponse> getQaPairDetailById(@PathVariable Integer id) {
        return ApiResponse.ok(qaPairReadService.getQaPairDetailById(id));
    }

    /**
     * Return intents assigned to a QA pair.
     */
    @GetMapping("/{id}/intents")
    public ApiResponse<List<IntentResponse>> getIntentsByQaId(@PathVariable Integer id) {
        return ApiResponse.ok(qaPairReadService.getIntentsByQaId(id));
    }

    /**
     * Search QA pairs; blank keyword falls back to all pairs.
     */
    @GetMapping
    public ApiResponse<List<QaPairResponse>> searchQaPairs(
            @RequestParam(required = false) String keyword
    ) {
        List<QaPairResponse> result = (keyword == null || keyword.isBlank())
                ? qaPairReadService.getAllQaPairs()
                : qaPairReadService.searchQaPairs(keyword);
        return ApiResponse.ok(result);
    }

    /**
     * Return QA pairs related to a course.
     */
    @GetMapping("/by-course/{courseId}")
    public ApiResponse<List<QaPairResponse>> getQaPairsByCourseId(@PathVariable Integer courseId) {
        return ApiResponse.ok(qaPairReadService.getQaPairsByCourseId(courseId));
    }

    /**
     * Return QA pairs related to an intent name.
     */
    @GetMapping("/by-intent")
    public ApiResponse<List<QaPairResponse>> getQaPairsByIntentName(@RequestParam String name) {
        return ApiResponse.ok(qaPairReadService.getQaPairsByIntentName(name));
    }

    /**
     * Return total number of QA pairs.
     */
    @GetMapping("/count")
    public ApiResponse<Long> countQaPairs() {
        return ApiResponse.ok(qaPairReadService.countQaPairs());
    }

    /**
     * Check whether a question already exists.
     */
    @GetMapping("/exists")
    public ApiResponse<Boolean> existsByQuestion(@RequestParam String question) {
        return ApiResponse.ok(qaPairReadService.existsByQuestion(question));
    }

}
