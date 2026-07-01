package com.example.platform.modules.qa.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.modules.qa.dto.response.QaEmbeddingResponse;
import com.example.platform.modules.qa.service.QaEmbeddingReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qa-embeddings")
public class QaEmbeddingController {

    private final QaEmbeddingReadService qaEmbeddingReadService;

    @GetMapping("/all")
    public ApiResponse<List<QaEmbeddingResponse>> getAllEmbeddings() {
        return ApiResponse.ok(qaEmbeddingReadService.getAllEmbeddings());
    }

    @GetMapping("/{id}")
    public ApiResponse<QaEmbeddingResponse> getEmbeddingById(@PathVariable Integer id) {
        return ApiResponse.ok(qaEmbeddingReadService.getEmbeddingById(id));
    }

    @GetMapping("/by-qa/{qaId}")
    public ApiResponse<QaEmbeddingResponse> getEmbeddingByQaId(@PathVariable Integer qaId) {
        return ApiResponse.ok(qaEmbeddingReadService.getEmbeddingByQaId(qaId));
    }

    @GetMapping("/by-model")
    public ApiResponse<List<QaEmbeddingResponse>> getEmbeddingsByModelName(@RequestParam String modelName) {
        return ApiResponse.ok(qaEmbeddingReadService.getEmbeddingsByModelName(modelName));
    }

    @GetMapping("/count")
    public ApiResponse<Long> countEmbeddings() {
        return ApiResponse.ok(qaEmbeddingReadService.countEmbeddings());
    }

    @GetMapping("/exists")
    public ApiResponse<Boolean> existsByQaId(@RequestParam Integer qaId) {
        return ApiResponse.ok(qaEmbeddingReadService.existsByQaId(qaId));
    }
}
