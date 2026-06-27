package com.example.platform.modules.chatlog.service.impl;

import com.example.platform.modules.chatlog.service.ChatLogService;

@Service
@RequiredArgsConstructor

public class ChatLogServiceImpl
implements ChatLogService {

private final ChatLogRepository repository;

@Override
public ChatLog saveLog(
ChatLogRequest dto
){

ChatLog log=

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

}

@Override
public ChatLog getLogById(Long id){

return repository.findById(id)

.orElseThrow(

()->new ResourceNotFoundException(

"ChatLog not found"

)

);

}

@Override
public Page<ChatLog>

getRecentLogs(

Pageable pageable

){

return repository

.findAllByOrderByCreatedAtDesc(

pageable

);

}

}