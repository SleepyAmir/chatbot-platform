package com.example.platform.modules.qa.dto.response;

/**
 * Represents the relationship between a QA pair and an intent.
 * Combines QA and intent summary fields in one response.
 *
 * <p>Used by QA-intent endpoints to show classification links.
 * DB: qa_intents | Phase 1: Read only | PK: qa_id + intent_id</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.model.QaIntent
 */
public record QaIntentResponse(
        Integer qaId,
        String question,
        Integer intentId,
        String intentName
) {
}