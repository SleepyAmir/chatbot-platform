package com.example.platform.orchestration.util;

//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseQueryNormalizerTest {
//
//    @Test
//    void blankOrNullQuery_returnsEmptyString() {
//        assertEquals("", CourseQueryNormalizer.normalize(null));
//        assertEquals("", CourseQueryNormalizer.normalize(""));
//        assertEquals("", CourseQueryNormalizer.normalize("   "));
//    }
//
//    @Test
//    void pythonKeyword_inPersianOrEnglish_resolvesToPython() {
//        assertEquals("Python", CourseQueryNormalizer.normalize("قیمت کلاس پایتون چقدر است؟"));
//        assertEquals("Python", CourseQueryNormalizer.normalize("python course price"));
//    }
//
//    @Test
//    void javaKeyword_doesNotMatchJavaScript() {
//        // نکته‌ی اصلی این تست: «جاوا» باید فقط Java برگرداند، نه JavaScript،
//        // حتی اگر در ALIASES به‌صورت substring درون «جاوااسکریپت» هم باشد.
//        assertEquals("Java", CourseQueryNormalizer.normalize("قیمت کلاس جاوا چقدر است؟"));
//        assertEquals("Java", CourseQueryNormalizer.normalize("هزینه دوره java چنده"));
//    }
//
//    @Test
//    void javaScriptKeyword_writtenAttachedOrWithSpace_resolvesToJavaScript() {
//        // ترتیب درج در ALIASES باید تضمین کند جاوااسکریپت با جاوا قاطی نمی‌شود.
//        assertEquals("JavaScript", CourseQueryNormalizer.normalize("هزینه دوره جاوااسکریپت چنده؟"));
//        assertEquals("JavaScript", CourseQueryNormalizer.normalize("جاوا اسکریپت چقدره"));
//        assertEquals("JavaScript", CourseQueryNormalizer.normalize("javascript course"));
//    }
//
//    @Test
//    void newlyAddedLanguages_reactGoCSharp_areRecognized() {
//        // این سه مورد قبلاً اصلاً پشتیبانی نمی‌شدند (مشکل ۸: کوئری‌های هاردکد).
//        assertEquals("React", CourseQueryNormalizer.normalize("کلاس react چنده؟"));
//        assertEquals("Go", CourseQueryNormalizer.normalize("ثبت نام دوره گو"));
//        assertEquals("C#", CourseQueryNormalizer.normalize("هزینه دوره سی‌شارپ چقدره"));
//    }
//
//    @Test
//    void unknownCourseName_fallsBackToNoiseStrippedKeyword() {
//        // «فلاتر» در ALIASES نیست؛ fallback باید کلمات سوالی رایج («کلاس»، «چنده»)
//        // را حذف کند و فقط نام دوره باقی بماند، نه یک رشته‌ی آلوده به کلمات اضافه.
//        assertEquals("فلاتر", CourseQueryNormalizer.normalize("کلاس فلاتر چنده؟"));
//        assertEquals("ویو جی‌اس", CourseQueryNormalizer.normalize("هزینه دوره ویو جی‌اس چقدره"));
//    }
//
//    @Test
//    void persianQuestionMark_isStrippedBeforeMatching() {
//        assertEquals("Python", CourseQueryNormalizer.normalize("پایتون؟"));
//    }
}