package com.example.platform.modules.ocr.dto;

public record OcrUploadResponse(
        String id,
        String extractedText
) {
}
