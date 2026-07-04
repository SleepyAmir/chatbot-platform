package com.example.platform.modules.chatlog.repository;

import com.example.platform.modules.chatlog.model.ChatLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogRepository
        extends JpaRepository<ChatLog, Long> {

    Page<ChatLog> findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );

}