package com.example.platform.modules.qa.mapper;

import com.example.platform.modules.qa.dto.response.IntentResponse;
import com.example.platform.modules.qa.dto.response.QaIntentResponse;
import com.example.platform.modules.qa.model.QaIntent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Maps QA-intent join entities to relationship DTOs.
 * Also extracts intent summaries from join rows when needed.
 *
 * <p>Used by QaIntentReadService and QA detail building.
 * Entity: QaIntent | DTO: QaIntentResponse | Tool: MapStruct</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.model.QaIntent
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface QaIntentMapper {

    /**
     * Convert a QA-intent link to relationship response.
     */
    @Mapping(target = "qaId", source = "qaPair.id")
    @Mapping(target = "question", source = "qaPair.question")
    @Mapping(target = "intentId", source = "intent.id")
    @Mapping(target = "intentName", source = "intent.name")
    QaIntentResponse toResponse(QaIntent qaIntent);

    /**
     * Convert QA-intent link list to response DTO list.
     */
    List<QaIntentResponse> toResponses(List<QaIntent> qaIntents);

    /**
     * Extract only the intent part from a QA-intent link.
     */
    @Mapping(target = "id", source = "intent.id")
    @Mapping(target = "name", source = "intent.name")
    IntentResponse toIntentResponse(QaIntent qaIntent);

    /**
     * Convert QA-intent links to intent response DTOs.
     */
    List<IntentResponse> toIntentResponses(List<QaIntent> qaIntents);
}
