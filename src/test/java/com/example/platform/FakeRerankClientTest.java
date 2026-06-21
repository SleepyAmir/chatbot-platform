package com.example.platform;

import com.example.platform.modules.rerank.client.FakeRerankClient;
import com.example.platform.modules.rerank.dto.RerankCandidateInput;
import com.example.platform.modules.rerank.dto.RerankedCandidate;
import com.example.platform.modules.search.service.RerankService;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FakeRerankClientTest {

    private final FakeRerankClient client = new FakeRerankClient(new RerankService());

    @Test
    void emptyCandidates_returnsEmptyList() {
        assertEquals(List.of(), client.rerank("دوره جاوا چقدره؟", List.of()));
    }

    @Test
    void higherVectorScore_andKeywordOverlap_winsFirstPlace() {
        List<RerankCandidateInput> candidates = List.of(
                new RerankCandidateInput("1", "هزینه دوره جاوا چقدر است", 0.90),
                new RerankCandidateInput("2", "ثبت نام دوره پایتون", 0.60)
        );

        List<RerankedCandidate> result = client.rerank("هزینه دوره جاوا چقدره", candidates);

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).id());
        assertTrue(result.get(0).finalScore() > result.get(1).finalScore());
    }

    @Test
    void finalScore_matchesHybridFormula_pureVectorNoKeywordOverlap() {
        // متن کاندیدا هیچ کلمه‌ی مشترکی با سوال ندارد -> keywordOverlap = 0
        // پس finalScore باید دقیقاً 0.7 * vectorScore باشد.
        List<RerankCandidateInput> candidates = List.of(
                new RerankCandidateInput("1", "زمان کلاس‌های آنلاین", 0.80)
        );

        List<RerankedCandidate> result = client.rerank("قیمت دوره جاوا چقدره", candidates);

        assertEquals(1, result.size());
        assertEquals(0.7 * 0.80, result.get(0).finalScore(), 1e-9);
    }
}