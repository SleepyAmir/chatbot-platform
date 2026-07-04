package com.example.platform.modules.chatlog.service;

import com.example.platform.modules.chatlog.dto.FeedbackRequest;
import com.example.platform.modules.chatlog.model.Feedback;

public interface FeedbackService {

    Feedback submitFeedback(
            FeedbackRequest request
    );

}