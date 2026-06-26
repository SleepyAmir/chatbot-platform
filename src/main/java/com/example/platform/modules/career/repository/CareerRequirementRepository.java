package com.example.platform.modules.career.repository;

import com.example.platform.modules.career.model.CareerRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerRequirementRepository extends JpaRepository<CareerRequirement, Integer> {

    List<CareerRequirement> findByCareer_IdOrderByChunkIndexAsc(Integer careerId);

    boolean existsByCareer_IdAndChunkIndex(Integer careerId, Integer chunkIndex);

    @Query(value = """
            SELECT
                requirement_id AS requirementId,
                career_id AS careerId,
                career_title AS careerTitle,
                chunk_index AS chunkIndex,
                requirement_text AS requirementText,
                similarity,
                created_at AS createdAt
            FROM search_career_requirements(CAST(:embedding AS vector), :topK, :minSimilarity)
            """, nativeQuery = true)
    List<CareerRequirementSearchProjection> searchSimilar(
            @Param("embedding") String embedding,
            @Param("topK") Integer topK,
            @Param("minSimilarity") Double minSimilarity
    );
}
