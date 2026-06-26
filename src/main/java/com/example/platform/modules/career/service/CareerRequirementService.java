package com.example.platform.modules.career.service;

import com.example.platform.modules.career.dto.request.CareerRequirementRequest;
import com.example.platform.modules.career.dto.request.CareerSearchRequest;
import com.example.platform.modules.career.dto.response.CareerRequirementResponse;
import com.example.platform.modules.career.dto.response.CareerSearchResponse;

import java.util.List;

public interface CareerRequirementService {

    CareerRequirementResponse addRequirement(Integer careerId, CareerRequirementRequest request);

    List<CareerRequirementResponse> getRequirementsByCareer(Integer careerId);

    List<CareerSearchResponse> searchSimilar(CareerSearchRequest request);
}
