package com.example.platform.modules.course.model;

import com.example.platform.common.entity.CreatedAtEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maps to the "courses" table created in V1__init_courses.sql.
 *
 * Columns:
 *   id          SERIAL PRIMARY KEY         -> inherited from CreatedAtEntity/BaseEntity
 *   name        TEXT NOT NULL UNIQUE       -> name
 *   lesson_url  TEXT                       -> lessonUrl
 *   created_at  TIMESTAMPTZ DEFAULT NOW()  -> inherited from CreatedAtEntity (DB-managed)
 *
 * Uniqueness note:
 * The original case-sensitive uk_courses_name constraint (from V1) was replaced in
 * V5__courses_case_insensitive_unique_name.sql with a unique index on LOWER(name),
 * to match the case-insensitive check already done in CourseServiceImpl
 * (existsByNameIgnoreCase / existsByNameIgnoreCaseAndIdNot). No @Table(uniqueConstraints=...)
 * is declared here because Hibernate annotations can't express a functional LOWER(...) index —
 * the real constraint lives purely in the database via Flyway, this entity just maps columns.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "courses")
public class Course extends CreatedAtEntity {

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "lesson_url", columnDefinition = "TEXT")
    private String lessonUrl;

    public Course(String name, String lessonUrl) {
        this.name = name;
        this.lessonUrl = lessonUrl;
    }
}