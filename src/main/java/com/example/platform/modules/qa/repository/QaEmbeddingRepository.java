package com.example.platform.modules.qa.repository;

import com.example.platform.modules.qa.model.QaEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QaEmbeddingRepository extends JpaRepository<QaEmbedding, Integer> {

    @Query("""
            SELECT e
            FROM qaEmbeddingEntity e
            JOIN FETCH e.qaPair q
            LEFT JOIN FETCH q.course
            """)
    List<QaEmbedding> findAllWithQaPair();

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

    @Query("""
            SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
            FROM qaEmbeddingEntity e
            WHERE e.qaPair.id = :qaId
            """)
    boolean existsByQaId(
            @Param("qaId") Integer qaId
    );

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