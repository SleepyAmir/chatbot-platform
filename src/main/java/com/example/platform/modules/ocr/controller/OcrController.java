package com.example.platform.modules.ocr.controller;


import com.example.platform.common.exception.ResourceNotFoundException;
import com.example.platform.modules.ocr.dto.OcrExtractResponse;
import com.example.platform.modules.ocr.dto.OcrUploadResponse;
import com.example.platform.modules.ocr.repository.OcrImageRepository;
import com.example.platform.modules.ocr.service.OcrService;
import com.example.platform.mongo.document.OcrImageDocument;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/v1/ocr")
public class OcrController {

    private final OcrService ocrService;
    private final OcrImageRepository repository;

    public OcrController(OcrService ocrService, OcrImageRepository repository) {
        this.ocrService = ocrService;
        this.repository = repository;
    }

    // آپلود تصویر و استخراج متن
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<OcrUploadResponse> upload(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("فایلی ارسال نشده است");
        }

        byte[] bytes = file.getBytes();
        String extractedText = ocrService.extractTextFromImage(bytes);

        OcrImageDocument doc = OcrImageDocument.builder()
                .imageData(bytes)
                .extractedText(extractedText)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(doc);

        return ResponseEntity.ok(new OcrUploadResponse(doc.getId(), extractedText));
    }

    // گرفتن متن استخراج‌شده از یک تصویر ذخیره‌شده
    @GetMapping("/{id}")
    public ResponseEntity<OcrExtractResponse> getById(@PathVariable String id) {

        return repository.findById(id)
                .map(doc -> ResponseEntity.ok(
                        new OcrExtractResponse(doc.getId(), doc.getExtractedText())))
                .orElseThrow(() -> new ResourceNotFoundException("OcrImage", id));
    }

    // دانلود تصویر ذخیره‌شده
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> downloadImage(@PathVariable String id) {

        OcrImageDocument doc = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OcrImage", id));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"ocr-image-" + id + ".jpg\"")
                .header("Content-Type", "image/jpeg")
                .body(doc.getImageData());
    }
}
