package com.example.platform.modules.qa.service.impl;

import com.example.platform.modules.qa.dto.response.QaIntentResponse;
import com.example.platform.modules.qa.dto.response.QaPairResponse;
import com.example.platform.modules.qa.mapper.QaIntentMapper;
import com.example.platform.modules.qa.mapper.QaPairMapper;
import com.example.platform.modules.qa.model.QaIntent;
import com.example.platform.modules.qa.repository.QaIntentRepository;
import com.example.platform.modules.qa.service.QaIntentReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements read-only business logic for QA-intent relationships.
 * Handles relationship reads, reverse lookups, existence checks, and counts.
 *
 * <p>Used by QaIntentController and QA pair detail composition.
 * Module: QA/Intent relation | Phase 1: Read only | Transaction: readOnly</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.QaIntentReadService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QaIntentReadServiceImpl implements QaIntentReadService {

    private final QaIntentRepository qaIntentRepository;
    private final QaIntentMapper qaIntentMapper;
    private final QaPairMapper qaPairMapper;

    /**
     * Read all QA-intent links with both related entities.
     */
    @Override
    public List<QaIntentResponse> getAllQaIntents() {
        return qaIntentRepository.findAllWithQaPairAndIntent()
                .stream()
                .map(qaIntentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Read all intent links for a QA pair.
     */
    @Override
    public List<QaIntentResponse> getIntentsByQaId(Integer qaId) {
        return qaIntentRepository.findByQaId(qaId)
                .stream()
                .map(qaIntentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Read QA pairs connected to one intent.
     */
    @Override
    public List<QaPairResponse> getQaPairsByIntentId(Integer intentId) {
        return qaPairMapper.toResponses(
                qaIntentRepository.findByIntentId(intentId).stream()
                        .map(QaIntent::getQaPair).toList()
        );
    }

    /**
     * Check whether the link between QA and intent exists.
     */
    @Override
    public boolean existsByQaIdAndIntentId(Integer qaId, Integer intentId) {
        return qaIntentRepository.existsByQaIdAndIntentId(qaId, intentId);
    }

    /**
     * Count all QA-intent links.
     */
    @Override
    public long countQaIntents() {
        return qaIntentRepository.count();
    }

    /**
     * Count QA pairs assigned to one intent.
     */
    @Override
    public long countQaPairsByIntentId(Integer intentId) {
        return qaIntentRepository.countQaPairsByIntentId(intentId);
    }

    /**
     * Count intents assigned to one QA pair.
     */
    @Override
    public long countIntentsByQaId(Integer qaId) {
        return qaIntentRepository.countIntentsByQaId(qaId);
    }
}
