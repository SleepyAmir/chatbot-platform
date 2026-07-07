package com.example.platform.modules.embedding.client;

import com.example.platform.common.exception.EmbeddingException;
import com.example.platform.infrastructure.config.openai.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Profile("openai")
@RequiredArgsConstructor
public class OpenAiEmbeddingClient implements EmbeddingClient {

    private final OpenAiProperties openAiProperties;
    private final WebClient.Builder webClientBuilder;

    @Override
    public List<Double> embed(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Cannot embed blank text");
        }
        try {
            var response = webClientBuilder
                    .baseUrl(openAiProperties.getBaseUrl())
                    .defaultHeader("Authorization", "Bearer " + openAiProperties.getApiKey())
                    .build()
                    .post()
                    .uri("/embeddings")
                    .bodyValue(Map.of(
                            "input", text,
                            "model", openAiProperties.getEmbedding().getModel(),
                            "dimensions", 384   // ← هماهنگ با vector(384) در Postgres
                    ))
                    .retrieve()
                    .bodyToMono(EmbeddingResponse.class)
                    .block();

            if (response == null || response.data() == null || response.data().isEmpty()) {
                throw new IllegalStateException("Empty embedding response for text: " + text);
            }
            return response.data().get(0).embedding();

        } catch (Exception e) {
            throw new EmbeddingException("Embedding API call failed", e); // یک RuntimeException اختصاصی بساز
        }
    }
    record EmbeddingResponse(List<EmbeddingData> data) {}
    record EmbeddingData(List<Double> embedding) {}
}