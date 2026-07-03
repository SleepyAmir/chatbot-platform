package com.example.platform.modules.chatlog.service.impl;

import com.example.platform.common.exception.ResourceNotFoundException;
import com.example.platform.modules.chatlog.dto.FeedbackRequest;
import com.example.platform.modules.chatlog.model.ChatLog;
import com.example.platform.modules.chatlog.model.Feedback;
import com.example.platform.modules.chatlog.repository.ChatLogRepository;
import com.example.platform.modules.chatlog.repository.FeedbackRepository;
import com.example.platform.modules.chatlog.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl
        implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    private final ChatLogRepository chatLogRepository;

    @Override
    public Feedback submitFeedback(
            FeedbackRequest request
    ) {

        ChatLog log = chatLogRepository.findById(
                        request.getLogId()
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "ChatLog not found"
                        )
                );

        Feedback feedback = Feedback.builder()
                .log(log)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        return feedbackRepository.save(
                feedback
        );

    }

}