package com.example.platform.modules.qa.mapper;

import com.example.platform.modules.qa.dto.response.QaEmbeddingResponse;
import com.example.platform.modules.qa.model.QaEmbedding;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Maps QA embedding entities to safe metadata responses.
 * Converts stored vector information without returning the full vector.
 *
 * <p>Used by embedding services and QA detail responses.
 * Entity: QaEmbedding | DTO: QaEmbeddingResponse | Tool: MapStruct</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.model.QaEmbedding
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface QaEmbeddingMapper {

    /**
     * Convert embedding entity to metadata response.
     */
    @Mapping(target = "qaId", source = "qaPair.id")
    @Mapping(
            target = "hasEmbedding",
            expression = "java(qaEmbedding.getEmbedding() != null && !qaEmbedding.getEmbedding().isBlank())"
    )
    @Mapping(target = "dimension", constant = "384")
    QaEmbeddingResponse toResponse(QaEmbedding qaEmbedding);

    /**
     * Convert embedding entity list to metadata DTO list.
     */
    List<QaEmbeddingResponse> toResponses(List<QaEmbedding> qaEmbeddings);
}
