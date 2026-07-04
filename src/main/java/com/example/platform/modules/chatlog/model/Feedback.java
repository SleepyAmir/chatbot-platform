package com.example.platform.modules.chatlog.model;

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
 * Thumbs-up / thumbs-down feedback on a logged chatbot answer.
 *
 * <p>DB: feedback | PK: id (Integer) | log_id references chat_logs(id), ON DELETE CASCADE.
 * A single chat log can receive more than one feedback row (no unique constraint
 * on log_id in the DB), so the relation is many-to-one, not one-to-one.</p>
 *
 * @see ChatLog
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id", nullable = false)
    private ChatLog log;

    /**
     * Only -1 (negative) or 1 (positive) is allowed, enforced at the DB
     * level too (chk_feedback_rating). Validated in the service layer since
     * a two-value CHECK constraint isn't a good fit for bean validation.
     */
    @Column(name = "rating", nullable = false)
    private Short rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
