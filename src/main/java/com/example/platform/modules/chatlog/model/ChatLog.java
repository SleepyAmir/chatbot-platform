package com.example.platform.modules.chatlog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "user_question")
    private String userQuestion;

    @Column(name = "matched_qa")
    private Long matchedQa;

    @Column(name = "answer_returned")
    private String answerReturned;

    private Float confidence;

    @Column(name = "model_used")
    private String modelUsed;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}