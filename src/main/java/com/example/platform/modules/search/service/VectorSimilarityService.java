package com.example.platform.modules.search.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class VectorSimilarityService {

    public double cosine(List<Double> v1, List<Double> v2) {
        if (v1 == null || v2 == null || v1.isEmpty() || v2.isEmpty()) {
            return 0.0;
        }
        if (v1.size() != v2.size()) {
            log.warn("Vector dimension mismatch: {} vs {}", v1.size(), v2.size());
            return 0.0;
        }

        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            normA += Math.pow(v1.get(i), 2);
            normB += Math.pow(v2.get(i), 2);
        }
        double denominator = Math.sqrt(normA) * Math.sqrt(normB);
        return denominator == 0 ? 0.0 : dot / denominator;
    }
}
