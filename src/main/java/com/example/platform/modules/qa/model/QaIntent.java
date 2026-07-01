package com.example.platform.modules.qa.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Represents the join row between a QA pair and an intent.
 * Enables many-to-many classification of prepared questions.
 *
 * <p>Used when filtering QA pairs by intent or listing intents of a QA pair.
 * DB: qa_intents | Phase 1: Read only | PK: qa_id + intent_id</p>
 *
 * @author Mobina
 * @see QaPair
 * @see Intent
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "qaIntentEntity")
@Table(name = "qa_intents")
@IdClass(QaIntentId.class)
public class QaIntent {
    /**
     * QA side of the composite primary key.
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qa_id", nullable = false)
    private QaPair qaPair;

    /**
     * Intent side of the composite primary key.
     */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intent_id", nullable = false)
    private Intent intent;
}

