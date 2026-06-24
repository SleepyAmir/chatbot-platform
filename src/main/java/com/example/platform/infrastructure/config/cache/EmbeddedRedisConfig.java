package com.example.platform.infrastructure.config.cache;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Configuration
@Profile({"local","dev"})
public class EmbeddedRedisConfig {

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws Exception {

        redisServer = new RedisServer(6379);

        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() throws Exception {

        if (redisServer != null) {
            redisServer.stop();
        }
    }
}
