package com.example.platform.modules.chatlog.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChatRequest {


    private String sessionId;

    private String userId;

    private String question;

    private MultipartFile file;
}
