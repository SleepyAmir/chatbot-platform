package com.example.platform.modules.course.model;

import com.example.platform.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Maps to the "course_details" table created in V1__init_courses.sql.
 *
 * Columns:
 *   id            SERIAL PRIMARY KEY                    -> inherited from BaseEntity
 *   course_id     INT NOT NULL UNIQUE (FK -> courses)    -> course (OneToOne)
 *   price         TEXT                                  -> price
 *   teacher       TEXT                                  -> teacher
 *   duration      TEXT                                  -> duration
 *   branch        TEXT                                  -> branch
 *   link          TEXT                                  -> link
 *   department    TEXT                                  -> department
 *   prerequisite  TEXT                                  -> prerequisite
 *   syllabus      TEXT                                  -> syllabus
 *   start_time    TEXT                                  -> startTime
 *   course_code   TEXT                                  -> courseCode
 *   updated_at    TIMESTAMPTZ DEFAULT NOW()              -> updatedAt
 *
 * Note:
 * This does NOT extend CreatedAtEntity because this table has updated_at, not created_at.
 * updated_at is managed by the PostgreSQL trigger trg_course_details_updated_at,
 * so we do NOT use @UpdateTimestamp here.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "course_details",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_course_details_course",
                columnNames = "course_id"
        )
)
public class CourseDetail extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "course_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_course_details_course")
    )
    private Course course;

    @Column(name = "price", columnDefinition = "TEXT")
    private String price;

    @Column(name = "teacher", columnDefinition = "TEXT")
    private String teacher;

    @Column(name = "duration", columnDefinition = "TEXT")
    private String duration;

    @Column(name = "branch", columnDefinition = "TEXT")
    private String branch;

    @Column(name = "link", columnDefinition = "TEXT")
    private String link;

    @Column(name = "department", columnDefinition = "TEXT")
    private String department;

    @Column(name = "prerequisite", columnDefinition = "TEXT")
    private String prerequisite;

    @Column(name = "syllabus", columnDefinition = "TEXT")
    private String syllabus;

    @Column(name = "start_time", columnDefinition = "TEXT")
    private String startTime;

    @Column(name = "course_code", columnDefinition = "TEXT")
    private String courseCode;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    public CourseDetail(Course course) {
        this.course = course;
    }
}