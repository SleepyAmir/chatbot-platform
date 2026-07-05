package com.example.platform.modules.rerank.client;

import com.example.platform.modules.rerank.dto.RerankCandidateInput;
import com.example.platform.modules.rerank.dto.RerankedCandidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

/**
 * کلاینت واقعی سرویس rerank پایتون.
 *
 * قرارداد API مورد انتظار (باید با تیم پایتون هماهنگ شود):
 *
 * POST {rerank.base-url}/rerank
 * Request body:
 * {
 *   "query": "دوره جاوا چقدره؟",
 *   "candidates": [
 *     { "id": "12", "text": "هزینه دوره جاوا چنده", "vectorScore": 0.81 },
 *     { "id": "27", "text": "ثبت‌نام دوره پایتون",   "vectorScore": 0.64 }
 *   ]
 * }
 *
 * Response body:
 * {
 *   "results": [
 *     { "id": "12", "finalScore": 0.93 },
 *     { "id": "27", "finalScore": 0.41 }
 *   ]
 * }
 *
 * نکته: id‌ها رشته‌ای و بدون هیچ معنای خاص (case-sensitivity و غیره) هستند —
 * برخلاف intent، این‌جا هیچ enum بسته‌ای برای مقایسه وجود ندارد، پس همان باگی که در
 * RealIntentClient (uppercase/lowercase) پیش آمد، این‌جا از اساس امکان‌پذیر نیست.
 */
@Slf4j
@Service
@Profile("prod")
public class RealRerankClient implements RerankClient {

    private final WebClient webClient;

    public RealRerankClient(
            WebClient.Builder builder,
            @Value("${clients.rerank.base-url}") String baseUrl
    ) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public List<RerankedCandidate> rerank(String query, List<RerankCandidateInput> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        long start = System.currentTimeMillis();
        try {
            RerankApiResponse response = webClient.post()
                    .uri("/rerank")
                    .bodyValue(new RerankApiRequest(query, candidates))
                    .retrieve()
                    .bodyToMono(RerankApiResponse.class)
                    .timeout(Duration.ofSeconds(8))
                    .block();

            long elapsed = System.currentTimeMillis() - start;

            if (response == null || response.results() == null || response.results().isEmpty()) {
                log.warn("[RealRerankClient] Empty rerank response from Python service ({}ms). Falling back to vector-score order. query='{}'",
                        elapsed, query);
                return fallbackToVectorOrder(candidates);
            }

            log.info("[RealRerankClient] query='{}' candidates={} ({}ms)", query, candidates.size(), elapsed);
            return response.results();

        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[RealRerankClient] Rerank Python service failed after {}ms. Falling back to vector-score order. query='{}'",
                    elapsed, query, e);
            return fallbackToVectorOrder(candidates);
        }
    }

    /**
     * اگر سرویس rerank در دسترس نبود/تایم‌اوت شد، به‌جای شکست کامل، همان ترتیب
     * شباهت برداری خام را برمی‌گردانیم — کیفیت پایین‌تر ولی سیستم سرپا می‌ماند.
     */
    private List<RerankedCandidate> fallbackToVectorOrder(List<RerankCandidateInput> candidates) {
        return candidates.stream()
                .sorted(Comparator.comparingDouble(RerankCandidateInput::vectorScore).reversed())
                .map(c -> new RerankedCandidate(c.id(), c.vectorScore()))
                .toList();
    }

    private record RerankApiRequest(String query, List<RerankCandidateInput> candidates) {
    }

    private record RerankApiResponse(List<RerankedCandidate> results) {
    }
}