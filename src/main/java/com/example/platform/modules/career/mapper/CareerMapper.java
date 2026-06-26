package com.example.platform.modules.career.mapper;

import com.example.platform.modules.career.dto.request.CareerRequest;
import com.example.platform.modules.career.dto.response.CareerRequirementResponse;
import com.example.platform.modules.career.dto.response.CareerResponse;
import com.example.platform.modules.career.dto.response.CareerSearchResponse;
import com.example.platform.modules.career.dto.response.CareerSkillResponse;
import com.example.platform.modules.career.dto.response.CourseCareerResponse;
import com.example.platform.modules.career.model.Career;
import com.example.platform.modules.career.model.CareerRequirement;
import com.example.platform.modules.career.model.CareerSkill;
import com.example.platform.modules.career.model.CourseCareer;
import com.example.platform.modules.career.repository.CareerRequirementSearchProjection;
import org.springframework.stereotype.Component;

@Component
public class CareerMapper {

    public Career toEntity(CareerRequest request) {
        return new Career(request.title(), request.description(), request.sourceUrl());
    }

    public CareerResponse toResponse(Career career) {
        return new CareerResponse(
                career.getId(),
                career.getTitle(),
                career.getDescription(),
                career.getSourceUrl(),
                career.getCreatedAt()
        );
    }

    public void updateEntityFromRequest(CareerRequest request, Career career) {
        career.setTitle(request.title());
        career.setDescription(request.description());
        career.setSourceUrl(request.sourceUrl());
    }

    public CareerSkillResponse toSkillResponse(CareerSkill skill) {
        return new CareerSkillResponse(skill.getId(), skill.getCareer().getId(), skill.getSkillName());
    }

    public CareerRequirementResponse toRequirementResponse(CareerRequirement requirement) {
        return new CareerRequirementResponse(
                requirement.getId(),
                requirement.getCareer().getId(),
                requirement.getChunkIndex(),
                requirement.getRequirementText(),
                requirement.getEmbedding() != null,
                requirement.getCreatedAt()
        );
    }

    public CourseCareerResponse toCourseCareerResponse(CourseCareer courseCareer) {
        return new CourseCareerResponse(
                courseCareer.getCourse().getId(),
                courseCareer.getCourse().getName(),
                courseCareer.getCareer().getId(),
                courseCareer.getCareer().getTitle(),
                courseCareer.getRelevance()
        );
    }

    public CareerSearchResponse toSearchResponse(CareerRequirementSearchProjection projection) {
        return new CareerSearchResponse(
                projection.getRequirementId(),
                projection.getCareerId(),
                projection.getCareerTitle(),
                projection.getChunkIndex(),
                projection.getRequirementText(),
                projection.getSimilarity(),
                projection.getCreatedAt()
        );
    }
}
