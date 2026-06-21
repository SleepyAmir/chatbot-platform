package com.example.platform.modules.embedding.service;

import com.example.platform.modules.embedding.client.EmbeddingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final EmbeddingClient embeddingClient;

    public List<Double> embed(String text) {
        return embeddingClient.embed(text);
    }
}
