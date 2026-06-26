package com.example.platform.modules.career.service.impl;

import com.example.platform.modules.career.dto.request.CareerSkillRequest;
import com.example.platform.modules.career.dto.response.CareerSkillResponse;
import com.example.platform.modules.career.mapper.CareerMapper;
import com.example.platform.modules.career.model.Career;
import com.example.platform.modules.career.model.CareerSkill;
import com.example.platform.modules.career.repository.CareerSkillRepository;
import com.example.platform.modules.career.service.CareerService;
import com.example.platform.modules.career.service.CareerSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerSkillServiceImpl implements CareerSkillService {

    private final CareerService careerService;
    private final CareerSkillRepository careerSkillRepository;
    private final CareerMapper careerMapper;

    @Override
    @Transactional
    public CareerSkillResponse addSkillToCareer(Integer careerId, CareerSkillRequest request) {
        if (careerSkillRepository.existsByCareer_IdAndSkillNameIgnoreCase(careerId, request.skillName())) {
            throw new IllegalArgumentException("Skill already exists for career id " + careerId);
        }

        Career career = careerService.getRequiredCareerEntity(careerId);
        CareerSkill savedSkill = careerSkillRepository.saveAndFlush(new CareerSkill(career, request.skillName()));
        return careerMapper.toSkillResponse(savedSkill);
    }

    @Override
    public List<CareerSkillResponse> getSkillsByCareer(Integer careerId) {
        careerService.getRequiredCareerEntity(careerId);
        return careerSkillRepository.findByCareer_IdOrderBySkillNameAsc(careerId)
                .stream()
                .map(careerMapper::toSkillResponse)
                .toList();
    }

    @Override
    public List<CareerSkillResponse> getSkillsByName(String skillName) {
        return careerSkillRepository.findBySkillNameIgnoreCase(skillName)
                .stream()
                .map(careerMapper::toSkillResponse)
                .toList();
    }
}
