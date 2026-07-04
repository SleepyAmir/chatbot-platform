package com.example.platform.infrastructure.config.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            RedisSerializer<Object> redisJsonSerializer
    ) {
        RedisCacheConfiguration defaultConfig = baseConfig(redisJsonSerializer, Duration.ofMinutes(30));

        Map<String, RedisCacheConfiguration> perCacheConfigs = new HashMap<>();
        perCacheConfigs.put("courses", baseConfig(redisJsonSerializer, Duration.ofMinutes(10)));
        perCacheConfigs.put("careers", baseConfig(redisJsonSerializer, Duration.ofMinutes(10)));
        perCacheConfigs.put("qa-search", baseConfig(redisJsonSerializer, Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(perCacheConfigs)
                .build();
    }

    private RedisCacheConfiguration baseConfig(RedisSerializer<Object> valueSerializer, Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer)
                );
    }
}
