package com.example.platform.modules.search.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VectorSearchResult implements RerankService.RerankCandidate {

    private String text;
    private double similarityScore;

    @Override
    public double getSimilarityScore() {
        return similarityScore;
    }

    @Override
    public String getText() {
        return text;
    }
}
