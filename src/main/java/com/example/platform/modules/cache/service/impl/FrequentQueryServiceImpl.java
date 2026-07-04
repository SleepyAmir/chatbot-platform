package com.example.platform.modules.cache.service.impl;
//
import com.example.platform.modules.cache.dto.FrequentQueryDto;
import com.example.platform.modules.cache.service.FrequentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;
//
@Service
@RequiredArgsConstructor
public class FrequentQueryServiceImpl implements FrequentQueryService {

    private static final String FREQUENT_QUERIES_KEY = "frequent:queries";

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager cacheManager;

    @Override
    public void trackQuery(String query) {
        if (query == null || query.isBlank()) {
            return;
        }
        redisTemplate.opsForZSet().incrementScore(FREQUENT_QUERIES_KEY, query.trim(), 1);
    }

    @Override
    public List<FrequentQueryDto> getTopQueries(int limit) {
        Set<ZSetOperations.TypedTuple<Object>> tuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(FREQUENT_QUERIES_KEY, 0, limit - 1);
        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }

        return tuples.stream()
                .map(tuple -> new FrequentQueryDto(
                        String.valueOf(tuple.getValue()),
                        tuple.getScore() == null ? 0L : tuple.getScore().longValue()
                ))
                .toList();
    }

    @Override
    public void flushAllCache() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }
}
