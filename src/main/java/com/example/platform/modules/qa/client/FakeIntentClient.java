package com.example.platform.modules.qa.client;

import com.example.platform.common.constant.ChatIntents;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"local"})
public class FakeIntentClient implements IntentClient {

    @Override
    public String detectIntent(String question) {

        if (question == null || question.isBlank()) {
            return ChatIntents.LLM;
        }

        String q = question.trim().toLowerCase();

        // قیمت
        if (q.contains("قیمت")
                || q.contains("هزینه")
                || q.contains("شهریه")
                || q.contains("چنده")
                || q.contains("چقدر")) {
            return ChatIntents.PRICING;
        }

        // جستجوی کلاس / دوره
        if (q.contains("کلاس")
                || q.contains("دوره")
                || q.contains("آموزش")
                || q.contains("ثبت نام")
                || q.contains("جاوا")
                || q.contains("java")
                || q.contains("پایتون")
                || q.contains("python")
                || q.contains("javascript")
                || q.contains("جاوااسکریپت")) {
            return ChatIntents.CLASS_SEARCH;
        }

        // سوالات عمومی
        if (q.contains("ساعت کاری")
                || q.contains("آدرس")
                || q.contains("شماره تماس")
                || q.contains("پشتیبانی")) {
            return ChatIntents.FAQ;
        }

        // مهم: پیش‌فرض باید LLM باشد، نه FAQ
        return ChatIntents.LLM;
    }
}
