package com.example.platform.modules.qa.service.impl;

import com.example.platform.modules.qa.dto.response.*;
import com.example.platform.modules.qa.exception.qa_pair.QaPairNotFoundException;
import com.example.platform.modules.qa.mapper.*;
import com.example.platform.modules.qa.model.QaPair;
import com.example.platform.modules.qa.repository.*;
import com.example.platform.modules.qa.service.QaPairReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements read-only business logic for QA pair resources.
 * Builds basic and detailed QA responses from pair, intent, and embedding data.
 *
 * <p>Used by QaPairController as the main QA read/search service.
 * Module: QA Pair | Phase 1: Read/Search only | Transaction: readOnly</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.QaPairReadService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QaPairReadServiceImpl implements QaPairReadService {

    private final QaPairRepository qaPairRepository;
    private final QaEmbeddingRepository qaEmbeddingRepository;
    private final QaIntentRepository qaIntentRepository;

    private final QaPairMapper qaPairMapper;
    private final QaEmbeddingMapper qaEmbeddingMapper;
    private final QaIntentMapper qaIntentMapper;


    /**
     * Read all QA pairs with optional course data.
     */
    @Override
    public List<QaPairResponse> getAllQaPairs() {
        return qaPairMapper.toResponses(qaPairRepository.findAllWithCourse());
    }

    /**
     * Read one QA pair or throw not-found.
     */
    @Override
    public QaPairResponse getQaPairById(Integer id) {
        QaPair qaPair = qaPairRepository.findWithCourseById(id)
                .orElseThrow(() -> QaPairNotFoundException.byId(id));
        return qaPairMapper.toResponse(qaPair);
    }

    /**
     * Build the full QA detail response from pair, intents, and embedding metadata.
     */
    @Override
    public QaPairDetailResponse getQaPairDetailById(Integer id) {
        QaPair qaPair = qaPairRepository.findWithCourseById(id)
                .orElseThrow(() -> QaPairNotFoundException.byId(id));

        List<IntentResponse> intents = qaIntentMapper.toIntentResponses(qaIntentRepository.findByQaId(id));
        QaEmbeddingResponse embedding = qaEmbeddingRepository.findByQaId(id)
                .map(qaEmbeddingMapper::toResponse)
                .orElse(null);

        return qaPairMapper.toDetailResponse(qaPair, intents, embedding);
    }

    /**
     * Search QA pairs by keyword.
     */
    @Override
    public List<QaPairResponse> searchQaPairs(String keyword) {
        return qaPairRepository.searchQaPairs(keyword)
                .stream()
                .map(qaPairMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Filter QA pairs by course id.
     */
    @Override
    public List<QaPairResponse> getQaPairsByCourseId(Integer courseId) {
        return qaPairRepository.findByCourseId(courseId)
                .stream()
                .map(qaPairMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Filter QA pairs by intent name.
     */
    @Override
    public List<QaPairResponse> getQaPairsByIntentName(String intentName) {
        return qaPairRepository.findByIntentName(intentName)
                .stream()
                .map(qaPairMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Count all QA pairs.
     */
    @Override
    public long countQaPairs() {
        return qaPairRepository.count();
    }

    /**
     * Check duplicate questions, ignoring case.
     */
    @Override
    public boolean existsByQuestion(String question) {
        return qaPairRepository.existsByQuestionIgnoreCase(question);
    }
    /**
     * Return only the intents connected to one QA pair.
     */
    @Override
    public List<IntentResponse> getIntentsByQaId(Integer qaId) {
        return qaIntentMapper.toIntentResponses(qaIntentRepository.findByQaId(qaId));
    }
}
