package com.example.platform.modules.career.service.impl;

import com.example.platform.common.exception.ResourceNotFoundException;
import com.example.platform.modules.career.dto.request.CareerRequest;
import com.example.platform.modules.career.dto.response.CareerResponse;
import com.example.platform.modules.career.mapper.CareerMapper;
import com.example.platform.modules.career.model.Career;
import com.example.platform.modules.career.repository.CareerRepository;
import com.example.platform.modules.career.service.CareerService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerServiceImpl implements CareerService {

    private final CareerRepository careerRepository;
    private final CareerMapper careerMapper;
    private final EntityManager entityManager;

    @Override
    public List<CareerResponse> getAllCareers() {
        return careerRepository.findAll().stream().map(careerMapper::toResponse).toList();
    }

    @Override
    public Page<CareerResponse> getAllCareers(Pageable pageable) {
        return careerRepository.findAll(pageable).map(careerMapper::toResponse);
    }

    @Override
    public Page<CareerResponse> searchCareers(String keyword, Pageable pageable) {
        return careerRepository.searchCareers(keyword, pageable).map(careerMapper::toResponse);
    }

    @Override
    @Cacheable(cacheNames = "careers", key = "#id")
    public CareerResponse getCareerById(Integer id) {
        return careerMapper.toResponse(getRequiredCareerEntity(id));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "careers", allEntries = true)
    public CareerResponse createCareer(CareerRequest request) {
        validateCareerTitleIsUnique(request.title());
        Career savedCareer = careerRepository.saveAndFlush(careerMapper.toEntity(request));
        entityManager.refresh(savedCareer);
        return careerMapper.toResponse(savedCareer);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "careers", allEntries = true)
    public CareerResponse updateCareer(Integer id, CareerRequest request) {
        Career career = getRequiredCareerEntity(id);
        validateCareerTitleIsUniqueForUpdate(request.title(), id);
        careerMapper.updateEntityFromRequest(request, career);
        return careerMapper.toResponse(careerRepository.saveAndFlush(career));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "careers", allEntries = true)
    public void deleteCareer(Integer id) {
        careerRepository.delete(getRequiredCareerEntity(id));
    }

    @Override
    public Career getRequiredCareerEntity(Integer id) {
        return careerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Career not found with id: " + id));
    }

    private void validateCareerTitleIsUnique(String title) {
        if (careerRepository.existsByTitleIgnoreCase(title)) {
            throw new IllegalArgumentException("Career with this title already exists: " + title);
        }
    }

    private void validateCareerTitleIsUniqueForUpdate(String title, Integer currentCareerId) {
        if (careerRepository.existsByTitleIgnoreCaseAndIdNot(title, currentCareerId)) {
            throw new IllegalArgumentException("Career with this title already exists: " + title);
        }
    }
}
