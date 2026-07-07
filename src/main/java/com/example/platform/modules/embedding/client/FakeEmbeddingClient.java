package com.example.platform.modules.embedding.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * جایگزین محلیِ سرویس embedding (OpenAI) برای local/dev.
 *
 * نسخه‌ی قبلی این کلاس بردار را از hashCode() کل رشته می‌ساخت — یعنی دو متن با کلمات
 * مشترک زیاد ("قیمت دوره جاوا" و "هزینه دوره جاوا چقدره") می‌توانستند بردارهای کاملاً
 * نامرتبط بگیرند، چون کل رشته یک‌جا هش می‌شد، نه کلمه‌به‌کلمه. این یعنی کش معنایی و
 * جست‌وجوی qa_pairs در dev عملاً تصادفی رفتار می‌کردند.
 *
 * این نسخه از تکنیک استاندارد "feature hashing" روی bag-of-words استفاده می‌کند:
 * هر کلمه (بعد از حذف stopwordهای رایج فارسی) به یکی از ابعاد بردار hash می‌شود و
 * در نهایت بردار L2-normalize می‌شود. نتیجه: متن‌هایی با کلمات مشترک بیشتر، شباهت
 * کسینوسی بالاتری می‌گیرند — دقیقاً همان رفتاری که از یک embedding واقعی انتظار می‌رود
 * (هرچند بدون درک معنایی واقعی، فقط بر اساس همپوشانی کلمات).
 */
@Slf4j
@Service
@Profile({"local", "dev", "prod"})
public class FakeEmbeddingClient implements EmbeddingClient {

    /** باید با ستون vector(384) در qa_embeddings/pgvector و با OpenAiEmbeddingClient یکی باشد. */
    @Value("${embedding.dimensions:384}")
    private int dimensions;

    private static final Set<String> STOPWORDS = Set.of(
            "و", "در", "به", "از", "که", "این", "را", "با", "برای", "است",
            "آیا", "چه", "چی", "هم", "یک", "تا", "آن", "هر", "می", "شود"
    );

    @Override
    public List<Double> embed(String text) {
        long start = System.currentTimeMillis();

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Cannot embed blank text");
        }

        List<String> tokens = tokenize(text);
        double[] vector = new double[dimensions];

        for (String token : tokens) {
            int bucket = Math.floorMod(token.hashCode(), dimensions);
            vector[bucket] += 1.0;

            // یک bigram ساده (دو کلمه‌ی پشت سر هم) هم اضافه می‌شود تا کمی context
            // محلی هم حفظ شود، نه فقط کلمات منفرد بدون ترتیب
        }
        addBigrams(tokens, vector);

        normalize(vector);

        List<Double> result = new ArrayList<>(dimensions);
        for (double v : vector) {
            result.add(v);
        }

        log.debug("[FakeEmbeddingClient] text='{}' tokens={} dims={} ({}ms)",
                text, tokens.size(), dimensions, System.currentTimeMillis() - start);

        return result;
    }

    private List<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase().split("\\s+"))
                .map(t -> t.replaceAll("[^\\p{L}\\p{N}]", ""))
                .filter(t -> !t.isBlank() && !STOPWORDS.contains(t))
                .toList();
    }

    private void addBigrams(List<String> tokens, double[] vector) {
        for (int i = 0; i < tokens.size() - 1; i++) {
            String bigram = tokens.get(i) + "_" + tokens.get(i + 1);
            int bucket = Math.floorMod(bigram.hashCode(), dimensions);
            // وزن کمتر از کلمات منفرد، چون فقط یک سیگنال کمکی است
            vector[bucket] += 0.5;
        }
    }

    private void normalize(double[] vector) {
        double normSquared = 0;
        for (double v : vector) {
            normSquared += v * v;
        }
        if (normSquared == 0) {
            // متن فقط شامل stopword/کاراکترهای غیرحرفی بود -> یک بردار واحد ثابت و
            // deterministic برمی‌گردانیم (نه تصادفی) تا embedding برای یک متن مشخص
            // همیشه یکسان بماند (مهم برای کش معنایی و تست‌پذیری).
            vector[0] = 1.0;
            return;
        }
        double norm = Math.sqrt(normSquared);
        for (int i = 0; i < vector.length; i++) {
            vector[i] /= norm;
        }
    }
}