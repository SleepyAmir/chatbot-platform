package com.example.platform.modules.chatlog.service;

import com.example.platform.modules.chatlog.dto.request.FeedbackRequest;
import com.example.platform.modules.chatlog.dto.response.FeedbackResponse;

import java.util.List;

public interface FeedbackService {

    FeedbackResponse submitFeedback(FeedbackRequest request);

    List<FeedbackResponse> getFeedbackForLog(Integer logId);
}
