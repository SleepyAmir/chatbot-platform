package com.example.platform.modules.qa.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "qaIntentEntity")
@Table(name = "qa_intents")
@IdClass(QaIntentId.class)
public class QaIntent {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qa_id", nullable = false)
    private QaPair qaPair;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intent_id", nullable = false)
    private Intent intent;
}


