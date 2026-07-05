package com.example.platform.modules.chatlog.repository;

import com.example.platform.modules.chatlog.document.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {

    Optional<ChatSession> findBySessionId(String sessionId);
}
