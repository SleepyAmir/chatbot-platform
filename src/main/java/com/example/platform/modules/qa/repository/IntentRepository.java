package com.example.platform.modules.qa.repository;

import com.example.platform.modules.qa.model.Intent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Provides database access methods for intents.
 * Contains read, search, and existence queries for intent names.
 *
 * <p>Used by IntentReadService to keep persistence logic isolated.
 * DB: intents | Phase 1: Read only | Base: JpaRepository</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.IntentReadService
 */
public interface IntentRepository extends JpaRepository<Intent, Integer> {

    /**
     * Load all intents.
     */
    @Query("""
            SELECT i
            FROM intentEntity i
            """)
    List<Intent> findAllIntents();

    /**
     * Find an intent by id.
     */
    @Query("""
            SELECT i
            FROM intentEntity i
            WHERE i.id = :id
            """)
    Optional<Intent> findIntentById(
            @Param("id") Integer id
    );

    /**
     * Find an intent by exact name, ignoring case.
     */
    @Query("""
            SELECT i
            FROM intentEntity i
            WHERE LOWER(i.name) = LOWER(:name)
            """)
    Optional<Intent> findByNameIgnoreCase(
            @Param("name") String name
    );

    /**
     * Search intent names by keyword.
     */
    @Query("""
            SELECT i
            FROM intentEntity i
            WHERE
                :keyword IS NULL
                OR :keyword = ''
                OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<Intent> searchIntents(
            @Param("keyword") String keyword
    );

    /**
     * Check duplicate intent names, ignoring case.
     */
    @Query("""
            SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END
            FROM intentEntity i
            WHERE LOWER(i.name) = LOWER(:name)
            """)
    boolean existsByNameIgnoreCase(
            @Param("name") String name
    );
}
