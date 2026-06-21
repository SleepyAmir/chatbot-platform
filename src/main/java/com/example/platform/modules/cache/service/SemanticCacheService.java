package com.example.platform.modules.cache.service;

import com.example.platform.modules.cache.document.CachedQA;
import com.example.platform.modules.embedding.service.EmbeddingService;
import com.example.platform.modules.search.service.VectorSimilarityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticCacheService {

    private static final double THRESHOLD = 0.92;
    private static final String CACHE_KEY_PREFIX = "semantic_cache:";
    private static final String CACHE_KEYS_SET = "semantic_cache:keys";

    private final EmbeddingService embeddingService;
    private final VectorSimilarityService similarityService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public String find(String question) {
        if (question == null || question.isBlank()) {
            log.warn("Skipping cache lookup: blank question");
            return null;
        }

        List<Double> queryEmbedding = embeddingService.embed(question);
        if (queryEmbedding == null || queryEmbedding.isEmpty()) {
            log.warn("Skipping cache lookup: empty embedding for question={}", question);
            return null;
        }

        Set<String> keySet = redisTemplate.opsForSet().members(CACHE_KEYS_SET);
        if (keySet == null || keySet.isEmpty()) {
            log.info("Semantic cache is empty");
            return null;
        }

        // به‌جای N فراخوانی جدای GET (یک رفت‌وبرگشت شبکه به ازای هر آیتم کش)،
        // همه‌ی مقادیر را با یک فراخوانی MGET واحد می‌گیریم. ترتیب پاسخ multiGet
        // دقیقاً با ترتیب ورودی keys مطابقت دارد (مقادیر گم‌شده/منقضی‌شده به‌صورت
        // null در همان اندیس برمی‌گردند)، پس باید از یک List ثابت‌شده از کلیدها
        // استفاده کنیم، نه مستقیماً از Set (که ترتیب تکرارش تضمین‌شده نیست).
        List<String> keys = new ArrayList<>(keySet);
        List<String> jsonValues = redisTemplate.opsForValue().multiGet(keys);

        if (jsonValues == null || jsonValues.isEmpty()) {
            log.info("Semantic cache MGET returned nothing. keys={}", keys.size());
            return null;
        }

        double bestScore = -1.0;
        String bestAnswer = null;
        String bestQuestion = null;

        for (int i = 0; i < keys.size(); i++) {
            String json = jsonValues.get(i);
            if (json == null || json.isBlank()) {
                continue;
            }

            CachedQA cached;
            try {
                cached = objectMapper.readValue(json, CachedQA.class);
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse cache item from redis. key={}", keys.get(i), e);
                continue;
            }

            if (cached.getEmbedding() == null || cached.getEmbedding().isEmpty()) {
                continue;
            }

            if (cached.getEmbedding().size() != queryEmbedding.size()) {
                log.warn("Skipping cache item due to dimension mismatch. key={}", keys.get(i));
                continue;
            }

            double score = similarityService.cosine(queryEmbedding, cached.getEmbedding());

            if (score > bestScore) {
                bestScore = score;
                bestAnswer = cached.getAnswer();
                bestQuestion = cached.getQuestion();
            }
        }

        log.info("Semantic cache lookup completed. bestScore={}, threshold={}, bestQuestion={}",
                bestScore, THRESHOLD, bestQuestion);

        if (bestScore > THRESHOLD && bestAnswer != null && !bestAnswer.isBlank()) {
            log.info("Semantic cache HIT. score={}, matchedQuestion={}", bestScore, bestQuestion);
            return bestAnswer;
        }

        log.info("Semantic cache MISS. bestScore={}", bestScore);
        return null;
    }

    public void save(String question, String answer) {
        if (question == null || question.isBlank()) {
            log.warn("Skipping cache save: blank question");
            return;
        }

        if (answer == null || answer.isBlank()) {
            log.warn("Skipping cache save: blank answer for question={}", question);
            return;
        }

        List<Double> embedding = embeddingService.embed(question);
        if (embedding == null || embedding.isEmpty()) {
            log.warn("Skipping cache save: empty embedding for question={}", question);
            return;
        }

        String id = UUID.randomUUID().toString();
        String key = CACHE_KEY_PREFIX + id;

        CachedQA cached = CachedQA.builder()
                .id(id)
                .question(question)
                .answer(answer)
                .embedding(embedding)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            String json = objectMapper.writeValueAsString(cached);

            redisTemplate.opsForValue().set(key, json);
            redisTemplate.opsForSet().add(CACHE_KEYS_SET, key);

            log.info("Semantic cache saved in Redis. key={}, question={}", key, question);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize cache item. question={}", question, e);
        }
    }
}