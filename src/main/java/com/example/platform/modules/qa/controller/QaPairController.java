package com.example.platform.modules.qa.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.modules.qa.dto.response.IntentResponse;
import com.example.platform.modules.qa.dto.response.QaPairDetailResponse;
import com.example.platform.modules.qa.dto.response.QaPairResponse;
import com.example.platform.modules.qa.service.QaPairReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qa")
public class QaPairController {

    private final QaPairReadService qaPairReadService;

    @GetMapping("/all")
    public ApiResponse<List<QaPairResponse>> getAllQaPairs() {
        return ApiResponse.ok(qaPairReadService.getAllQaPairs());
    }

    @GetMapping("/{id}")
    public ApiResponse<QaPairResponse> getQaPairById(@PathVariable Integer id) {
        return ApiResponse.ok(qaPairReadService.getQaPairById(id));
    }

    @GetMapping("/{id}/detail")
    public ApiResponse<QaPairDetailResponse> getQaPairDetailById(@PathVariable Integer id) {
        return ApiResponse.ok(qaPairReadService.getQaPairDetailById(id));
    }

    @GetMapping("/{id}/intents")
    public ApiResponse<List<IntentResponse>> getIntentsByQaId(@PathVariable Integer id) {
        return ApiResponse.ok(qaPairReadService.getIntentsByQaId(id));
    }

    @GetMapping
    public ApiResponse<List<QaPairResponse>> searchQaPairs(
            @RequestParam(required = false) String keyword
    ) {
        List<QaPairResponse> result = (keyword == null || keyword.isBlank())
                ? qaPairReadService.getAllQaPairs()
                : qaPairReadService.searchQaPairs(keyword);
        return ApiResponse.ok(result);
    }

    @GetMapping("/by-course/{courseId}")
    public ApiResponse<List<QaPairResponse>> getQaPairsByCourseId(@PathVariable Integer courseId) {
        return ApiResponse.ok(qaPairReadService.getQaPairsByCourseId(courseId));
    }

    @GetMapping("/by-intent")
    public ApiResponse<List<QaPairResponse>> getQaPairsByIntentName(@RequestParam String name) {
        return ApiResponse.ok(qaPairReadService.getQaPairsByIntentName(name));
    }

    @GetMapping("/count")
    public ApiResponse<Long> countQaPairs() {
        return ApiResponse.ok(qaPairReadService.countQaPairs());
    }

    @GetMapping("/exists")
    public ApiResponse<Boolean> existsByQuestion(@RequestParam String question) {
        return ApiResponse.ok(qaPairReadService.existsByQuestion(question));
    }

}
