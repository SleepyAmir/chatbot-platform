package com.example.platform.modules.rerank.client;

import com.example.platform.modules.rerank.dto.RerankCandidateInput;
import com.example.platform.modules.rerank.dto.RerankedCandidate;

import java.util.List;

/**
 * انتزاع سرویس rerank — دقیقاً هم‌خانواده با IntentClient / LLMClient / EmbeddingClient:
 * در پروفایل local/dev با {@link FakeRerankClient} (الگوریتم محلی) پاسخ داده می‌شود،
 * در پروفایل prod با {@link RealRerankClient} (تماس HTTP با سرویس پایتون rerank).
 * هیچ بخش دیگری از پروژه نباید مستقیماً با این دو پیاده‌سازی کار کند؛ همیشه از طریق این اینترفیس.
 */
public interface RerankClient {

    /**
     * کاندیداها را بر اساس ارتباط واقعی‌شان با سوال کاربر، با امتیاز نهایی مرتب می‌کند.
     *
     * @param query      سوال خام کاربر (یا سوال شفاف‌سازی‌شده)
     * @param candidates لیست کاندیداهایی که قبلاً از جست‌وجوی برداری به‌دست آمده‌اند (معمولاً top-K)
     * @return لیست merge‌شده، نزولی بر اساس finalScore. هرگز null نیست؛
     *         اگر candidates خالی باشد، لیست خالی برمی‌گردد.
     */
    List<RerankedCandidate> rerank(String query, List<RerankCandidateInput> candidates);
}