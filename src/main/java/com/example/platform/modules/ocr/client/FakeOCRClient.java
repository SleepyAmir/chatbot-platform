package com.example.platform.modules.ocr.client;

import com.example.platform.modules.ocr.dto.OCRResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile({"local"})
public class FakeOCRClient implements OCRClient {

    @Override
    public OCRResponse extract(MultipartFile file) {
        return OCRResponse.builder()
                .text("متن تستی استخراج شده از تصویر")
                .confidence(0.95)
                .build();
    }
}
