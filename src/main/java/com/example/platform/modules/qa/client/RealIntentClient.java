package com.example.platform.modules.qa.client;

import com.example.platform.common.constant.ChatIntents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@Profile("prod")
public class RealIntentClient implements IntentClient {

    private static final Set<String> ALLOWED_INTENTS = Set.of(
            ChatIntents.FAQ,
            ChatIntents.PRICING,
            ChatIntents.CLASS_SEARCH,
            ChatIntents.LLM
    );

    private final WebClient webClient;

    public RealIntentClient(
            WebClient.Builder builder,
            @Value("${clients.intent.base-url}") String baseUrl
    ) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public String detectIntent(String question) {
        long start = System.currentTimeMillis();
        try {
            IntentResponse response = webClient.post()
                    .uri("/detect")
                    .bodyValue(Map.of("text", question))
                    .retrieve()
                    .bodyToMono(IntentResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

            long elapsed = System.currentTimeMillis() - start;

            if (response == null || response.intent() == null || response.intent().isBlank()) {
                log.warn("[RealIntentClient] Empty intent response from Python service ({}ms). Fallback to LLM.", elapsed);
                return ChatIntents.LLM;
            }

            String intent = resolveIntent(response.intent());

            if (intent == null) {
                log.warn("[RealIntentClient] Unknown intent from Python service: raw='{}' ({}ms). Fallback to LLM.",
                        response.intent(), elapsed);
                return ChatIntents.LLM;
            }

            log.info("[RealIntentClient] Detected intent='{}', confidence={} ({}ms)", intent, response.confidence(), elapsed);
            return intent;

        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[RealIntentClient] Intent Python service failed after {}ms. Fallback to LLM.", elapsed, e);
            return ChatIntents.LLM;
        }
    }

    /**
     * نرمال‌سازی و اعتبارسنجی intent خام برگشتی از سرویس پایتون.
     *
     * نکته‌ی مهم (باگ قبلی که این‌جا رفع شده): ALLOWED_INTENTS با حروف کوچک تعریف شده
     * (ChatIntents.FAQ = "faq" و...)، پس پاسخ سرویس پایتون باید به lowercase نرمال شود،
     * نه uppercase — نسخه‌ی قبلی این متد toUpperCase() صدا می‌زد که باعث می‌شد "PRICING"
     * هیچ‌وقت با مقدار ثابتِ lowercase "pricing" مچ نشود و در نتیجه intent همیشه (در عمل
     * در هر فراخوانی پروداکشن) به LLM فال‌بک می‌کرد.
     *
     * package-private و static تا بدون نیاز به mock کردن WebClient قابل تست باشد
     * (نگاه کنید به RealIntentClientTest).
     *
     * @return یکی از مقادیر ChatIntents اگر معتبر بود، یا null اگر نامعتبر/ناشناخته بود.
     */
    static String resolveIntent(String rawIntent) {
        if (rawIntent == null) {
            return null;
        }
        String normalized = rawIntent.trim().toLowerCase();
        return ALLOWED_INTENTS.contains(normalized) ? normalized : null;
    }

    record IntentResponse(String intent, Double confidence) {}
}