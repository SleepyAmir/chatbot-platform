package com.example.platform.modules.qa.mapper;

import com.example.platform.modules.qa.dto.response.QaSearchResponse;
import com.example.platform.modules.qa.model.QaEmbedding;
import com.example.platform.modules.qa.model.QaPair;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * Builds semantic-search response DTOs from QA and embedding data.
 * Combines matched content with similarity score and model information.
 *
 * <p>Used for future vector-search result mapping.
 * Output: QaSearchResponse | Phase 1: Search support | Tool: MapStruct</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.dto.response.QaSearchResponse
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface QaSearchMapper {

    /**
     * Build search response from QA pair and explicit score data.
     */
    @Mapping(target = "qaId", source = "qaPair.id")
    @Mapping(target = "question", source = "qaPair.question")
    @Mapping(target = "answer", source = "qaPair.answer")
    @Mapping(target = "courseId", source = "qaPair.course.id")
    @Mapping(target = "courseName", source = "qaPair.course.name")
    @Mapping(target = "similarity", source = "similarity")
    @Mapping(target = "modelName", source = "modelName")
    QaSearchResponse toResponse(
            QaPair qaPair,
            Double similarity,
            String modelName
    );

    /**
     * Build search response directly from embedding entity and score.
     */
    @Mapping(target = "qaId", source = "qaEmbedding.qaPair.id")
    @Mapping(target = "question", source = "qaEmbedding.qaPair.question")
    @Mapping(target = "answer", source = "qaEmbedding.qaPair.answer")
    @Mapping(target = "courseId", source = "qaEmbedding.qaPair.course.id")
    @Mapping(target = "courseName", source = "qaEmbedding.qaPair.course.name")
    @Mapping(target = "similarity", source = "similarity")
    @Mapping(target = "modelName", source = "qaEmbedding.modelName")
    QaSearchResponse toResponse(
            QaEmbedding qaEmbedding,
            Double similarity
    );
}
