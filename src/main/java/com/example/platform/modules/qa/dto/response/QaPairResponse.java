package com.example.platform.modules.qa.dto.response;

public record QaPairResponse(
        Integer id,
        String question,
        String answer,
        Integer courseId,
        String courseName
) {
}