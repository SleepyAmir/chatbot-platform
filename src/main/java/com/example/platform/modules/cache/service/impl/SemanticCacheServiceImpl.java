package com.example.platform.modules.cache.service.impl;

import com.example.platform.modules.cache.dto.CachedQA;
import com.example.platform.modules.cache.service.SemanticCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
/// /
@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticCacheServiceImpl implements SemanticCacheService {

    private static final String KEY_PREFIX = "qa-cache:";
    private static final String HIT_RANKING_KEY = "qa-cache:hit-ranking";


    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${semantic.cache.ttl-minutes:5}")
    private long ttlMinutes;


    @Override
    public Optional<String> getCachedAnswer(Integer qaId) {
        String key = KEY_PREFIX + qaId;

        CachedQA cached = (CachedQA) redisTemplate.opsForValue().get(key);
        if (cached == null) {
            return Optional.empty();
        }

        cached.setHitCount(cached.getHitCount() == null ? 1L : cached.getHitCount() + 1);
        redisTemplate.opsForValue().set(key, cached, Duration.ofMinutes(ttlMinutes));

        redisTemplate.opsForZSet().incrementScore(HIT_RANKING_KEY, String.valueOf(qaId), 1);

        return Optional.ofNullable(cached.getAnswer());
    }

    @Override
    public void cacheAnswer(Integer qaId, String intent, String question, String answer) {
        String key = KEY_PREFIX + qaId;

        CachedQA cachedQA = CachedQA.builder()
                .id(String.valueOf(qaId))
                .intent(intent)
                .question(question)
                .answer(answer)
                .createdAt(LocalDateTime.now())
                .hitCount(0L)
                .build();

        redisTemplate.opsForValue().set(key, cachedQA, Duration.ofMinutes(ttlMinutes));
    }
}
