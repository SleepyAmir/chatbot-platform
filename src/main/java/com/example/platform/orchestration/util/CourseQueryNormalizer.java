package com.example.platform.orchestration.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * سوال خام کاربر (مثلاً «قیمت کلاس جاوااسکریپت چنده؟») را به یک کلیدواژه‌ی
 * جست‌وجوی ساده (مثلاً «JavaScript») برای {@code CourseService.searchCourses}
 * تبدیل می‌کند.
 *
 * این منطق قبلاً به‌صورت یک زنجیره‌ی if/else هاردکد داخل OrchestratorService
 * بود که برای هر زبان/دوره‌ی جدید نیاز به تغییر کد و دیپلوی مجدد داشت. اینجا
 * به یک Map داده‌محور تبدیل شده تا افزودن یک اسم مستعار جدید فقط یک خط باشد
 * (نگاه کنید به {@link #ALIASES}) و منطق برنامه دیگر دست‌نخورده بماند.
 *
 * نکته: این Map صرفاً برای *کمک به جست‌وجوی متنی LIKE %keyword%* است، نه یک
 * enum بسته‌ی معتبر؛ اگر کلیدواژه‌ای در این لیست نباشد، fallback ساده
 * (حذف کلمات سوالی رایج فارسی) همچنان اعمال می‌شود تا دوره‌های دیگر هم قابل
 * جست‌وجو بمانند.
 */
public final class CourseQueryNormalizer {

    private CourseQueryNormalizer() {
    }

    /**
     * نگاشت کلیدواژه (همیشه lowercase، بدون فاصله‌ی اضافه) -> نام استاندارد دوره.
     * هر زبان/دوره‌ی جدید فقط یک ورودی جدید نیاز دارد؛ تغییر دیگری در کد لازم نیست.
     */
    private static final Map<String, String> ALIASES = new LinkedHashMap<>();

    static {
        ALIASES.put("پایتون", "Python");
        ALIASES.put("python", "Python");

        // نکته: «جاوا» باید بعد از حذف عبارت «جاوااسکریپت» چک شود وگرنه با آن قاطی می‌شود؛
        // به همین دلیل ترتیب درج در ALIASES مهم است (LinkedHashMap ترتیب را حفظ می‌کند)
        // و «جاوااسکریپت»/«javascript» قبل از «جاوا»/«java» می‌آیند.
        ALIASES.put("جاوااسکریپت", "JavaScript");
        ALIASES.put("javascript", "JavaScript");
        ALIASES.put("جاوا اسکریپت", "JavaScript");

        ALIASES.put("جاوا", "Java");
        ALIASES.put("java", "Java");

        ALIASES.put("ری‌اکت", "React");
        ALIASES.put("ریکت", "React");
        ALIASES.put("react", "React");

        ALIASES.put("گو", "Go");
        ALIASES.put("golang", "Go");

        ALIASES.put("سی‌شارپ", "C#");
        ALIASES.put("سی شارپ", "C#");
        ALIASES.put("c#", "C#");
    }

    /**
     * کلمات سوالی/اضافی رایج فارسی که هیچ ارزش جست‌وجویی ندارند و باید از
     * کوئری fallback حذف شوند (وقتی هیچ alias مشخصی پیدا نشد).
     */
    private static final Pattern NOISE_WORDS = Pattern.compile(
            "کلاس|دوره|قیمت|هزینه|شهریه|چنده|چقدر|ثبت[\\s‌]?نام"
    );

    public static String normalize(String rawQuery) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return "";
        }

        String q = rawQuery.toLowerCase().replace("؟", "").trim();

        for (Map.Entry<String, String> alias : ALIASES.entrySet()) {
            if (q.contains(alias.getKey())) {
                return alias.getValue();
            }
        }

        return NOISE_WORDS.matcher(q).replaceAll("").trim().replaceAll("\\s+", " ");
    }
}