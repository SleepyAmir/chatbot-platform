package com.example.platform.modules.chatlog.repository;

import com.example.platform.modules.chatlog.entity.ChatLog;

import org.springframework.data.domain.*;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatLogRepository
        extends JpaRepository<ChatLog,Long>{

    List<ChatLog> findBySessionId(
            String sessionId
    );

    Page<ChatLog>
    findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );

}