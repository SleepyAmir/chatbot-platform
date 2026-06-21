package com.example.platform.modules.qa.repository;

import com.example.platform.modules.qa.dto.QaSearchRow;
import com.example.platform.modules.qa.model.QaPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QaPairRepository extends JpaRepository<QaPair, Integer> {

    Optional<QaPair> findByQuestionIgnoreCase(String question);

    /**
     * فراخوانی تابع search_qa(...) که در V2__qa_intent_embedding.sql تعریف شده.
     * embeddingLiteral باید با PgVectorUtils.toVectorLiteral(...) ساخته شده باشد
     * (مثلاً "[0.12,0.98,...]").
     *
     * aliasهای ستون عمداً صریح نوشته شده‌اند (qa_id AS qaId و ...) چون پروژکشن‌های
     * native query در Spring Data به تطبیق دقیق نام alias با getter وابسته‌اند.
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