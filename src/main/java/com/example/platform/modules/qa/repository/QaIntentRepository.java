package com.example.platform.modules.qa.repository;

import com.example.platform.modules.qa.model.QaIntent;
import com.example.platform.modules.qa.model.QaIntentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Provides database access methods for QA-intent relationships.
 * Contains read, count, and existence queries for the join table.
 *
 * <p>Used by QaIntentReadService and QA detail building.
 * DB: qa_intents | Phase 1: Read only | Base: JpaRepository</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.QaIntentReadService
 */
public interface QaIntentRepository extends JpaRepository<QaIntent, QaIntentId> {

    /**
     * Load all QA-intent links with both sides fetched.
     */
    @Query("""
            SELECT qi
            FROM qaIntentEntity qi
            JOIN FETCH qi.qaPair q
            JOIN FETCH qi.intent i
            """)
    List<QaIntent> findAllWithQaPairAndIntent();

    /**
     * Find all intent links for one QA pair.
     */
    @Query("""
            SELECT qi
            FROM qaIntentEntity qi
            JOIN FETCH qi.qaPair q
            JOIN FETCH qi.intent i
            WHERE q.id = :qaId
            """)
    List<QaIntent> findByQaId(
            @Param("qaId") Integer qaId
    );

    /**
     * Find all QA links for one intent.
     */
    @Query("""
            SELECT qi
            FROM qaIntentEntity qi
            JOIN FETCH qi.qaPair q
            JOIN FETCH qi.intent i
            WHERE i.id = :intentId
            """)
    List<QaIntent> findByIntentId(
            @Param("intentId") Integer intentId
    );

    /**
     * Check whether a QA pair is linked to an intent.
     */
    @Query("""
            SELECT CASE WHEN COUNT(qi) > 0 THEN true ELSE false END
            FROM qaIntentEntity qi
            WHERE qi.qaPair.id = :qaId
              AND qi.intent.id = :intentId
            """)
    boolean existsByQaIdAndIntentId(
            @Param("qaId") Integer qaId,
            @Param("intentId") Integer intentId
    );

    /**
     * Count QA pairs assigned to one intent.
     */
    @Query("""
            SELECT COUNT(qi)
            FROM qaIntentEntity qi
            WHERE qi.intent.id = :intentId
            """)
    Long countQaPairsByIntentId(@Param("intentId") Integer intentId);

    /**
     * Count intents assigned to one QA pair.
     */
    @Query("""
            SELECT COUNT(qi)
            FROM qaIntentEntity qi
            WHERE qi.qaPair.id = :qaId
            """)
    Long countIntentsByQaId(@Param("qaId") Integer qaId);
}
