package com.example.platform.modules.cache.service;

import com.example.platform.modules.cache.dto.FrequentQueryDto;

import java.util.List;
/// /
public interface FrequentQueryService {

    void trackQuery(String query);

    List<FrequentQueryDto> getTopQueries(int limit);

    void flushAllCache();
}
