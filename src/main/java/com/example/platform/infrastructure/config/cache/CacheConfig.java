package com.example.platform.infrastructure.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.*;

import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {
//
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {

        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(15));

        Map<String, RedisCacheConfiguration> cacheConfigs =
                Map.of(

                        "courses",
                        defaultConfig.entryTtl(
                                Duration.ofMinutes(10)
                        ),

                        "careers",
                        defaultConfig.entryTtl(
                                Duration.ofMinutes(10)
                        ),

                        "qa-search",
                        defaultConfig.entryTtl(
                                Duration.ofMinutes(5)
                        )
                );

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}
