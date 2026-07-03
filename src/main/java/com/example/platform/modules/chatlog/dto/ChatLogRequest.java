package com.example.platform.modules.chatlog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatLogRequest {

    private String sessionId;

    private String userQuestion;

    private Long matchedQa;

    private String answerReturned;

    private Float confidence;

    private String modelUsed;

    private Long responseTimeMs;

}