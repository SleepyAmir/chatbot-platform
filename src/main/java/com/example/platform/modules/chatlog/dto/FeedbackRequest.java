package com.example.platform.modules.chatlog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequest {

    private Long logId;

    private Short rating;

    private String comment;

}