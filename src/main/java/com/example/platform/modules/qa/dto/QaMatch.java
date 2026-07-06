package com.example.platform.modules.qa.dto;

/**
 * بهترین تطبیق نهایی (بعد از جست‌وجوی برداری + rerank) که OrchestratorService
 * مستقیماً به‌عنوان پاسخ کاربر استفاده می‌کند — بدون فراخوانی LLM.
 */
public record QaMatch(
        Integer qaId,
        String question,
        String answer,
        double vectorScore,
        double rerankScore
) {
}