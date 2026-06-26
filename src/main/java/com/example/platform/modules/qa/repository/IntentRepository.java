package com.example.platform.modules.qa.repository;

import com.example.platform.modules.qa.model.Intent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IntentRepository extends JpaRepository<Intent, Integer> {

    @Query("""
            SELECT i
            FROM intentEntity i
            """)
    List<Intent> findAllIntents();

    @Query("""
            SELECT i
            FROM intentEntity i
            WHERE i.id = :id
            """)
    Optional<Intent> findIntentById(
            @Param("id") Integer id
    );

    @Query("""
            SELECT i
            FROM intentEntity i
            WHERE LOWER(i.name) = LOWER(:name)
            """)
    Optional<Intent> findByNameIgnoreCase(
            @Param("name") String name
    );

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

    @Query("""
            SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END
            FROM intentEntity i
            WHERE LOWER(i.name) = LOWER(:name)
            """)
    boolean existsByNameIgnoreCase(
            @Param("name") String name
    );
}