package com.example.platform.modules.chatlog.controller;

import com.example.platform.common.response.ApiResponse;
import com.example.platform.common.web.PageableUtils;
import com.example.platform.modules.chatlog.dto.request.ChatLogRequest;
import com.example.platform.modules.chatlog.dto.response.ChatLogResponse;
import com.example.platform.modules.chatlog.service.ChatLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-logs")
public class ChatLogController {

    /**
     * Only these ChatLog properties may be used in ?sort= - see PageableUtils
     * for why unfiltered sort input is dangerous.
     */
    private static final Set<String> CHAT_LOG_SORTABLE_PROPERTIES = Set.of(
            "id", "sessionId", "confidence", "responseTimeMs", "createdAt"
    );

    private final ChatLogService chatLogService;

    /**
     * Called by the chat pipeline right after an answer is produced, to
     * persist the interaction for reporting/feedback.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChatLogResponse> createLog(@Valid @RequestBody ChatLogRequest request) {
        return ApiResponse.ok("Chat log saved successfully", chatLogService.saveLog(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ChatLogResponse> getLog(@PathVariable Integer id) {
        return ApiResponse.ok(chatLogService.getLogById(id));
    }

    /**
     * Lists chat logs, optionally filtered by session, newest first by default.
     */
    @GetMapping
    public ApiResponse<Page<ChatLogResponse>> getLogs(
            @RequestParam(required = false) String sessionId,
            Pageable pageable
    ) {
        Pageable safePageable = withDefaultSort(
                PageableUtils.sanitizeSort(pageable, CHAT_LOG_SORTABLE_PROPERTIES)
        );

        Page<ChatLogResponse> result = (sessionId == null || sessionId.isBlank())
                ? chatLogService.getRecentLogs(safePageable)
                : chatLogService.getLogsBySession(sessionId, safePageable);

        return ApiResponse.ok(result);
    }

    @GetMapping("/session/{sessionId}")
    public ApiResponse<List<ChatLogResponse>> getLogsForSession(@PathVariable String sessionId) {
        return ApiResponse.ok(chatLogService.getLogsBySession(sessionId));
    }

    /**
     * Chat logs are read as a conversation timeline, so default to newest
     * first when the caller didn't specify a sort.
     */
    private Pageable withDefaultSort(Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable;
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
    }
}
