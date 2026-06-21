package com.example.platform.modules.ocr.client;

import com.example.platform.modules.ocr.dto.OCRResponse;
import org.springframework.web.multipart.MultipartFile;

public interface OCRClient {

    OCRResponse extract(MultipartFile file);
}
