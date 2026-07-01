package com.example.platform.modules.qa.mapper;

import com.example.platform.modules.qa.dto.response.IntentResponse;
import com.example.platform.modules.qa.model.Intent;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Maps intent entities to response DTOs.
 *
 * <p>Used by IntentReadService for read-only intent responses.
 * Entity: Intent | DTO: IntentResponse | Tool: MapStruct</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.model.Intent
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface IntentMapper {

    /**
     * Convert one intent entity to response DTO.
     */
    IntentResponse toResponse(Intent intent);

    /**
     * Convert intent entity list to response DTO list.
     */
    List<IntentResponse> toResponses(List<Intent> intents);
}
