package com.example.platform.modules.chatlog.service.impl;

import com.example.platform.common.exception.ResourceNotFoundException;
import com.example.platform.modules.chatlog.dto.ChatLogRequest;
import com.example.platform.modules.chatlog.model.ChatLog;
import com.example.platform.modules.chatlog.repository.ChatLogRepository;
import com.example.platform.modules.chatlog.service.ChatLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class ChatLogServiceImpl implements ChatLogService {
public class ChatLogServiceImpl
        implements ChatLogService {

    private final ChatLogRepository repository;

    @Override
    public ChatLog saveLog(
            ChatLogRequest dto
    ) {

        ChatLog log =

                ChatLog.builder()

                        .sessionId(dto.getSessionId())

                        .userQuestion(dto.getUserQuestion())

                        .matchedQa(dto.getMatchedQa())

                        .answerReturned(dto.getAnswerReturned())

                        .confidence(dto.getConfidence())

                        .modelUsed(dto.getModelUsed())

                        .responseTimeMs(dto.getResponseTimeMs())

                        .createdAt(LocalDateTime.now())

                        .build();

return repository.save(log);

        //injaro nafar-e 4 bayad ezafe kone, ba'd az inke repository.save() aslan kar kard:
        // frequentQueryService.trackQuery(dto.getUserQuestion());
        return null;
            ChatLogRequest request
    ) {

        ChatLog log = ChatLog.builder()
                .sessionId(request.getSessionId())
                .userQuestion(request.getUserQuestion())
                .matchedQa(request.getMatchedQa())
                .answerReturned(request.getAnswerReturned())
                .confidence(request.getConfidence())
                .modelUsed(request.getModelUsed())
                .responseTimeMs(request.getResponseTimeMs())
                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(log);

    }

    @Override
    public ChatLog getLogById(Long id) {

        return repository.findById(id)

                .orElseThrow(

                        () -> new ResourceNotFoundException(

                                "ChatLog not found"

                        )

                );
        return null;
    public ChatLog getLogById(
            Long id
    ) {

        return repository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "ChatLog not found"
                        )
                );

    }

    @Override
    public Page<ChatLog>

    getRecentLogs(

            Pageable pageable

    ) {

        return repository

                .findAllByOrderByCreatedAtDesc(pageable

                );
        return null;
    public Page<ChatLog> getRecentLogs(
            Pageable pageable
    ) {

        return repository.findAllByOrderByCreatedAtDesc(
                pageable
        );

    }

}