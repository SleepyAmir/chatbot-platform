package com.example.platform.modules.chatlog.mapper;

import com.example.platform.modules.chatlog.dto.request.ChatLogRequest;
import com.example.platform.modules.chatlog.dto.response.ChatLogResponse;
import com.example.platform.modules.chatlog.model.ChatLog;
import com.example.platform.modules.qa.model.QaPair;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ChatLogMapper {

    /**
     * matchedQa is resolved separately by the service (it needs a repository
     * lookup by matchedQaId), so it's passed in already loaded rather than
     * mapped straight from the request.
     */
    public ChatLog toEntity(ChatLogRequest request, QaPair matchedQa, OffsetDateTime createdAt) {
        if (request == null) {
            return null;
        }

        return ChatLog.builder()
                .sessionId(request.sessionId())
                .userQuestion(request.userQuestion())
                .matchedQa(matchedQa)
                .answerReturned(request.answerReturned())
                .confidence(request.confidence())
                .modelUsed(request.modelUsed())
                .responseTimeMs(request.responseTimeMs())
                .createdAt(createdAt)
                .build();
    }

    @Mapping(source = "matchedQa.id", target = "matchedQaId")
    @Mapping(source = "matchedQa.question", target = "matchedQuestion")
    public abstract ChatLogResponse toResponse(ChatLog chatLog);
}
