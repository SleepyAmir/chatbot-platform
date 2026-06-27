package com.example.platform.modules.chatlog.controller;

@RestController

@RequiredArgsConstructor

@RequestMapping("/api")

public class ChatLogController {

private final ChatLogService service;

@GetMapping("/logs")

public Page<ChatLog>

logs(

Pageable pageable

){

return service.getRecentLogs(

pageable

);

}

@GetMapping("/logs/{id}")

public ChatLog get(

@PathVariable Long id

){

return service.getLogById(id);

}

}