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

/**
 * Maps QA pair entities to basic and detailed response DTOs.
 * Adds course summary and externally prepared related data to responses.
 *
 * <p>Used by QaPairReadService for list and detail views.
 * Entity: QaPair | DTOs: QaPairResponse, QaPairDetailResponse | Tool: MapStruct</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.model.QaPair
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface QaPairMapper {

    /**
     * Convert QA pair to basic response with course summary.
     */
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    QaPairResponse toResponse(QaPair qaPair);

    /**
     * Convert QA pair list to basic response DTO list.
     */
    List<QaPairResponse> toResponses(List<QaPair> qaPairs);

    /**
     * Build detail response from QA pair plus extra related data.
     */
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
