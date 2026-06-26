package com.example.platform.modules.career.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "career_requirements")
public class CareerRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "career_id", nullable = false, foreignKey = @ForeignKey(name = "fk_career_requirements_career"))
    private Career career;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Column(name = "requirement_text", nullable = false, columnDefinition = "TEXT")
    private String requirementText;

    @Column(name = "embedding", columnDefinition = "vector(384)")
    private float[] embedding;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public CareerRequirement(Career career, Integer chunkIndex, String requirementText, float[] embedding) {
        this.career = career;
        this.chunkIndex = chunkIndex;
        this.requirementText = requirementText;
        this.embedding = embedding;
    }
}
