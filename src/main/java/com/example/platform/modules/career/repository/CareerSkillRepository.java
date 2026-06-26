package com.example.platform.modules.career.repository;

import com.example.platform.modules.career.model.CareerSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerSkillRepository extends JpaRepository<CareerSkill, Integer> {

    List<CareerSkill> findByCareer_IdOrderBySkillNameAsc(Integer careerId);

    List<CareerSkill> findBySkillNameIgnoreCase(String skillName);

    boolean existsByCareer_IdAndSkillNameIgnoreCase(Integer careerId, String skillName);
}
