package com.example.platform.modules.chatlog.model;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="feedback")

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "log_id")

    private ChatLog log;

    private Short rating;

    private String comment;

    @Column(name="created_at")
    private LocalDateTime createdAt;

}