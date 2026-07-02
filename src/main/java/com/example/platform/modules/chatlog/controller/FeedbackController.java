package com.example.platform.modules.chatlog.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.modules.chatlog.dto.request.FeedbackRequest;
import com.example.platform.modules.chatlog.dto.response.FeedbackResponse;
import com.example.platform.modules.chatlog.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FeedbackResponse> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        return ApiResponse.ok("Feedback submitted successfully", feedbackService.submitFeedback(request));
    }

    @GetMapping("/log/{logId}")
    public ApiResponse<List<FeedbackResponse>> getFeedbackForLog(@PathVariable Integer logId) {
        return ApiResponse.ok(feedbackService.getFeedbackForLog(logId));
    }
}
