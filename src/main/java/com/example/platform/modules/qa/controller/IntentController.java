package com.example.platform.modules.qa.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.modules.qa.dto.response.IntentResponse;
import com.example.platform.modules.qa.service.IntentReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/intents")
public class IntentController {

    private final IntentReadService intentReadService;

    @GetMapping("/all")
    public ApiResponse<List<IntentResponse>> getAllIntents() {
        return ApiResponse.ok(intentReadService.getAllIntents());
    }

    @GetMapping("/{id}")
    public ApiResponse<IntentResponse> getIntentById(@PathVariable Integer id) {
        return ApiResponse.ok(intentReadService.getIntentById(id));
    }

    @GetMapping("/by-name")
    public ApiResponse<IntentResponse> getIntentByName(@RequestParam String name) {
        return ApiResponse.ok(intentReadService.getIntentByName(name));
    }

    @GetMapping
    public ApiResponse<List<IntentResponse>> searchIntents(
            @RequestParam(required = false) String keyword
    ) {
        List<IntentResponse> result = (keyword == null || keyword.isBlank())
                ? intentReadService.getAllIntents()
                : intentReadService.searchIntents(keyword);
        return ApiResponse.ok(result);
    }

    @GetMapping("/count")
    public ApiResponse<Long> countIntents() {
        return ApiResponse.ok(intentReadService.countIntents());
    }

    @GetMapping("/exists")
    public ApiResponse<Boolean> existsByName(@RequestParam String name) {
        return ApiResponse.ok(intentReadService.existsByName(name));
    }

}