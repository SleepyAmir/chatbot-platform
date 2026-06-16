package com.example.platform.modules.ocr.service.impl;

import com.example.platform.modules.ocr.service.OcrService;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;

@Service
public class OcrServiceImpl implements OcrService {

    /**
     * مسیر tessdata — از application.yaml خوانده می‌شود.
     * • در local dev: خالی بگذار → از classpath/tessdata بارگذاری می‌شود
     * • در Docker: OCR_TESSDATA_PATH=/usr/share/tesseract-ocr/5/tessdata
     */
    @Value("${ocr.tessdata-path:}")
    private String tessdataPath;

    @Value("${ocr.language:fas}")
    private String language;

    @Override
    public String extractTextFromImage(byte[] imageData) {
        try {
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath(resolveTessdataPath());
            tesseract.setLanguage(language);

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            if (image == null) {
                throw new RuntimeException("فایل آپلود شده یک تصویر معتبر نیست");
            }

            return tesseract.doOCR(image);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("خطا در پردازش OCR: " + e.getMessage(), e);
        }
    }

    /**
     * اولویت: env var → classpath
     */
    private String resolveTessdataPath() {
        if (tessdataPath != null && !tessdataPath.isBlank()) {
            return tessdataPath;
        }

        // fallback به classpath (برای local dev)
        URL resource = getClass().getClassLoader().getResource("tessdata");
        if (resource == null) {
            throw new RuntimeException(
                    "tessdata پیدا نشد. یکی از دو کار را انجام دهید:\n" +
                            "  ١) پوشه tessdata را در src/main/resources/ قرار دهید (برای local dev)\n" +
                            "  ٢) متغیر OCR_TESSDATA_PATH را در Docker/env تنظیم کنید"
            );
        }
        return resource.getPath();
    }
}