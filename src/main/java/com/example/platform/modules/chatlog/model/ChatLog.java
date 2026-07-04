package com.example.platform.modules.chatlog.model;

import com.example.platform.modules.qa.model.QaPair;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Represents one logged chatbot interaction: the user's question, the answer
 * returned, and (optionally) which prepared QA pair it was matched to.
 *
 * <p>DB: chat_logs | PK: id (Integer) | matched_qa_id references qa_pairs(id), ON DELETE SET NULL</p>
 *
 * @see Feedback
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "chat_logs")
public class ChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "session_id", nullable = false, columnDefinition = "TEXT")
    private String sessionId;

    @Column(name = "user_question", nullable = false, columnDefinition = "TEXT")
    private String userQuestion;

    /**
     * The QA pair this question was matched to, if any. Nullable: a log can
     * be created even when nothing matched above the confidence threshold.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_qa_id", nullable = true)
    private QaPair matchedQa;

    @Column(name = "answer_returned", nullable = false, columnDefinition = "TEXT")
    private String answerReturned;

    /**
     * Match confidence in the [0, 1] range, enforced at the DB level too
     * (chk_chat_logs_confidence_range).
     */
    @Column(name = "confidence")
    private Float confidence;

    @Column(name = "model_used")
    private String modelUsed;

    /**
     * response_time_ms is a Postgres INT (4 bytes) -> Java Integer, not Long.
     */
    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
