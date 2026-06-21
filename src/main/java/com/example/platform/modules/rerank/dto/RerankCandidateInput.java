package com.example.platform.modules.rerank.dto;

/**
 * یک کاندیدِ ورودی به سرویس rerank.
 *
 * @param id          شناسه‌ی کاندیدا (مثلاً qa_id در qa_pairs) — برای map کردن نتیجه به رکورد اصلی
 * @param text        متنی که کیفیت تطبیقش با سوال کاربر باید سنجیده شود (مثلاً question در qa_pairs)
 * @param vectorScore شباهت کسینوسی‌ای که در مرحله‌ی جست‌وجوی برداری (pgvector) به‌دست آمده؛
 *                    rerank این عدد را به‌عنوان یکی از سیگنال‌های ورودی خودش در نظر می‌گیرد،
 *                    نه به‌عنوان امتیاز نهایی.
 */
public record RerankCandidateInput(
        String id,
        String text,
        double vectorScore
) {
}