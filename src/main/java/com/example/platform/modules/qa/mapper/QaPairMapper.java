package com.example.platform.modules.qa.mapper;

import com.example.platform.modules.qa.dto.response.IntentResponse;
import com.example.platform.modules.qa.dto.response.QaEmbeddingResponse;
import com.example.platform.modules.qa.dto.response.QaPairDetailResponse;
import com.example.platform.modules.qa.dto.response.QaPairResponse;
import com.example.platform.modules.qa.model.QaPair;
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
public interface QaPairMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    QaPairResponse toResponse(QaPair qaPair);

    List<QaPairResponse> toResponses(List<QaPair> qaPairs);

    @Mapping(target = "id", source = "qaPair.id")
    @Mapping(target = "question", source = "qaPair.question")
    @Mapping(target = "answer", source = "qaPair.answer")
    @Mapping(target = "courseId", source = "qaPair.course.id")
    @Mapping(target = "courseName", source = "qaPair.course.name")
    @Mapping(target = "intents", source = "intents")
    @Mapping(target = "embedding", source = "embedding")
    QaPairDetailResponse toDetailResponse(
            QaPair qaPair,
            List<IntentResponse> intents,
            QaEmbeddingResponse embedding
    );
}