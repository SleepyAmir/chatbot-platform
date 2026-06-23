package com.example.platform;

import com.example.platform.modules.cache.CachedQA;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RedisTestService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "qa";

    Faker faker = new Faker();

    private List<Double> generateEmbedding(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> Math.random())
                .toList();
    }


    public CachedQA createFake() {
        CachedQA cachedQA = new CachedQA();
        cachedQA.setId(UUID.randomUUID().toString());
        cachedQA.setIntent("GENERAL");
        cachedQA.setQuestion(faker.lorem().sentence());
        cachedQA.setAnswer(faker.lorem().paragraph());
        cachedQA.setEmbedding(generateEmbedding(384));
        cachedQA.setCreatedAt(LocalDateTime.now());
        cachedQA.setHitCount(0L);
        return cachedQA;

    }

    public void save5000Record() throws Exception {

        for (int i = 0; i < 5000; i++) {
            CachedQA cachedQA = createFake();

            String json = objectMapper.writeValueAsString(cachedQA);
            redisTemplate.opsForValue().set(KEY_PREFIX + cachedQA.getId(), json);
        }

        System.out.println("50000 records saved to redis");
    }

    public void printRedisMemoryUsage(){
        Object info = redisTemplate.getConnectionFactory()
                .getConnection()
                .info("memory");

        System.out.println(("Redis Memory Info: "));
        System.out.println(info);
    }

    public void runTest() throws Exception {

        long start = System.currentTimeMillis();

        save5000Record();

        long end = System.currentTimeMillis();

        System.out.println("Time to insert: " + (end - start) + " ms");

        printRedisMemoryUsage();
    }
}
