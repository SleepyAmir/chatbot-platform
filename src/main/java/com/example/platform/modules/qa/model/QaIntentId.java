package com.example.platform.modules.qa.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Represents the composite primary key of the QA-intent join table.
 * Contains the QA pair id and intent id parts used by JpaRepository.
 *
 * <p>Used by QaIntent as its IdClass definition.
 * DB: qa_intents | Phase 1: Read only | Fields: qaPair, intent</p>
 *
 * @author Mobina
 * @see QaIntent
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class QaIntentId implements Serializable {
    /**
     * Matches QaIntent.qaPair.
     */
    private Integer qaPair;
    /**
     * Matches QaIntent.intent.
     */
    private Integer intent;
}
