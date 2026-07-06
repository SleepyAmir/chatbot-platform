package com.example.platform.modules.qa.dto;

import java.time.Instant;

/**
 * Spring Data projection برای خروجی تابع SQL search_qa() (تعریف‌شده در
 * V2__qa_intent_embedding.sql). نام getterها باید با aliasهای ستون در
 * QaPairRepository.searchQa مطابقت داشته باشد.
 */
public interface QaSearchRow {

    Integer getQaId();

    String getQuestion();

    String getAnswer();

    Integer getCourseId();

    Double getSimilarity();

    String getModelName();

    Instant getCreatedAt();
}