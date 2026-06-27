package com.example.platform.modules.chatlog.controller;

@RestController

@RequiredArgsConstructor

@RequestMapping("/api")

public class FeedbackController {

private final FeedbackService service;

@PostMapping("/feedback")

public Feedback submit(

@RequestBody FeedbackRequest request

){

return service.submitFeedback(

request.getLogId(),

request.getRating(),

request.getComment()

);

}

}