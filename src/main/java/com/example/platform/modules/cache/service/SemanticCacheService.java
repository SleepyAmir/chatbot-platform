package com.example.platform.modules.cache.service;

import java.util.Optional;
/// /
public interface SemanticCacheService {
    Optional<String> getCachedAnswer(Integer qaId);

    void cacheAnswer(Integer qaId, String intent, String question, String answer);
}
