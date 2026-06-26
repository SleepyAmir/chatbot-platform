package com.example.platform.modules.career.repository;

import java.time.OffsetDateTime;

public interface CareerRequirementSearchProjection {

    Integer getRequirementId();

    Integer getCareerId();

    String getCareerTitle();

    Integer getChunkIndex();

    String getRequirementText();

    Double getSimilarity();

    OffsetDateTime getCreatedAt();
}
