package com.example.platform.modules.rerank.client;

import com.example.platform.modules.rerank.dto.RerankCandidateInput;
import com.example.platform.modules.rerank.dto.RerankedCandidate;
import com.example.platform.modules.search.service.RerankService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * جایگزین محلیِ سرویس rerank پایتون برای local/dev.
 *
 * همان قرارداد (0.7 * شباهت برداری + 0.3 * هم‌پوشانی کلیدواژه) را با استفاده از
 * {@link RerankService} پیاده می‌کند، تا کل مسیر QaSearchService → Rerank → تصمیم نهایی
 * بدون نیاز به سرویس پایتون واقعی، سر تا ته قابل تست باشد.
 */
@Service
@Profile({"local"})
@RequiredArgsConstructor
public class FakeRerankClient implements RerankClient {

    private final RerankService rerankService;

    @Override
    public List<RerankedCandidate> rerank(String query, List<RerankCandidateInput> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        return candidates.stream()
                .map(candidate -> new RerankedCandidate(
                        candidate.id(),
                        rerankService.score(candidate.vectorScore(), candidate.text(), query)
                ))
                .sorted(Comparator.comparingDouble(RerankedCandidate::finalScore).reversed())
                .toList();
    }
}