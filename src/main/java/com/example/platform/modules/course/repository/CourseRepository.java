package com.example.platform.modules.course.repository;

import com.example.platform.modules.course.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query("""
            SELECT c
            FROM Course c
            WHERE LOWER(c.name) = LOWER(:name)
            """)
    Optional<Course> findByNameIgnoreCaseExact(@Param("name") String name);

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
            FROM Course c
            WHERE LOWER(c.name) = LOWER(:name)
            """)
    boolean existsByNameIgnoreCase(@Param("name") String name);

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
            FROM Course c
            WHERE LOWER(c.name) = LOWER(:name)
              AND c.id <> :id
            """)
    boolean existsByNameIgnoreCaseAndIdNot(
            @Param("name") String name,
            @Param("id") Integer id
    );

    @Query("""
            SELECT c
            FROM Course c
            WHERE
                :keyword IS NULL
                OR :keyword = ''
                OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(c.lessonUrl, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Course> searchCourses(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}