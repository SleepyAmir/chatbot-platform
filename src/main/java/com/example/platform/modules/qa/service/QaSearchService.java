package com.example.platform.modules.qa.service;

import com.example.platform.common.util.PgVectorUtils;
import com.example.platform.modules.embedding.service.EmbeddingService;
import com.example.platform.modules.qa.dto.QaMatch;
import com.example.platform.modules.qa.dto.QaSearchRow;
import com.example.platform.modules.qa.repository.QaPairRepository;
import com.example.platform.modules.rerank.client.RerankClient;
import com.example.platform.modules.rerank.dto.RerankCandidateInput;
import com.example.platform.modules.rerank.dto.RerankedCandidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * زنجیره‌ی کامل RAG روی qa_pairs:
 *
 *   1) embed(question)                              -> EmbeddingClient (Fake/OpenAI)
 *   2) search_qa(embedding, topK, minVectorScore)    -> pgvector ANN search روی qa_embeddings
 *   3) rerank(question, candidates)                  -> RerankClient (Fake/Python)
 *   4) اگر بهترین امتیاز نهایی >= آستانه‌ی پذیرش بود -> جواب مستقیم از qa_pairs
 *      وگرنه -> Optional.empty() و OrchestratorService می‌رود سراغ LLM
 *
 * این سرویس عمداً "fail open به سمت LLM" طراحی شده: هر خطایی در هر مرحله (embedding،
 * دیتابیس، rerank) باعث Optional.empty() می‌شود، نه پرتاب exception — چون نبود جواب
 * از کش معنایی نباید کل درخواست چت کاربر را خراب کند؛ فقط یعنی برو سراغ LLM.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QaSearchService {

    private final EmbeddingService embeddingService;
    private final QaPairRepository qaPairRepository;
    private final RerankClient rerankClient;

    /** چند کاندیدا از pgvector برای rerank بیاوریم. */
    @Value("${qa.search.top-k:10}")
    private int topK;

    /**
     * فیلتر اولیه و شل در سطح pgvector (قبل از rerank) — فقط برای کم‌کردن حجم
     * کاندیداهای بی‌ربط، نه تصمیم نهایی.
     */
    @Value("${qa.search.min-vector-similarity:0.55}")
    private double minVectorSimilarity;

    /**
     * آستانه‌ی نهایی پذیرش بعد از rerank. اگر بهترین finalScore زیر این مقدار بود،
     * یعنی هیچ qa_pair موجود به اندازه‌ی کافی به سوال کاربر نزدیک نیست -> برو سراغ LLM.
     */
    @Value("${qa.search.min-rerank-score:0.78}")
    private double minRerankScore;

    public Optional<QaMatch> findBestMatch(String userQuestion) {
        if (userQuestion == null || userQuestion.isBlank()) {
            return Optional.empty();
        }

        List<Double> queryEmbedding;
        try {
            queryEmbedding = embeddingService.embed(userQuestion);
        } catch (Exception e) {
            log.warn("Embedding failed during QA search. question={}", userQuestion, e);
            return Optional.empty();
        }

        if (queryEmbedding == null || queryEmbedding.isEmpty()) {
            return Optional.empty();
        }

        List<QaSearchRow> candidates;
        try {
            candidates = qaPairRepository.searchQa(
                    PgVectorUtils.toVectorLiteral(queryEmbedding),
                    topK,
                    minVectorSimilarity
            );
        } catch (Exception e) {
            log.error("pgvector search_qa() call failed. question={}", userQuestion, e);
            return Optional.empty();
        }

        if (candidates == null || candidates.isEmpty()) {
            log.info("No qa_pairs candidate above vector threshold={}. question={}", minVectorSimilarity, userQuestion);
            return Optional.empty();
        }

        List<RerankCandidateInput> rerankInputs = candidates.stream()
                .map(row -> new RerankCandidateInput(
                        String.valueOf(row.getQaId()),
                        row.getQuestion(),
                        row.getSimilarity() != null ? row.getSimilarity() : 0.0
                ))
                .toList();

        List<RerankedCandidate> reranked;
        try {
            reranked = rerankClient.rerank(userQuestion, rerankInputs);
        } catch (Exception e) {
            log.error("Rerank call failed, falling back to raw vector ranking. question={}", userQuestion, e);
            reranked = rerankInputs.stream()
                    .sorted(Comparator.comparingDouble(RerankCandidateInput::vectorScore).reversed())
                    .map(c -> new RerankedCandidate(c.id(), c.vectorScore()))
                    .toList();
        }

        if (reranked.isEmpty()) {
            return Optional.empty();
        }

        RerankedCandidate best = reranked.get(0);

        if (best.finalScore() < minRerankScore) {
            log.info("Best QA candidate below rerank threshold. score={}, threshold={}, question={}",
                    best.finalScore(), minRerankScore, userQuestion);
            return Optional.empty();
        }

        QaSearchRow bestRow = candidates.stream()
                .filter(row -> String.valueOf(row.getQaId()).equals(best.id()))
                .findFirst()
                .orElse(null);

        if (bestRow == null) {
            log.warn("Reranked id={} not found among original candidates (unexpected). question={}", best.id(), userQuestion);
            return Optional.empty();
        }

        log.info("QA semantic match accepted. qaId={}, vectorScore={}, rerankScore={}, question={}",
                bestRow.getQaId(), bestRow.getSimilarity(), best.finalScore(), userQuestion);

        return Optional.of(new QaMatch(
                bestRow.getQaId(),
                bestRow.getQuestion(),
                bestRow.getAnswer(),
                bestRow.getSimilarity() != null ? bestRow.getSimilarity() : 0.0,
                best.finalScore()
        ));
    }
}