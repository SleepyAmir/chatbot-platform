package com.example.platform.modules.ocr.service;

public interface OcrService {

    /**
     * متن را از بایت‌های یک تصویر استخراج می‌کند
     *
     * @param imageData بایت‌های تصویر
     * @return متن استخراج‌شده
     */
    String extractTextFromImage(byte[] imageData);
}
