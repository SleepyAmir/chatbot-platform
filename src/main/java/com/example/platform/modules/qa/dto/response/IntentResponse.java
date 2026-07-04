package com.example.platform.modules.qa.dto.response;

/**
 * Represents a lightweight response for an intent category.
 * Contains only the intent identifier and display name.
 *
 * <p>Used by intent list, detail, and QA detail responses.
 * DB: intents | Phase 1: Read only | PK: id (Integer)</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.model.Intent
 */
public record IntentResponse(
        Integer id,
        String name
) {
}