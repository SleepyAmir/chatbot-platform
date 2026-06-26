package com.example.platform.modules.qa.model;

import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "qaEmbeddingEntity")
@Table(name = "qa_embeddings")
public class QaEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qa_id", unique = true, nullable = false)
    private QaPair qaPair;

    @Column(columnDefinition = "vector(384)", nullable = false)
    private String embedding;

    @Column(name = "model_name", nullable = false)
    private String modelName;

//    @CreatedDate
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
}

