package com.example.platform.modules.chatlog.service;

import com.example.platform.modules.chatlog.dto.ChatLogRequest;
import com.example.platform.modules.chatlog.model.ChatLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatLogService {

    ChatLog saveLog(
            ChatLogRequest request
    );

    ChatLog getLogById(
            Long id
    );

    Page<ChatLog> getRecentLogs(
            Pageable pageable
    );

}