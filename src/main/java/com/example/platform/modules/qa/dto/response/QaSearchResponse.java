package com.example.platform.modules.qa.dto.response;

public record QaSearchResponse(
        Integer qaId,
        String question,
        String answer,
        Integer courseId,
        String courseName,
        Double similarity,
        String modelName
) {
}