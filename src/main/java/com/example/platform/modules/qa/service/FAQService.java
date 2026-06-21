package com.example.platform.modules.qa.service;

import com.example.platform.modules.qa.model.FAQ;
import org.springframework.stereotype.Service;

@Service
public class FAQService {

    public FAQ find(String query) {

        if (query == null || query.isBlank()) {
            return null;
        }

        if (query.contains("قیمت") || query.contains("هزینه") || query.contains("شهریه")) {
            return FAQ.builder()
                    .question(query)
                    .answer("برای اطلاع از هزینه دقیق لطفاً نوع دوره یا کلاس موردنظر را مشخص کنید.")
                    .build();
        }

        if (query.contains("ثبت نام") || query.contains("ثبت‌نام")) {
            return FAQ.builder()
                    .question(query)
                    .answer("برای ثبت‌نام می‌توانید از بخش دوره‌ها، کلاس موردنظر را انتخاب کنید و فرم ثبت‌نام را تکمیل کنید.")
                    .build();
        }

        return null;
    }
}

