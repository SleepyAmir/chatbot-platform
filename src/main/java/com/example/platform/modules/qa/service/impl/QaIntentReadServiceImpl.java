package com.example.platform.modules.qa.service.impl;

import com.example.platform.modules.qa.dto.response.IntentResponse;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QaIntentReadServiceImpl implements QaIntentReadService {

    private final QaIntentRepository qaIntentRepository;
    private final QaIntentMapper qaIntentMapper;
    private final QaPairMapper qaPairMapper;

    @Override
    public List<QaIntentResponse> getAllQaIntents() {
        return qaIntentRepository.findAllWithQaPairAndIntent()
                .stream()
                .map(qaIntentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QaIntentResponse> getIntentsByQaId(Integer qaId) {
        return qaIntentRepository.findByQaId(qaId)
                .stream()
                .map(qaIntentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QaPairResponse> getQaPairsByIntentId(Integer intentId) {
        return qaPairMapper.toResponses(
                qaIntentRepository.findByIntentId(intentId).stream()
                        .map(QaIntent::getQaPair).toList()
        );
    }

    @Override
    public boolean existsByQaIdAndIntentId(Integer qaId, Integer intentId) {
        return qaIntentRepository.existsByQaIdAndIntentId(qaId, intentId);
    }

    @Override
    public long countQaIntents() {
        return qaIntentRepository.count();
    }

    @Override
    public long countQaPairsByIntentId(Integer intentId) {
        return qaIntentRepository.countQaPairsByIntentId(intentId);
    }

    @Override
    public long countIntentsByQaId(Integer qaId) {
        return qaIntentRepository.countIntentsByQaId(qaId);
    }
}