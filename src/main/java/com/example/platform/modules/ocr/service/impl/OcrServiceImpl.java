package com.example.platform.modules.ocr.service.impl;


import com.example.platform.modules.ocr.service.OcrService;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;

@Service
public class OcrServiceImpl implements OcrService {

    @Override
    public String extractTextFromImage(byte[] imageData) {
        try {
            Tesseract tesseract = new Tesseract();

            URL tessdataUrl = getClass().getClassLoader().getResource("tessdata");
            if (tessdataUrl == null) {
                throw new RuntimeException("پوشه tessdata در classpath پیدا نشد");
            }
            tesseract.setDatapath(tessdataUrl.getPath());
            tesseract.setLanguage("fas");

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
}