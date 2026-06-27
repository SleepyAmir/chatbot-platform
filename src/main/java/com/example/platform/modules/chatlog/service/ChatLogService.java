package com.example.platform.modules.chatlog.service;

public interface ChatLogService {

ChatLog saveLog(
ChatLogRequest dto
);

ChatLog getLogById(
Long id
);

Page<ChatLog>
getRecentLogs(
Pageable pageable
);

}