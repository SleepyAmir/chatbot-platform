package com.example.platform.modules.chatlog.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@Profile("prod")
public class RealLLMClient implements LLMClient {

    private final WebClient webClient;

    public RealLLMClient(
            WebClient.Builder builder,
            @Value("${clients.llm.base-url}") String baseUrl
    ) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public String ask(String question) {
        long start = System.currentTimeMillis();
        try {
            LLMResponse response = webClient.post()
                    .uri("/ask")
                    .bodyValue(Map.of("question", question))
                    .retrieve()
                    .bodyToMono(LLMResponse.class)
                    .timeout(Duration.ofSeconds(15))
                    .block();

            long elapsed = System.currentTimeMillis() - start;

            if (response == null || response.answer() == null || response.answer().isBlank()) {
                log.warn("[RealLLMClient] Empty LLM response from Python service ({}ms).", elapsed);
                return "متأسفانه الان نتونستم پاسخ مناسبی تولید کنم.";
            }

            log.info("[RealLLMClient] Got answer ({}ms, {} chars)", elapsed, response.answer().length());
            return response.answer().trim();

        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[RealLLMClient] LLM Python service failed after {}ms.", elapsed, e);
            return "متأسفانه سرویس پاسخ‌گویی هوشمند در حال حاضر در دسترس نیست.";
        }
    }

    record LLMResponse(String answer) {}
}