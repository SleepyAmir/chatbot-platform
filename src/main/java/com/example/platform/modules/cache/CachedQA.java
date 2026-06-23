package com.example.platform.modules.cache;

import jakarta.persistence.*;
import lombok.*;

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

    private Long hitCount;
}
