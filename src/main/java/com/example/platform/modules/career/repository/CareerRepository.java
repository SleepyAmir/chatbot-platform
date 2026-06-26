package com.example.platform.modules.career.repository;

import com.example.platform.modules.career.model.Career;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerRepository extends JpaRepository<Career, Integer> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Career c WHERE LOWER(c.title) = LOWER(:title)")
    boolean existsByTitleIgnoreCase(@Param("title") String title);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Career c WHERE LOWER(c.title) = LOWER(:title) AND c.id <> :id")
    boolean existsByTitleIgnoreCaseAndIdNot(@Param("title") String title, @Param("id") Integer id);

    @Query("""
            SELECT c
            FROM Career c
            WHERE :keyword IS NULL
               OR :keyword = ''
               OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(c.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(c.sourceUrl, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Career> searchCareers(@Param("keyword") String keyword, Pageable pageable);
}
