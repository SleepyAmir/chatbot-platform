package com.example.platform.modules.cache.service.impl;

import com.example.platform.modules.cache.dto.CachedQA;
import com.example.platform.modules.cache.service.SemanticCacheService;
import com.example.platform.modules.embedding.service.EmbeddingService;
import com.example.platform.modules.search.service.VectorSimilarityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticCacheServiceImpl implements SemanticCacheService {

    private static final String KEY_PREFIX = "qa-cache:";
    private static final String HIT_RANKING_KEY = "qa-cache:hit-ranking";

    /** Separate namespace from the qaId-keyed entries above - these are keyed by a random id, found by embedding similarity, not by qaId. */
    private static final String SEMANTIC_KEY_PREFIX = "qa-cache:semantic:";
    private static final String SEMANTIC_KEYS_SET = "qa-cache:semantic:keys";

    private final RedisTemplate<String, Object> redisTemplate;
    private final EmbeddingService embeddingService;
    private final VectorSimilarityService similarityService;

    @Value("${semantic.cache.ttl-minutes:5}")
    private long ttlMinutes;

    /** How similar a cached question must be (cosine) to count as the same question. */
    @Value("${semantic.cache.similarity-threshold:0.92}")
    private double similarityThreshold;

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

    @Override
    public String find(String question) {
        if (question == null || question.isBlank()) {
            return null;
        }

        List<Double> queryEmbedding;
        try {
            queryEmbedding = embeddingService.embed(question);
        } catch (Exception e) {
            log.warn("Semantic cache lookup: embedding failed for question='{}'", question, e);
            return null;
        }
        if (queryEmbedding == null || queryEmbedding.isEmpty()) {
            return null;
        }

        Set<Object> rawKeys = redisTemplate.opsForSet().members(SEMANTIC_KEYS_SET);
        if (rawKeys == null || rawKeys.isEmpty()) {
            return null;
        }

// One MGET for every candidate instead of N separate GETs.
        List<String> keyList = rawKeys.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList();
        if (keyList.isEmpty()) {
            return null;
        }
        List<Object> values = redisTemplate.opsForValue().multiGet(keyList);
        if (values == null) {
            return null;
        }

        String bestAnswer = null;
        double bestScore = -1.0;
        String bestKey = null;

        for (int i = 0; i < values.size(); i++) {
            Object raw = values.get(i);
            if (!(raw instanceof CachedQA cached) || cached.getEmbedding() == null) {
                continue;
            }

            double score = similarityService.cosine(queryEmbedding, toDoubleList(cached.getEmbedding()));
            if (score > bestScore) {
                bestScore = score;
                bestAnswer = cached.getAnswer();
                bestKey = keyList.get(i);
            }
        }

        if (bestAnswer == null || bestScore < similarityThreshold) {
            log.debug("Semantic cache MISS. bestScore={} threshold={} question='{}'", bestScore, similarityThreshold, question);
            return null;
        }

        log.info("Semantic cache HIT. score={} question='{}'", bestScore, question);
        redisTemplate.opsForZSet().incrementScore(HIT_RANKING_KEY, bestKey, 1);
        return bestAnswer;
    }

    @Override
    public void save(String question, String answer) {
        if (question == null || question.isBlank() || answer == null || answer.isBlank()) {
            return;
        }

        List<Double> embedding;
        try {
            embedding = embeddingService.embed(question);
        } catch (Exception e) {
            log.warn("Semantic cache save: embedding failed for question='{}'", question, e);
            return;
        }
        if (embedding == null || embedding.isEmpty()) {
            return;
        }

        String id = UUID.randomUUID().toString();
        String key = SEMANTIC_KEY_PREFIX + id;

        CachedQA cachedQA = CachedQA.builder()
                .id(id)
                .question(question)
                .answer(answer)
                .embedding(toFloatList(embedding))
                .createdAt(LocalDateTime.now())
                .hitCount(0L)
                .build();

        redisTemplate.opsForValue().set(key, cachedQA, Duration.ofMinutes(ttlMinutes));
        redisTemplate.opsForSet().add(SEMANTIC_KEYS_SET, key);
        redisTemplate.expire(SEMANTIC_KEYS_SET, Duration.ofMinutes(ttlMinutes));
    }

    private List<Double> toDoubleList(List<Float> floats) {
        List<Double> doubles = new ArrayList<>(floats.size());
        for (Float f : floats) {
            doubles.add(f == null ? 0.0 : f.doubleValue());
        }
        return doubles;
    }

    private List<Float> toFloatList(List<Double> doubles) {
        List<Float> floats = new ArrayList<>(doubles.size());
        for (Double d : doubles) {
            floats.add(d == null ? 0f : d.floatValue());
        }
        return floats;
    }
}
