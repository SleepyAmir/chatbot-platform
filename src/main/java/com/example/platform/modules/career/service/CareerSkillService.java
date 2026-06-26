package com.example.platform.modules.career.service;

import com.example.platform.modules.career.dto.request.CareerSkillRequest;
import com.example.platform.modules.career.dto.response.CareerSkillResponse;

import java.util.List;

public interface CareerSkillService {

    CareerSkillResponse addSkillToCareer(Integer careerId, CareerSkillRequest request);

    List<CareerSkillResponse> getSkillsByCareer(Integer careerId);

    List<CareerSkillResponse> getSkillsByName(String skillName);
}
