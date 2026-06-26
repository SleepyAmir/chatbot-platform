package com.example.platform.modules.qa.dto.response;

public record QaIntentResponse(
        Integer qaId,
        String question,
        Integer intentId,
        String intentName
) {
}