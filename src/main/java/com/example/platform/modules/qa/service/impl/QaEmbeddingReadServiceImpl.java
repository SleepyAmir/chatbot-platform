package com.example.platform.modules.qa.service.impl;

import com.example.platform.modules.qa.dto.response.QaEmbeddingResponse;
import com.example.platform.modules.qa.exception.qa_embedding.QaEmbeddingNotFoundException;
import com.example.platform.modules.qa.mapper.QaEmbeddingMapper;
import com.example.platform.modules.qa.model.QaEmbedding;
import com.example.platform.modules.qa.repository.QaEmbeddingRepository;
import com.example.platform.modules.qa.service.QaEmbeddingReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements read-only business logic for QA embedding metadata.
 * Fetches embedding records and hides the full vector in responses.
 *
 * <p>Used by QaEmbeddingController and QA detail composition.
 * Module: QA/Embedding | Phase 1: Read only | Transaction: readOnly</p>
 *
 * @author Mobina
 * @see com.example.platform.modules.qa.service.QaEmbeddingReadService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QaEmbeddingReadServiceImpl implements QaEmbeddingReadService {

    private final QaEmbeddingRepository qaEmbeddingRepository;
    private final QaEmbeddingMapper qaEmbeddingMapper;

    /**
     * Read all embedding metadata with QA context.
     */
    @Override
    public List<QaEmbeddingResponse> getAllEmbeddings() {
        return qaEmbeddingRepository.findAllWithQaPair()
                .stream()
                .map(qaEmbeddingMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Read one embedding by id or throw not-found.
     */
    @Override
    public QaEmbeddingResponse getEmbeddingById(Integer id) {
        QaEmbedding embedding = qaEmbeddingRepository.findWithQaPairById(id)
                .orElseThrow(() -> QaEmbeddingNotFoundException.byId(id));
        return qaEmbeddingMapper.toResponse(embedding);
    }

    /**
     * Read embedding metadata for a QA pair or throw not-found.
     */
    @Override
    public QaEmbeddingResponse getEmbeddingByQaId(Integer qaId) {
        QaEmbedding embedding = qaEmbeddingRepository.findByQaId(qaId)
                .orElseThrow(() -> QaEmbeddingNotFoundException.byQaId(qaId));
        return qaEmbeddingMapper.toResponse(embedding);
    }

    /**
     * Filter embeddings by model name.
     */
    @Override
    public List<QaEmbeddingResponse> getEmbeddingsByModelName(String modelName) {
        return qaEmbeddingRepository.findByModelName(modelName)
                .stream()
                .map(qaEmbeddingMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Check whether a QA pair has stored embedding data.
     */
    @Override
    public boolean existsByQaId(Integer qaId) {
        return qaEmbeddingRepository.existsByQaId(qaId);
    }

    /**
     * Count all embedding rows.
     */
    @Override
    public long countEmbeddings() {
        return qaEmbeddingRepository.count();
    }
}
