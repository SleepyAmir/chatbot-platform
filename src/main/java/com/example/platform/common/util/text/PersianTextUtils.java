package com.example.platform.common.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * ابزار مشترک نرمال‌سازی و توکنایز متن فارسی/انگلیسی، که پایه‌ی الگوریتم‌های
 * Fake (محلی، بدون شبکه) برای embedding / intent / rerank است.
 *
 * <p>چرا این کلاس لازم است؟ قبل از این، هر کدام از Fake clientها (یا اصلاً متنی را
 * تحلیل نمی‌کردند مثل {@code FakeEmbeddingClient} که فقط hashCode می‌گرفت، یا با یک
 * regex خیلی ساده توکنایز می‌کردند مثل {@code RerankService}) که نه نیم‌فاصله (ZWNJ)
 * را می‌فهمیدند، نه مترادف‌های رایج («هزینه»/«قیمت»/«شهریه») را یکی می‌دانستند، نه بین
 * «جاوا» و «جاوااسکریپت» تمایز درستی می‌گذاشتند. نتیجه این بود که در حالت local/dev
 * (یعنی همان حالتی که بیشترین زمان توسعه و دمو در آن سپری می‌شود) کش معنایی و
 * جست‌وجوی برداری qa_pairs عملاً تصادفی رفتار می‌کردند.
 *
 * <p>این کلاس عمداً بدون هیچ وابستگی خارجی (بدون کتابخانه‌ی NLP) نوشته شده چون باید
 * کاملاً محلی، سریع و قطعی (deterministic) باشد — دقیقاً همان قید یک Fake client خوب.
 *
 * <p>نکته‌ی مهم: دیکشنری مترادف‌های اینجا ({@link #SYNONYMS}) هدف متفاوتی از
 * {@code CourseQueryNormalizer.ALIASES} دارد. آن یکی کل سوال را به یک کلیدواژه‌ی
 * انگلیسی واحد برای جست‌وجوی LIKE در دیتابیس تبدیل می‌کند (خروجی: یک کلمه).
 * این‌جا هدف یکی‌سازی توکن‌به‌توکن برای محاسبه‌ی شباهت متنی/برداری است (خروجی: چند
 * توکن کانونیکال‌شده). همپوشانی کوچکی بین این دو وجود دارد که قابل قبول است؛ اگر در
 * آینده دیکشنری‌های مترادف بیشتر شدند، می‌توان آن‌ها را در یک منبع مشترک یکی کرد.
 */
public final class PersianTextUtils {

    private PersianTextUtils() {
    }

    /**
     * کلمات ربط/کمکی فارسی که هیچ بار معنایی موضوعی ندارند و باید از محاسبه‌ی
     * شباهت کلیدواژه‌ای/برداری حذف شوند. عمداً کلماتی مثل «چقدر»/«قیمت» این‌جا
     * نیستند چون برای تشخیص intent علامت معنادار محسوب می‌شوند.
     */
    private static final Set<String> STOPWORDS = Set.of(
            "از", "به", "با", "در", "را", "که", "این", "آن", "یک", "و", "یا", "تا",
            "هم", "نیز", "است", "هست", "بود", "شد", "شده", "می", "نمی", "برای",
            "روی", "زیر", "بر", "آیا", "لطفا", "لطفاً", "ممنون", "سلام", "بفرمایید",
            "کنم", "کنید", "شما", "من", "ما", "چه", "های"
    );

    /**
     * عبارات چند کلمه‌ای که باید قبل از توکنایز به یک توکن واحد «چسبانده» شوند،
     * تا «ثبت نام» / «ثبت‌نام» (با نیم‌فاصله) / «ثبتنام» (یک‌جا نوشته‌شده) همگی به
     * یک توکن یکسان برسند، فارغ از این‌که کاربر چطور تایپ کرده.
     * ترتیب درج مهم است: عبارات بلندتر/خاص‌تر باید زودتر اعمال شوند.
     */
    private static final String[][] PHRASE_JOINS = {
            {"جاوا اسکریپت", "جاوااسکریپت"},
            {"سی شارپ", "سیشارپ"},
            {"ثبت نام", "ثبتنام"},
            {"ساعت کاری", "ساعتکاری"},
            {"ساعات کاری", "ساعتکاری"},
            {"شماره تماس", "تماس"},
            {"شماره تلفن", "تماس"},
    };

    /**
     * نگاشت توکن خام (یا واریانت انگلیسی/محاوره‌ای) -> توکن کانونیکال.
     * هدف: جمع کردن مترادف‌های رایج زیر یک توکن واحد تا شباهت متنی واقعی محاسبه شود.
     */
    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
        // نام دوره‌ها: فارسی/انگلیسی -> یک توکن کانونیکال فارسی
        SYNONYMS.put("java", "جاوا");
        SYNONYMS.put("python", "پایتون");
        SYNONYMS.put("javascript", "جاوااسکریپت");
        SYNONYMS.put("react", "ریکت");
        SYNONYMS.put("ریاکت", "ریکت");
        SYNONYMS.put("golang", "گو");
        SYNONYMS.put("گلنگ", "گو");
        SYNONYMS.put("c#", "سیشارپ");

        // مترادف‌های قیمت -> یک توکن کانونیکال
        SYNONYMS.put("هزینه", "قیمت");
        SYNONYMS.put("شهریه", "قیمت");
        SYNONYMS.put("تعرفه", "قیمت");

        // واریانت‌های محاوره‌ای «چقدر»
        SYNONYMS.put("چنده", "چقدر");
        SYNONYMS.put("چقدره", "چقدر");
    }

    /**
     * نرمال‌سازی سطح کاراکتر: یکسان‌سازی حروف عربی/فارسی، حذف اعراب، تبدیل ارقام
     * فارسی/عربی به انگلیسی، تبدیل نیم‌فاصله و خط‌تیره به فاصله، حذف علائم نگارشی،
     * و کوچک‌کردن حروف لاتین.
     */
    public static String normalize(String input) {
        if (input == null) {
            return "";
        }

        String s = input;

        // یکسان‌سازی حروف عربی به معادل فارسی
        s = s.replace('\u064A', '\u06CC'); // ي -> ی
        s = s.replace('\u0643', '\u06A9'); // ك -> ک
        s = s.replace('\u0629', '\u0647'); // ة -> ه

        // حذف اعراب عربی (فتحه/کسره/ضمه/تشدید و ...)
        s = s.replaceAll("[\u064B-\u0652]", "");

        // تبدیل ارقام فارسی/عربی به ارقام انگلیسی (برای مقایسه‌ی شماره/قیمت)
        s = convertDigits(s);

        // نیم‌فاصله و خط‌تیره را معادل فاصله در نظر می‌گیریم
        s = s.replace('\u200C', ' ').replace('-', ' ');

        // کوچک‌سازی حروف لاتین (java, python, ...)
        s = s.toLowerCase(Locale.ROOT);

        // حذف علائم نگارشی؛ '#' حفظ می‌شود چون بخشی از نام دوره (C#) است
        s = s.replaceAll("[^\\p{L}\\p{N}#\\s]", " ");

        s = s.replaceAll("\\s+", " ").trim();
        return s;
    }

    private static String convertDigits(String s) {
        String fa = "۰۱۲۳۴۵۶۷۸۹";
        String ar = "٠١٢٣٤٥٦٧٨٩";
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int fi = fa.indexOf(c);
            int ai = fi >= 0 ? -1 : ar.indexOf(c);
            if (fi >= 0) {
                sb.append((char) ('0' + fi));
            } else if (ai >= 0) {
                sb.append((char) ('0' + ai));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * بسیار سبک: فقط نشانه‌های جمع رایج فارسی («ها»/«های») را حذف می‌کند.
     * عمداً تهاجمی نیست (مثلاً پسوندهای ملکی/فعلی را قطع نمی‌کند) تا false-positive
     * کم بماند.
     */
    private static String lightStem(String token) {
        if (token.length() > 4 && token.endsWith("های")) {
            return token.substring(0, token.length() - 3);
        }
        if (token.length() > 3 && token.endsWith("ها")) {
            return token.substring(0, token.length() - 2);
        }
        return token;
    }

    /**
     * متن خام را به فهرستی از توکن‌های کانونیکال‌شده (بدون stopword) تبدیل می‌کند.
     * این متد پایه‌ی هر سه الگوریتم Fake (embedding/intent/rerank) است.
     */
    public static List<String> tokenize(String input) {
        String normalized = normalize(input);

        for (String[] join : PHRASE_JOINS) {
            normalized = normalized.replace(join[0], join[1]);
        }

        List<String> tokens = new ArrayList<>();
        if (normalized.isBlank()) {
            return tokens;
        }

        for (String raw : normalized.split(" ")) {
            if (raw.isBlank()) {
                continue;
            }
            String tok = SYNONYMS.getOrDefault(raw, raw);
            tok = lightStem(tok);
            tok = SYNONYMS.getOrDefault(tok, tok); // بعد از stem هم یک‌بار دیگر چک می‌شود
            if (tok.length() < 2 && !tok.equals("c#")) {
                continue;
            }
            if (STOPWORDS.contains(tok)) {
                continue;
            }
            tokens.add(tok);
        }
        return tokens;
    }

    /**
     * n-gram کاراکتری یک توکن (با علامت‌های مرزی ^/$) — برای مقاوم‌بودن embedding
     * در برابر تغییرات صرفی جزئی که {@link #lightStem(String)} نمی‌گیرد.
     */
    public static List<String> charNgrams(String token, int n) {
        String padded = "^" + token + "$";
        List<String> grams = new ArrayList<>();
        if (padded.length() < n) {
            grams.add(padded);
            return grams;
        }
        for (int i = 0; i + n <= padded.length(); i++) {
            grams.add(padded.substring(i, i + n));
        }
        return grams;
    }

    /**
     * هش پایدار با پخش بیت بهتر از {@code String.hashCode()} خام (الهام‌گرفته از
     * فینالایزر MurmurHash3). نتیجه در همه‌ی اجراهای JVM یکسان است چون فقط روی
     * {@code String.hashCode()} (که طبق JLS قطعی است) کار می‌کند، نه روی هش‌های
     * Object پیش‌فرض.
     */
    public static int stableHash(String feature) {
        int h = feature.hashCode();
        h ^= (h >>> 16);
        h *= 0x85ebca6b;
        h ^= (h >>> 13);
        h *= 0xc2b2ae35;
        h ^= (h >>> 16);
        return h;
    }
}