package com.example.platform;

import com.example.platform.modules.cache.dto.CachedQA;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisTestService {

    private static final String KEY_PREFIX = "qa-test:";
    private static final int RECORD_COUNT = 5000;

    private final RedisTemplate<String, Object> redisTemplate;
    private final Faker faker = new Faker();

    private CachedQA createFake() {
        return CachedQA.builder()
                .id(UUID.randomUUID().toString())
                .intent("GENERAL")
                .question(faker.lorem().sentence())
                .answer(faker.lorem().paragraph())
                .createdAt(LocalDateTime.now())
                .hitCount(0L)
                .build();
    }

    public void saveRecords() {
        for (int i = 0; i < RECORD_COUNT; i++) {
            CachedQA cachedQA = createFake();
            redisTemplate.opsForValue().set(KEY_PREFIX + cachedQA.getId(), cachedQA);
        }
        System.out.println(RECORD_COUNT + " records saved to redis");
    }

    public void printRedisMemoryUsage() {
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
        try {
            Properties info = connection.info("memory");
            if (info == null) {
                System.out.println("Could not retrieve Redis memory info");
                return;
            }
            System.out.println("used_memory_human: " + info.getProperty("used_memory_human"));
            System.out.println("used_memory: " + info.getProperty("used_memory"));
            System.out.println("maxmemory_human: " + info.getProperty("maxmemory_human"));
        } finally {
            connection.close();
        }
    }

    public void runTest() {
        long start = System.currentTimeMillis();
        saveRecords();
        long end = System.currentTimeMillis();
        System.out.println("Time to insert: " + (end - start) + " ms");
        printRedisMemoryUsage();
    }
}