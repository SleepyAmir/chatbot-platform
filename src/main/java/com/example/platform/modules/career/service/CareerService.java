package com.example.platform.modules.career.service;

import com.example.platform.modules.career.dto.request.CareerRequest;
import com.example.platform.modules.career.dto.response.CareerResponse;
import com.example.platform.modules.career.model.Career;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CareerService {

    List<CareerResponse> getAllCareers();

    Page<CareerResponse> getAllCareers(Pageable pageable);

    Page<CareerResponse> searchCareers(String keyword, Pageable pageable);

    CareerResponse getCareerById(Integer id);

    CareerResponse createCareer(CareerRequest request);

    CareerResponse updateCareer(Integer id, CareerRequest request);

    void deleteCareer(Integer id);

    Career getRequiredCareerEntity(Integer id);
}
