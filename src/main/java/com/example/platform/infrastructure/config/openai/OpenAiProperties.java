package com.example.platform.infrastructure.config.openai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    private String apiKey;
    private String baseUrl;
    private Embedding embedding;

    @Data
    public static class Embedding {
        private String model;
    }
}

