package com.example.platform.modules.chatlog.controller;

import com.example.platform.modules.chatlog.dto.FeedbackRequest;
import com.example.platform.modules.chatlog.model.Feedback;
import com.example.platform.modules.chatlog.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService service;

    @PostMapping
    public Feedback submit(
            @RequestBody FeedbackRequest request
    ) {

        return service.submitFeedback(
                request
        );

    }

}