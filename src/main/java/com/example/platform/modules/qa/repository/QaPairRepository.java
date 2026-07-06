package com.example.platform.modules.qa.repository;

import com.example.platform.modules.qa.dto.QaSearchRow;
import com.example.platform.modules.qa.model.QaPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Provides database access methods for QA pairs.
 * Contains list, lookup, keyword search, and filter queries.
 *
 * <p>Used by QaPairReadService as the main QA persistence layer.
 * DB: qa_pairs | Phase 1: Read/Search only | Base: JpaRepository</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.QaPairReadService
 */
public interface QaPairRepository extends JpaRepository<QaPair, Integer> {

    /**
     * Load all QA pairs with optional course data.
     */
    @Query("""
            SELECT q
            FROM qa_pairEntity q
            LEFT JOIN FETCH q.course
            """)
    List<QaPair> findAllWithCourse();

    /**
     * Find one QA pair by id with course data.
     */
    @Query("""
            SELECT q
            FROM qa_pairEntity q
            LEFT JOIN FETCH q.course
            WHERE q.id = :id
            """)
    Optional<QaPair> findWithCourseById(
            @Param("id") Integer id
    );

    /**
     * Find QA pairs that belong to a course.
     */
    @Query("""
            SELECT q
            FROM qa_pairEntity q
            LEFT JOIN FETCH q.course
            WHERE q.course.id = :courseId
            """)
    List<QaPair> findByCourseId(
            @Param("courseId") Integer courseId
    );

    /**
     * Search question, answer, and course name by keyword.
     */
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

    /**
     * Find QA pairs connected to an intent name.
     */
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

    /**
     * Check duplicate questions, ignoring case.
     */
    @Query("""
            SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END
            FROM qa_pairEntity q
            WHERE LOWER(q.question) = LOWER(:question)
            """)
    boolean existsByQuestionIgnoreCase(
            @Param("question") String question
    );

    /**
     * Calls the search_qa(...) pgvector function defined in V2__qa_intent_embedding.sql.
     * embeddingLiteral must be built via PgVectorUtils.toVectorLiteral(...)
     * (e.g. "[0.12,0.98,...]").
     *
     * <p>Used by QaSearchService as the vector-search stage of the RAG pipeline
     * (embed -> search_qa -> rerank -> accept/reject).</p>
     *
     * Column aliases are written explicitly (qa_id AS qaId etc.) because native
     * query projections in Spring Data rely on an exact alias-to-getter match.
     */
    @Query(value = """
            SELECT
                qa_id       AS qaId,
                question    AS question,
                answer      AS answer,
                course_id   AS courseId,
                similarity  AS similarity,
                model_name  AS modelName,
                created_at  AS createdAt
            FROM search_qa(
                CAST(:embedding AS vector(384)),
                :topK,
                :minSimilarity
            )
            """, nativeQuery = true)
    List<QaSearchRow> searchQa(
            @Param("embedding") String embeddingLiteral,
            @Param("topK") int topK,
            @Param("minSimilarity") double minSimilarity
    );
}
