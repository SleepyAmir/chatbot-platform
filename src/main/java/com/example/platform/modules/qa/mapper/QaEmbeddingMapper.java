package com.example.platform.modules.qa.mapper;

import com.example.platform.modules.qa.dto.response.QaEmbeddingResponse;
import com.example.platform.modules.qa.model.QaEmbedding;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface QaEmbeddingMapper {

    @Mapping(target = "qaId", source = "qaPair.id")
    @Mapping(
            target = "hasEmbedding",
            expression = "java(qaEmbedding.getEmbedding() != null && !qaEmbedding.getEmbedding().isBlank())"
    )
    @Mapping(target = "dimension", constant = "384")
    QaEmbeddingResponse toResponse(QaEmbedding qaEmbedding);

    List<QaEmbeddingResponse> toResponses(List<QaEmbedding> qaEmbeddings);
}