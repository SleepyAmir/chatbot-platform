package com.example.platform.modules.qa.repository;

import com.example.platform.modules.qa.model.QaPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QaPairRepository extends JpaRepository<QaPair, Integer> {

    @Query("""
            SELECT q
            FROM qa_pairEntity q
            LEFT JOIN FETCH q.course
            """)
    List<QaPair> findAllWithCourse();

    @Query("""
            SELECT q
            FROM qa_pairEntity q
            LEFT JOIN FETCH q.course
            WHERE q.id = :id
            """)
    Optional<QaPair> findWithCourseById(
            @Param("id") Integer id
    );

    @Query("""
            SELECT q
            FROM qa_pairEntity q
            LEFT JOIN FETCH q.course
            WHERE q.course.id = :courseId
            """)
    List<QaPair> findByCourseId(
            @Param("courseId") Integer courseId
    );

    @Query("""
            SELECT q
            FROM qa_pairEntity q
            LEFT JOIN FETCH q.course
            WHERE
                :keyword IS NULL
                OR :keyword = ''
                OR LOWER(q.question) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(q.answer) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(q.course.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<QaPair> searchQaPairs(
            @Param("keyword") String keyword
    );

    @Query("""
            SELECT DISTINCT q
            FROM qaIntentEntity qi
            JOIN qi.qaPair q
            LEFT JOIN FETCH q.course
            JOIN qi.intent i
            WHERE LOWER(i.name) = LOWER(:intentName)
            """)
    List<QaPair> findByIntentName(
            @Param("intentName") String intentName
    );

    @Query("""
            SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END
            FROM qa_pairEntity q
            WHERE LOWER(q.question) = LOWER(:question)
            """)
    boolean existsByQuestionIgnoreCase(
            @Param("question") String question
    );
}