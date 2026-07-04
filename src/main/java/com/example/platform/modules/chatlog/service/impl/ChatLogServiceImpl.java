package com.example.platform.modules.chatlog.service.impl;

import com.example.platform.common.exception.ResourceNotFoundException;
import com.example.platform.modules.chatlog.dto.request.ChatLogRequest;
import com.example.platform.modules.chatlog.dto.response.ChatLogResponse;
import com.example.platform.modules.chatlog.mapper.ChatLogMapper;
import com.example.platform.modules.chatlog.model.ChatLog;
import com.example.platform.modules.chatlog.repository.ChatLogRepository;
import com.example.platform.modules.chatlog.service.ChatLogService;
import com.example.platform.modules.qa.model.QaPair;
import com.example.platform.modules.qa.repository.QaPairRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatLogServiceImpl implements ChatLogService {

    private final ChatLogRepository chatLogRepository;
    private final QaPairRepository qaPairRepository;
    private final ChatLogMapper chatLogMapper;

    @Override
    @Transactional
    public ChatLogResponse saveLog(ChatLogRequest request) {
        QaPair matchedQa = resolveMatchedQa(request.matchedQaId());

        ChatLog chatLog = chatLogMapper.toEntity(request, matchedQa, OffsetDateTime.now());
        ChatLog savedLog = chatLogRepository.save(chatLog);

        log.info("Saved chat log id={} sessionId={} matchedQaId={}",
                savedLog.getId(), savedLog.getSessionId(), request.matchedQaId());

        return chatLogMapper.toResponse(savedLog);
    }

    @Override
    public ChatLogResponse getLogById(Integer id) {
        ChatLog chatLog = chatLogRepository.findWithMatchedQaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChatLog not found with id: " + id));
        return chatLogMapper.toResponse(chatLog);
    }

    @Override
    public Page<ChatLogResponse> getRecentLogs(Pageable pageable) {
        return chatLogRepository.findAll(pageable)
                .map(chatLogMapper::toResponse);
    }

    @Override
    public Page<ChatLogResponse> getLogsBySession(String sessionId, Pageable pageable) {
        return chatLogRepository.findBySessionId(sessionId, pageable)
                .map(chatLogMapper::toResponse);
    }

    @Override
    public List<ChatLogResponse> getLogsBySession(String sessionId) {
        return chatLogRepository.findBySessionIdOrderByCreatedAtDesc(sessionId)
                .stream()
                .map(chatLogMapper::toResponse)
                .toList();
    }

    @Override
    public ChatLog getRequiredLogEntity(Integer id) {
        return chatLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChatLog not found with id: " + id));
    }

    private QaPair resolveMatchedQa(Integer matchedQaId) {
        if (matchedQaId == null) {
            return null;
        }
        return qaPairRepository.findById(matchedQaId)
                .orElseThrow(() -> new ResourceNotFoundException("QaPair not found with id: " + matchedQaId));
    }
}
