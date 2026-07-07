package com.example.platform.modules.chatlog.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.modules.chatlog.dto.ChatRequest;
import com.example.platform.modules.chatlog.dto.ChatResponse;
import com.example.platform.orchestration.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final OrchestratorService orchestratorService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ChatResponse> chatJson(@RequestBody ChatRequest request) {
        return ApiResponse.ok(orchestratorService.chat(request));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ChatResponse> chatMultipart(@ModelAttribute ChatRequest request) {
        return ApiResponse.ok(orchestratorService.chat(request));
    }
}