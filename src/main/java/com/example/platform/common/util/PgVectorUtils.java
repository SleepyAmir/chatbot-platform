package com.example.platform.common.util;

import java.util.List;

/**
 * تبدیل یک بردار embedding جاوا (List&lt;Double&gt;) به لیترال متنی pgvector
 * (مثلاً "[0.12,0.98,...]") که در یک native query با CAST(:embedding AS vector(384))
 * قابل استفاده است.
 *
 * چرا به این شکل و نه با تایپ سفارشی Hibernate؟ چون ساده، بدون وابستگی اضافه و
 * برای حجم کوچک/متوسط درخواست‌ها کاملاً کافی است؛ اگر بعداً نیاز به batch insert
 * سنگین شد، می‌توان به com.pgvector.PGvector (که در pom.xml از قبل هست ولی
 * استفاده نمی‌شود) مهاجرت کرد.
 */
public final class PgVectorUtils {

    private PgVectorUtils() {
    }

    public static String toVectorLiteral(List<Double> embedding) {
        if (embedding == null || embedding.isEmpty()) {
            throw new IllegalArgumentException("Embedding must not be null or empty");
        }

        StringBuilder sb = new StringBuilder(embedding.size() * 8 + 2);
        sb.append('[');
        for (int i = 0; i < embedding.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(embedding.get(i));
        }
        sb.append(']');
        return sb.toString();
    }
}