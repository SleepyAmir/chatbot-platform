package com.example.platform.modules.ocr.client;

import com.example.platform.modules.ocr.dto.OCRResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;


@Service
@Profile("prod")
public class RealOCRClient implements OCRClient {

    private final WebClient webClient;

    public RealOCRClient(
            WebClient.Builder builder,
            @Value("${clients.ocr.base-url}") String baseUrl
    ) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public OCRResponse extract(MultipartFile file) {
        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

            bodyBuilder.part("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            return webClient.post()
                    .uri("/ocr")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(bodyBuilder.build())
                    .retrieve()
                    .bodyToMono(OCRResponse.class)
                    .block();

        } catch (Exception e) {
            throw new RuntimeException("OCR service failed", e);
        }
    }
}
