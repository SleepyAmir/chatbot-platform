package com.example.platform.modules.cache.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CachedQA {
    private String id;
    private String intent;
    private String question;
    private String answer;
    private List<Double> embedding;
    private LocalDateTime createdAt;
}
