package com.example.platform.modules.qa.mapper;

import com.example.platform.modules.qa.dto.response.IntentResponse;
import com.example.platform.modules.qa.model.Intent;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface IntentMapper {

    IntentResponse toResponse(Intent intent);

    List<IntentResponse> toResponses(List<Intent> intents);
}