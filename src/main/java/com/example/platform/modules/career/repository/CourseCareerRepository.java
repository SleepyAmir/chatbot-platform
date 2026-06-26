package com.example.platform.modules.career.repository;

import com.example.platform.modules.career.model.CourseCareer;
import com.example.platform.modules.career.model.CourseCareerId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseCareerRepository extends JpaRepository<CourseCareer, CourseCareerId> {

    @EntityGraph(attributePaths = {"career"})
    List<CourseCareer> findByCourse_IdOrderByRelevanceDesc(Integer courseId);

    @EntityGraph(attributePaths = {"course"})
    List<CourseCareer> findByCareer_IdOrderByRelevanceDesc(Integer careerId);

    boolean existsByCourse_IdAndCareer_Id(Integer courseId, Integer careerId);
}
