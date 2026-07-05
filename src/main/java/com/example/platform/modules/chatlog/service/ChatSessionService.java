package com.example.platform.modules.chatlog.service;

import com.example.platform.modules.chatlog.document.ChatSession;
import com.example.platform.modules.chatlog.document.MessageDoc;
import com.example.platform.modules.chatlog.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private static final String GUEST_USER_ID = "guest-user";

    private final ChatSessionRepository chatSessionRepository;

    /**
     * userId خالی/null به‌جای رد شدن، به یک کاربر مهمان نگاشت می‌شود — هم‌خوان با
     * رفتار قبلی OrchestratorService وقتی sessionId در درخواست چت ارسال نشده بود.
     * این منطق اینجا متمرکز شده تا هر دو مسیر ورودی (چت بدون session و
     * POST /api/chat-sessions مستقیم) رفتار یکسانی داشته باشند.
     */
    public ChatSession createSession(String userId) {
        String resolvedUserId = (userId == null || userId.isBlank()) ? GUEST_USER_ID : userId;

        Instant now = Instant.now();

        ChatSession session = ChatSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .userId(resolvedUserId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return chatSessionRepository.save(session);
    }

    public ChatSession addMessage(String sessionId, String role, String content) {
        ChatSession session = chatSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        // برای اطمینان ۱۰۰٪ (اگه قبلاً دیتای نال توی دیتابیس بوده)
        if (session.getMessages() == null) {
            session.setMessages(new java.util.ArrayList<>());
        }

        Instant now = Instant.now();
        MessageDoc newMessage = MessageDoc.builder()
                .role(role)
                .content(content)
                .timestamp(now)
                .build();

        session.getMessages().add(newMessage);
        session.setUpdatedAt(now);

        return chatSessionRepository.save(session);
    }


    public List<MessageDoc> getHistory(String sessionId) {
        return chatSessionRepository.findBySessionId(sessionId)
                .map(ChatSession::getMessages)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
    }
}