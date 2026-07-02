package com.example.platform.modules.chatlog.mapper;

import com.example.platform.modules.chatlog.dto.request.FeedbackRequest;
import com.example.platform.modules.chatlog.dto.response.FeedbackResponse;
import com.example.platform.modules.chatlog.model.ChatLog;
import com.example.platform.modules.chatlog.model.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class FeedbackMapper {

    public Feedback toEntity(FeedbackRequest request, ChatLog log, OffsetDateTime createdAt) {
        if (request == null) {
            return null;
        }

        return Feedback.builder()
                .log(log)
                .rating(request.rating())
                .comment(request.comment())
                .createdAt(createdAt)
                .build();
    }

    @Mapping(source = "log.id", target = "logId")
    public abstract FeedbackResponse toResponse(Feedback feedback);
}
