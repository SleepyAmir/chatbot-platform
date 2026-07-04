package com.example.platform.modules.qa.dto.response;

/**
 * Represents the basic response for a question-answer pair.
 * Includes the prepared answer and optional course summary.
 *
 * <p>Used by QA list, search, and filter endpoints.
 * DB: qa_pairs | Phase 1: Read/Search only | PK: id (Integer)</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.model.QaPair
 */
public record QaPairResponse(
        Integer id,
        String question,
        String answer,
        Integer courseId,
        String courseName
) {
}