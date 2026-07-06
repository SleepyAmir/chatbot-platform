package com.example.platform.modules.embedding.client;

import java.util.List;

public interface EmbeddingClient {
    List<Double> embed(String text);
}

