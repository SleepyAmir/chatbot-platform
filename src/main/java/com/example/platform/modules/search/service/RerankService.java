package com.example.platform.modules.search.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * الگوریتم خالص هیبریدی (0.7 * شباهت برداری + 0.3 * هم‌پوشانی کلیدواژه‌ای) که در پروفایل
 * local/dev به‌عنوان جایگزین سرویس واقعی rerank پایتون استفاده می‌شود
 * (نگاه کنید به {@link com.example.platform.modules.rerank.client.FakeRerankClient}).
 * در پروفایل prod اصلاً استفاده نمی‌شود — منبع حقیقت آن‌جا سرویس پایتون است
 * (نگاه کنید به {@link com.example.platform.modules.rerank.client.RealRerankClient}).
 */
@Service
public class RerankService {

    private static final double SIMILARITY_WEIGHT = 0.7;
    private static final double KEYWORD_WEIGHT = 0.3;

    /**
     * نسخه‌ی قبلی API: فقط لیست مرتب‌شده برمی‌گرداند (بدون امتیاز عددی).
     * برای کاربردهای داخلی که فقط ترتیب مهم است نگه داشته شده.
     */
    public <T extends RerankCandidate> List<T> rerank(List<T> candidates, String userQuery) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        Set<String> queryTokens = tokenize(userQuery);

        return candidates.stream()
                .sorted(Comparator.comparingDouble(
                        (T candidate) -> -score(candidate.getSimilarityScore(), candidate.getText(), queryTokens)
                ))
                .toList();
    }

    /**
     * نقطه‌ی ورود عمومی برای گرفتن امتیاز عددی واقعی (نه فقط ترتیب) — این متد را
     * {@link com.example.platform.modules.rerank.client.FakeRerankClient} صدا می‌زند
     * تا برای هر کاندیدا finalScore واقعی تولید کند.
     */
    public double score(double similarityScore, String candidateText, String userQuery) {
        return score(similarityScore, candidateText, tokenize(userQuery));
    }

    private double score(double similarityScore, String candidateText, Set<String> queryTokens) {
        double keywordOverlapScore = calculateKeywordOverlap(candidateText, queryTokens);
        return (SIMILARITY_WEIGHT * similarityScore) + (KEYWORD_WEIGHT * keywordOverlapScore);
    }

    private double calculateKeywordOverlap(String candidateText, Set<String> queryTokens) {
        if (candidateText == null || candidateText.isBlank() || queryTokens.isEmpty()) {
            return 0.0;
        }

        Set<String> candidateTokens = tokenize(candidateText);

        long overlapCount = queryTokens.stream()
                .filter(candidateTokens::contains)
                .count();

        return (double) overlapCount / queryTokens.size();
    }

    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(text.toLowerCase().split("\\s+"))
                .map(token -> token.replaceAll("[^\\p{L}\\p{N}]", ""))
                .filter(token -> !token.isBlank())
                .collect(Collectors.toSet());
    }

    public interface RerankCandidate {
        double getSimilarityScore();

        String getText();
    }
}