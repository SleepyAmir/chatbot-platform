package com.example.platform.mongo.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "ocr_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrImageDocument {

    @Id
    private String id;

    private byte[] imageData;

    private String extractedText;

    private LocalDateTime createdAt;
}