package com.example.platform.modules.cache.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.modules.cache.dto.FrequentQueryDto;
import com.example.platform.modules.cache.service.FrequentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/// /
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final FrequentQueryService frequentQueryService;

    @GetMapping("/top-queries")
    public ApiResponse<List<FrequentQueryDto>> getTopQueries(@RequestParam(defaultValue = "10") int limit) {
        List<FrequentQueryDto> topQueries = frequentQueryService.getTopQueries(limit);
        return ApiResponse.ok(topQueries);
    }

    @DeleteMapping("/flush")
    public ApiResponse<Void> flushCache() {
        frequentQueryService.flushAllCache();
        return ApiResponse.ok("Cache flushed successfully");
    }
}
