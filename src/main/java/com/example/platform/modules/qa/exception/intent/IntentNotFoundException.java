package com.example.platform.modules.qa.exception.intent;

/**
 * Represents a not-found error for intent resources.
 * Provides factory methods for id-based and name-based lookups.
 *
 * <p>Used by IntentReadService when no matching intent exists.
 * DB: intents | Phase 1: Read only | Error type: RuntimeException</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.IntentReadService
 */
public class IntentNotFoundException extends RuntimeException {
    /**
     * Create exception with a custom message.
     */
    public IntentNotFoundException(String message) {
        super(message);
    }

    /**
     * Factory for missing intent by id.
     */
    public static IntentNotFoundException byId(Integer id) {
        throw new IntentNotFoundException("intent with id " + id + " not Found");
    }

    /**
     * Factory for missing intent by name.
     */
    public static IntentNotFoundException byName(String name) {
        throw new IntentNotFoundException("intent with name " + name + " not Found");
    }
}
