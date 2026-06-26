package com.example.platform.modules.qa.mapper;

import com.example.platform.modules.qa.dto.response.IntentResponse;
import com.example.platform.modules.qa.dto.response.QaIntentResponse;
import com.example.platform.modules.qa.model.QaIntent;
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
public interface QaIntentMapper {

    @Mapping(target = "qaId", source = "qaPair.id")
    @Mapping(target = "question", source = "qaPair.question")
    @Mapping(target = "intentId", source = "intent.id")
    @Mapping(target = "intentName", source = "intent.name")
    QaIntentResponse toResponse(QaIntent qaIntent);

    List<QaIntentResponse> toResponses(List<QaIntent> qaIntents);

    @Mapping(target = "id", source = "intent.id")
    @Mapping(target = "name", source = "intent.name")
    IntentResponse toIntentResponse(QaIntent qaIntent);

    List<IntentResponse> toIntentResponses(List<QaIntent> qaIntents);
}