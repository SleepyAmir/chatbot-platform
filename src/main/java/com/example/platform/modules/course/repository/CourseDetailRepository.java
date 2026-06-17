package com.example.platform.modules.course.repository;

import com.example.platform.modules.course.model.CourseDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseDetailRepository extends JpaRepository<CourseDetail, Integer> {

    @EntityGraph(attributePaths = "course")
    Optional<CourseDetail> findByCourse_Id(Integer courseId);

    boolean existsByCourse_Id(Integer courseId);

    void deleteByCourse_Id(Integer courseId);

    @EntityGraph(attributePaths = "course")
    Optional<CourseDetail> findWithCourseById(Integer id);

    @EntityGraph(attributePaths = "course")
    List<CourseDetail> findByCourse_IdIn(Collection<Integer> courseIds);

    @Query("""
            SELECT d
            FROM CourseDetail d
            JOIN d.course c
            WHERE
                :keyword IS NULL
                OR :keyword = ''
                OR LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(d.price, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(d.teacher, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(d.duration, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(d.branch, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(d.department, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(d.prerequisite, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(d.syllabus, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(d.startTime, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(d.courseCode, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<CourseDetail> searchDetails(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}