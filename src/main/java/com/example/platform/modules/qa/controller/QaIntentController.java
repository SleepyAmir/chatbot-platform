package com.example.platform.modules.qa.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.modules.qa.dto.response.QaIntentResponse;
import com.example.platform.modules.qa.dto.response.QaPairResponse;
import com.example.platform.modules.qa.service.QaIntentReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qa-intents")
public class QaIntentController {

    private final QaIntentReadService qaIntentReadService;

    @GetMapping("/all")
    public ApiResponse<List<QaIntentResponse>> getAllQaIntents() {
        return ApiResponse.ok(qaIntentReadService.getAllQaIntents());
    }

    @GetMapping("/by-qa/{qaId}")
    public ApiResponse<List<QaIntentResponse>> getIntentsByQaId(@PathVariable Integer qaId) {
        return ApiResponse.ok(qaIntentReadService.getIntentsByQaId(qaId));
    }

    @GetMapping("/by-intent/{intentId}")
    public ApiResponse<List<QaPairResponse>> getQaPairsByIntentId(@PathVariable Integer intentId) {
        return ApiResponse.ok(qaIntentReadService.getQaPairsByIntentId(intentId));
    }

    @GetMapping("/count")
    public ApiResponse<Long> countQaIntents() {
        return ApiResponse.ok(qaIntentReadService.countQaIntents());
    }

    @GetMapping("/exists")
    public ApiResponse<Boolean> existsByQaIdAndIntentId(
            @RequestParam Integer qaId,
            @RequestParam Integer intentId
    ) {
        return ApiResponse.ok(qaIntentReadService.existsByQaIdAndIntentId(qaId, intentId));
    }

    @GetMapping("/count/by-qa/{qaId}")
    public ApiResponse<Long> countIntentsByQaId(@PathVariable Integer qaId) {
        return ApiResponse.ok(qaIntentReadService.countIntentsByQaId(qaId));
    }

    @GetMapping("/count/by-intent/{intentId}")
    public ApiResponse<Long> countQaPairsByIntentId(@PathVariable Integer intentId) {
        return ApiResponse.ok(qaIntentReadService.countQaPairsByIntentId(intentId));
    }

}
