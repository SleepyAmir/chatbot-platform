package com.example.platform.modules.chatlog.controller;

import com.example.platform.modules.chatlog.document.ChatSession;
import com.example.platform.modules.chatlog.document.MessageDoc;
import com.example.platform.modules.chatlog.dto.AddMessageRequest;
import com.example.platform.modules.chatlog.dto.CreateSessionRequest;
import com.example.platform.modules.chatlog.service.ChatSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-sessions")
@RequiredArgsConstructor
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    @PostMapping
    public ChatSession createSession(@Valid @RequestBody CreateSessionRequest request) {
        ChatSession session = chatSessionService.createSession(request.getUserId());
        System.out.println("New chat session created: " + session.getSessionId());
        return session;
    }
    @PostMapping("/{sessionId}/messages")
    public ChatSession addMessage(
            @PathVariable String sessionId,
            @Valid @RequestBody AddMessageRequest request
    ) {
        return chatSessionService.addMessage(
                sessionId,
                request.getRole(),
                request.getContent()
        );
    }

    @GetMapping("/{sessionId}/history")
    public List<MessageDoc> getHistory(@PathVariable String sessionId) {
        return chatSessionService.getHistory(sessionId);
    }


}