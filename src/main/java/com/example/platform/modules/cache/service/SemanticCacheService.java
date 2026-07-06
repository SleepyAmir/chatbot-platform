package com.example.platform.modules.cache.service;

import java.util.Optional;

public interface SemanticCacheService {
    Optional<String> getCachedAnswer(Integer qaId);

    void cacheAnswer(Integer qaId, String intent, String question, String answer);

    /**
     * True semantic cache lookup: embeds {@code question} and compares it
     * against every cached entry's stored embedding (cosine similarity),
     * not an exact-text or qaId match. Used by OrchestratorService as the
     * very first step of the pipeline, before intent detection even runs -
     * so unlike {@link #getCachedAnswer(Integer)} it doesn't require already
     * knowing which qa_pair the question matches.
     *
     * @return the cached answer if a sufficiently similar question was found, else null.
     */
    String find(String question);

    /**
     * Stores {@code question}'s embedding alongside {@code answer} for future
     * {@link #find(String)} lookups.
     */
    void save(String question, String answer);
}
