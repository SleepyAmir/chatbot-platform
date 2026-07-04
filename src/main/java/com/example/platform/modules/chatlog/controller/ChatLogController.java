package com.example.platform.modules.chatlog.controller;

import com.example.platform.modules.chatlog.model.ChatLog;
import com.example.platform.modules.chatlog.service.ChatLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class ChatLogController {

    private final ChatLogService service;

    @GetMapping
    public Page<ChatLog> logs(
            Pageable pageable
    ) {

        return service.getRecentLogs(
                pageable
        );

    }

    @GetMapping("/{id}")
    public ChatLog get(
            @PathVariable Long id
    ) {

        return service.getLogById(id);

    }

}