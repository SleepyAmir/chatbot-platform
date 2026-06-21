package com.example.platform.modules.rerank.dto;

/**
 * خروجی نهایی rerank برای یک کاندیدا.
 *
 * @param id         همان id ورودی (RerankCandidateInput.id) — تغییر نمی‌کند، فقط برای map کردن برمی‌گردد
 * @param finalScore امتیاز نهاییِ ترکیب‌شده (در بازه‌ی تقریبی 0..1) که علیه آستانه‌ی پذیرش
 *                   (مثلاً 0.78) سنجیده می‌شود تا تصمیم بگیریم جواب از qa_pairs داده شود یا نه.
 */
public record RerankedCandidate(
        String id,
        double finalScore
) {
}