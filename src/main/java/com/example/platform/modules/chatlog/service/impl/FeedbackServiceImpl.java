package com.example.platform.modules.chatlog.service.impl;

import com.example.platform.modules.chatlog.dto.request.FeedbackRequest;
import com.example.platform.modules.chatlog.dto.response.FeedbackResponse;
import com.example.platform.modules.chatlog.mapper.FeedbackMapper;
import com.example.platform.modules.chatlog.model.ChatLog;
import com.example.platform.modules.chatlog.model.Feedback;
import com.example.platform.modules.chatlog.repository.FeedbackRepository;
import com.example.platform.modules.chatlog.service.ChatLogService;
import com.example.platform.modules.chatlog.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackServiceImpl implements FeedbackService {

    private static final Set<Short> ALLOWED_RATINGS = Set.of((short) 1, (short) -1);

    private final FeedbackRepository feedbackRepository;
    private final ChatLogService chatLogService;
    private final FeedbackMapper feedbackMapper;

    @Override
    @Transactional
    public FeedbackResponse submitFeedback(FeedbackRequest request) {
        validateRating(request.rating());

        // Throws ResourceNotFoundException (-> 404) if the log doesn't exist,
        // instead of letting the INSERT fail with a raw FK violation.
        ChatLog chatLog = chatLogService.getRequiredLogEntity(request.logId());

        Feedback feedback = feedbackMapper.toEntity(request, chatLog, OffsetDateTime.now());
        Feedback savedFeedback = feedbackRepository.save(feedback);

        log.info("Saved feedback id={} for chatLogId={} rating={}",
                savedFeedback.getId(), request.logId(), request.rating());

        return feedbackMapper.toResponse(savedFeedback);
    }

    @Override
    public List<FeedbackResponse> getFeedbackForLog(Integer logId) {
        // Ensures a clean 404 for an unknown logId instead of silently
        // returning an empty list.
        chatLogService.getRequiredLogEntity(logId);

        return feedbackRepository.findByLogIdOrderByCreatedAtDesc(logId)
                .stream()
                .map(feedbackMapper::toResponse)
                .toList();
    }

    private void validateRating(Short rating) {
        if (!ALLOWED_RATINGS.contains(rating)) {
            throw new IllegalArgumentException("Rating must be either 1 (positive) or -1 (negative)");
        }
    }
}
