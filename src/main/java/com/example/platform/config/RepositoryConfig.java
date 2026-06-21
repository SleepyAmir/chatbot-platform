package com.example.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
        "com.example.platform.modules.course",
        "com.example.platform.modules.qa"
})
@EnableMongoRepositories(basePackages = {
        "com.example.platform.modules.cache",
        "com.example.platform.modules.chatlog",
        "com.example.platform.modules.ocr",
        "com.example.platform.mongo"
})
public class RepositoryConfig {
}