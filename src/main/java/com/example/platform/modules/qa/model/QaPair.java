package com.example.platform.modules.qa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Maps to the "qa_pairs" table created in V2__qa_intent_embedding.sql.
 * این جدول قبلاً فقط در دیتابیس وجود داشت و هیچ Entity جاوایی نداشت.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "qa_pairs")
public class QaPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public QaPair(String question, String answer, Integer courseId) {
        this.question = question;
        this.answer = answer;
        this.courseId = courseId;
    }
}