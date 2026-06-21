package com.example.platform;

import com.example.platform.modules.embedding.client.FakeEmbeddingClient;
import com.example.platform.modules.search.service.VectorSimilarityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * نسخه‌ی قبلی FakeEmbeddingClient کل رشته را یک‌جا hash می‌کرد، یعنی دو متن با کلمات
 * مشترک زیاد می‌توانستند بردارهای کاملاً نامرتبط بگیرند. این تست تضمین می‌کند نسخه‌ی
 * جدید (feature hashing روی bag-of-words) رفتار منطقی دارد: متن‌های مشابه باید شباهت
 * کسینوسی بسیار بالاتری نسبت به متن‌های نامرتبط داشته باشند.
 */
class FakeEmbeddingClientTest {

    private final FakeEmbeddingClient client = new FakeEmbeddingClient();
    private final VectorSimilarityService similarity = new VectorSimilarityService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "dimensions", 384);
    }

    @Test
    void similarPersianQuestions_haveHighCosineSimilarity() {
        List<Double> v1 = client.embed("هزینه دوره جاوا چقدره؟");
        List<Double> v2 = client.embed("قیمت دوره جاوا چنده؟");

        double score = similarity.cosine(v1, v2);

        assertTrue(score > 0.5, "Expected high similarity for overlapping-word questions, got " + score);
    }

    @Test
    void unrelatedQuestions_haveLowCosineSimilarity() {
        List<Double> v1 = client.embed("هزینه دوره جاوا چقدره؟");
        List<Double> v2 = client.embed("ساعت کاری پشتیبانی چنده؟");

        double score = similarity.cosine(v1, v2);

        assertTrue(score < 0.4, "Expected low similarity for unrelated questions, got " + score);
    }

    @Test
    void sameText_isDeterministic() {
        List<Double> v1 = client.embed("دوره پایتون چقدره");
        List<Double> v2 = client.embed("دوره پایتون چقدره");

        assertTrue(similarity.cosine(v1, v2) > 0.999, "Embedding the same text twice must be deterministic");
    }

    @Test
    void vectorHasConfiguredDimensions() {
        List<Double> v = client.embed("سلام");
        assertTrue(v.size() == 384);
    }
}