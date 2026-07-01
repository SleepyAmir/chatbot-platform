package com.example.platform.modules.qa.repository;

import com.example.platform.modules.qa.model.QaEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Provides database access methods for QA embeddings.
 * Loads embedding metadata together with QA pair and course context.
 *
 * <p>Used by QaEmbeddingReadService and QA detail building.
 * DB: qa_embeddings | Phase 1: Read only | Base: JpaRepository</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.QaEmbeddingReadService
 */
public interface QaEmbeddingRepository extends JpaRepository<QaEmbedding, Integer> {

    /**
     * Load embeddings with QA pair and course to avoid lazy loading issues.
     */
    @Query("""
            SELECT e
            FROM qaEmbeddingEntity e
            JOIN FETCH e.qaPair q
            LEFT JOIN FETCH q.course
            """)
    List<QaEmbedding> findAllWithQaPair();

    /**
     * Find one embedding by id with its QA pair.
     */
    @Query("""
            SELECT e
            FROM qaEmbeddingEntity e
            JOIN FETCH e.qaPair q
            LEFT JOIN FETCH q.course
            WHERE e.id = :id
            """)
    Optional<QaEmbedding> findWithQaPairById(
            @Param("id") Integer id
    );

    /**
     * Find the embedding attached to a QA pair.
     */
    @Query("""
            SELECT e
            FROM qaEmbeddingEntity e
            JOIN FETCH e.qaPair q
            LEFT JOIN FETCH q.course
            WHERE q.id = :qaId
            """)
    Optional<QaEmbedding> findByQaId(
            @Param("qaId") Integer qaId
    );

    /**
     * Check if a QA pair already has an embedding.
     */
    @Query("""
            SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
            FROM qaEmbeddingEntity e
            WHERE e.qaPair.id = :qaId
            """)
    boolean existsByQaId(
            @Param("qaId") Integer qaId
    );

    /**
     * Find embeddings by the model that created them.
     */
    @Query("""
            SELECT e
            FROM qaEmbeddingEntity e
            JOIN FETCH e.qaPair q
            LEFT JOIN FETCH q.course
            WHERE LOWER(e.modelName) = LOWER(:modelName)
            """)
    List<QaEmbedding> findByModelName(
            @Param("modelName") String modelName
    );
}
