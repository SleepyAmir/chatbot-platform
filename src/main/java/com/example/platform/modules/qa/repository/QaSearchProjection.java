package com.example.platform.modules.qa.repository;

public interface QaSearchProjection {

    Integer getQaId();

    String getQuestion();

    String getAnswer();

    Integer getCourseId();

    Double getSimilarity();

    String getModelName();
}
