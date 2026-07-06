package com.example.platform.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS باز فقط برای توسعه‌ی محلی (local/dev) -- اجازه می‌دهد صفحات تست
 * مستقل (مثلاً یک فایل .html باز شده با file:// یا سرو شده از یک پورت دیگر،
 * نگاه کنید به index.html) مستقیماً به /api/** از مرورگر فچ بزنند.
 *
 * این کانفیگ عمداً در پروفایل prod فعال نیست؛ برای استقرار واقعی، origin های
 * مجاز باید صریح و محدود تعریف شوند، نه با allowedOriginPatterns("*").
 */
@Configuration
@Profile({"local", "dev"})
public class DevCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}