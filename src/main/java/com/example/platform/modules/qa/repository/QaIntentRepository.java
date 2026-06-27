package com.example.platform.modules.qa.repository;

import com.example.platform.modules.qa.model.QaIntent;
import com.example.platform.modules.qa.model.QaIntentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QaIntentRepository extends JpaRepository<QaIntent, QaIntentId> {

    @Query("""
            SELECT qi
            FROM qaIntentEntity qi
            JOIN FETCH qi.qaPair q
            JOIN FETCH qi.intent i
            """)
    List<QaIntent> findAllWithQaPairAndIntent();

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

    @Query("""
            SELECT COUNT(qi)
            FROM qaIntentEntity qi
            WHERE qi.intent.id = :intentId
            """)
    Long countQaPairsByIntentId(@Param("intentId") Integer intentId);

    @Query("""
            SELECT COUNT(qi)
            FROM qaIntentEntity qi
            WHERE qi.qaPair.id = :qaId
            """)
    Long countIntentsByQaId(@Param("qaId") Integer qaId);
}
