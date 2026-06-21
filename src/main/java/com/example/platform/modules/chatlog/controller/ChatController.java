package com.example.platform.modules.chatlog.controller;

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

    /**
     * For normal JSON chat requests.
     *
     * Example:
     * POST /api/chat
     * Content-Type: application/json
     *
     * {
     *   "question": "قیمت کلاس جاوا چقدر است؟"
     * }
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponse chatJson(@RequestBody ChatRequest request) {
        return orchestratorService.chat(request);
    }

    /**
     * For multipart requests with optional file.
     *
     * Example:
     * POST /api/chat
     * Content-Type: multipart/form-data
     *
     * fields:
     * - question: "این تصویر مربوط به چه کلاسی است؟"
     * - file: image/pdf file
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ChatResponse chatMultipart(@ModelAttribute ChatRequest request) {
        return orchestratorService.chat(request);
    }
}
