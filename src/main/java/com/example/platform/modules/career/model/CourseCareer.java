package com.example.platform.modules.career.model;

import com.example.platform.modules.course.model.Course;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "course_careers")
public class CourseCareer {

    @EmbeddedId
    private CourseCareerId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("courseId")
    @JoinColumn(name = "course_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_careers_course"))
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("careerId")
    @JoinColumn(name = "career_id", nullable = false, foreignKey = @ForeignKey(name = "fk_course_careers_career"))
    private Career career;

    @Column(name = "relevance", nullable = false)
    private Float relevance;

    public CourseCareer(Course course, Career career, Float relevance) {
        this.id = new CourseCareerId(course.getId(), career.getId());
        this.course = course;
        this.career = career;
        this.relevance = relevance;
    }
}
