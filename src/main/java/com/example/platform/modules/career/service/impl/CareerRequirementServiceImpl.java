package com.example.platform.modules.career.service.impl;

import com.example.platform.modules.career.dto.request.CareerRequirementRequest;
import com.example.platform.modules.career.dto.request.CareerSearchRequest;
import com.example.platform.modules.career.dto.response.CareerRequirementResponse;
import com.example.platform.modules.career.dto.response.CareerSearchResponse;
import com.example.platform.modules.career.mapper.CareerMapper;
import com.example.platform.modules.career.model.Career;
import com.example.platform.modules.career.model.CareerRequirement;
import com.example.platform.modules.career.repository.CareerRequirementRepository;
import com.example.platform.modules.career.service.CareerRequirementService;
import com.example.platform.modules.career.service.CareerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareerRequirementServiceImpl implements CareerRequirementService {

    private static final int DEFAULT_TOP_K = 5;
    private static final double DEFAULT_MIN_SIMILARITY = 0.0;

    private final CareerService careerService;
    private final CareerRequirementRepository careerRequirementRepository;
    private final CareerMapper careerMapper;

    @Override
    @Transactional
    public CareerRequirementResponse addRequirement(Integer careerId, CareerRequirementRequest request) {
        if (careerRequirementRepository.existsByCareer_IdAndChunkIndex(careerId, request.chunkIndex())) {
            throw new IllegalArgumentException("Requirement chunk already exists for career id " + careerId);
        }

        Career career = careerService.getRequiredCareerEntity(careerId);
        CareerRequirement requirement = new CareerRequirement(
                career,
                request.chunkIndex(),
                request.requirementText(),
                toFloatArray(request.embedding())
        );

        return careerMapper.toRequirementResponse(careerRequirementRepository.saveAndFlush(requirement));
    }

    @Override
    public List<CareerRequirementResponse> getRequirementsByCareer(Integer careerId) {
        careerService.getRequiredCareerEntity(careerId);
        return careerRequirementRepository.findByCareer_IdOrderByChunkIndexAsc(careerId)
                .stream()
                .map(careerMapper::toRequirementResponse)
                .toList();
    }

    @Override
    public List<CareerSearchResponse> searchSimilar(CareerSearchRequest request) {
        Integer topK = request.topK() == null ? DEFAULT_TOP_K : request.topK();
        Double minSimilarity = request.minSimilarity() == null ? DEFAULT_MIN_SIMILARITY : request.minSimilarity();

        return careerRequirementRepository.searchSimilar(toVectorLiteral(request.embedding()), topK, minSimilarity)
                .stream()
                .map(careerMapper::toSearchResponse)
                .toList();
    }

    private float[] toFloatArray(List<Float> embedding) {
        if (embedding == null) {
            return null;
        }

        validateEmbeddingValues(embedding);
        float[] values = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            values[i] = embedding.get(i);
        }
        return values;
    }

    private String toVectorLiteral(List<Float> embedding) {
        validateEmbeddingValues(embedding);
        return embedding.stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
    }

    private void validateEmbeddingValues(List<Float> embedding) {
        if (embedding == null || embedding.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Embedding values cannot be null");
        }
    }
}
