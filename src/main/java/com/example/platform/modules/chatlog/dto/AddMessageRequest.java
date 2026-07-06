package com.example.platform.modules.chatlog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddMessageRequest {

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "user|assistant", message = "Role must be either 'user' or 'assistant'")
    private String role;

    @NotBlank(message = "Content is required")
    private String content;
}