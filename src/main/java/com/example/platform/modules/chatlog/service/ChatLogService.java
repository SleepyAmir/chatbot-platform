package com.example.platform.modules.chatlog.service;

import com.example.platform.modules.chatlog.dto.request.ChatLogRequest;
import com.example.platform.modules.chatlog.dto.response.ChatLogResponse;
import com.example.platform.modules.chatlog.model.ChatLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatLogService {

    ChatLogResponse saveLog(ChatLogRequest request);

    ChatLogResponse getLogById(Integer id);

    Page<ChatLogResponse> getRecentLogs(Pageable pageable);

    Page<ChatLogResponse> getLogsBySession(String sessionId, Pageable pageable);

    List<ChatLogResponse> getLogsBySession(String sessionId);

    /**
     * Used internally by FeedbackService to attach feedback to an existing
     * log - throws ResourceNotFoundException (-> 404) if it doesn't exist.
     */
    ChatLog getRequiredLogEntity(Integer id);
}
