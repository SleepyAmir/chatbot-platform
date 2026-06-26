package com.example.platform.modules.qa.service.impl;

import com.example.platform.modules.qa.dto.response.IntentResponse;
import com.example.platform.modules.qa.exception.intent.IntentNotFoundException;
import com.example.platform.modules.qa.mapper.IntentMapper;
import com.example.platform.modules.qa.model.Intent;
import com.example.platform.modules.qa.repository.IntentRepository;
import com.example.platform.modules.qa.service.IntentReadService;
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
public class IntentRaedServiceImpl implements IntentReadService {

    private final IntentRepository intentRepository;
    private final IntentMapper intentMapper;

    @Override
    public List<IntentResponse> getAllIntents() {
        return intentMapper.toResponses(intentRepository.findAllIntents());
    }

    @Override
    public IntentResponse getIntentById(Integer id) {
        Intent intent=intentRepository.findIntentById(id).orElseThrow(()-> IntentNotFoundException.byId(id));
        return intentMapper.toResponse(intent);
    }

    @Override
    public IntentResponse getIntentByName(String name) {
      Intent intent=intentRepository.findByNameIgnoreCase(name).orElseThrow(()-> IntentNotFoundException.byName(name));
      return intentMapper.toResponse(intent);
    }

    @Override
    public List<IntentResponse> searchIntents(String keyword) {
        return intentRepository.searchIntents(keyword)
                .stream()
                .map(intentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countIntents() {
        return intentRepository.count();
    }

    @Override
    public boolean existsByName(String name) {
        return intentRepository.existsByNameIgnoreCase(name);
    }
}
