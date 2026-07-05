# chatbot-platform

بک‌اند ماژولار چت‌بات دانشگاهی — Spring Boot + Postgres(pgvector) + MongoDB + Redis.
این README وضعیت فعلی پروژه (فاز ۱) رو مستند می‌کنه: هر ماژول چیکار می‌کنه، هر پوشه چیه،
چی به هم وصله، چی هنوز وصل نیست، و برای اجرا چی لازمه.

---

## ۱. معماری کلی، یه نگاه از بالا

```
فرانت (JSON) ──> ChatController ──> OrchestratorService ──┬──> کش معنایی (Redis)
                                                            ├──> IntentClient (تشخیص نیت)
                                                            ├──> QaSearchService (qa_pairs + pgvector + rerank)
                                                            ├──> Course/CourseDetail (قیمت/کلاس)
                                                            ├──> LLMClient (fallback نهایی)
                                                            ├──> ChatSessionService (Mongo - تاریخچه‌ی مکالمه)
                                                            └──> ChatLogService (Postgres - لاگ تحلیلی + فیدبک)
```

هر مرحله (`IntentClient`, `LLMClient`, `RerankClient`, و به‌زودی `EmbeddingClient`) از یه
اینترفیس با دو پیاده‌سازی ساخته شده:
- **Fake\*** → برای dev/تست محلی، بدون نیاز به هیچ سرویس بیرونی
- **Real\*** → فقط JSON می‌فرسته/می‌گیره به یه سرویس پایتون جدا (هیچ منطق مدل داخل جاوا نیست)

کدوم پیاده‌سازی فعاله رو **پروفایل اسپرینگ** تعیین می‌کنه (بخش ۵ رو ببینید).

---

## ۲. استک فنی

| بخش | تکنولوژی |
|---|---|
| زبان/فریم‌ورک | Java 21 / Spring Boot |
| دیتابیس اصلی | PostgreSQL 17 + pgvector (Flyway migration) |
| دیتابیس مکالمه | MongoDB (تاریخچه‌ی چت، session-محور) |
| کش | Redis |
| HTTP client به سرویس‌های پایتون | Spring WebClient (از `spring-boot-starter-webflux`) |
| مستندسازی API | springdoc-openapi (`/swagger-ui.html`) |

---

## ۳. ساختار پوشه‌ها (`src/main/java/com/example/platform`)

### `common/`
چیزهای مشترک بین همه‌ی ماژول‌ها:
- `response/ApiResponse.java` — پوشش استاندارد پاسخ همه‌ی endpointها
- `exception/` — `GlobalExceptionHandler`, `ResourceNotFoundException`, `EmbeddingException`, ...
- `web/PageableUtils.java` — سفیدلیست کردن فیلدهای قابل sort (جلوگیری از تزریق sort روی ستون دلخواه)
- `util/StepTimer.java` — اندازه‌گیری و لاگ زمان هر مرحله از orchestration
- `util/PgVectorUtils.java` — تبدیل `List<Double>` به فرمت لیترال pgvector (`"[0.1,0.2,...]"`)
- `constant/ChatIntents.java` — ثابت‌های نیت مکالمه (`FAQ`, `PRICING`, `CLASS_SEARCH`, `LLM`)

### `infrastructure/`
- `config/cache/RedisConfig.java` — تنظیم `RedisTemplate` (سریالایزر JSON با polymorphic typing)
- `config/WebClientConfig.java` — بین `WebClient.Builder` برای همه‌ی Real\* clientها
- `config/DevCorsConfig.java` — CORS برای فرانت در dev
- `config/openai/OpenAiProperties.java` — تنظیمات مسیر OpenAI (فعلاً غیرفعال، بخش ۵ رو ببینید)

### `modules/course/`
مدیریت دوره‌ها و جزئیات‌شون (قیمت، مدرس، مدت). CRUD کامل + جستجو. کامل و پایداره.

### `modules/career/`
مسیرهای شغلی و نیازمندی‌هاشون (مهارت‌ها، ارتباط با دوره‌ها). کامل و پایداره.

### `modules/qa/`
بانک سوال-جواب آماده (FAQ واقعی سیستم) + جست‌وجوی معنایی روی همون بانک.
- `model/` → `QaPair` (سوال/جواب اصلی), `QaEmbedding` (بردار pgvector), `QaIntent` (دسته‌بندی/تگ هر QA — **این با `qa/client/IntentClient` فرق داره، اون یکی تشخیص نیتِ runtime کاربره، این یکی entity دیتابیسه**)
- `repository/QaPairRepository.java` → شامل متد `searchQa(...)` که تابع SQL `search_qa(...)` (تعریف‌شده در `V2__qa_intent_embedding.sql`) رو صدا می‌زنه — این همون قلب جست‌وجوی برداریه
- `dto/QaSearchRow.java` → نتیجه‌ی خام `searchQa` (قبل از rerank)
- `dto/QaMatch.java` → نتیجه‌ی نهایی بعد از rerank (چیزی که orchestrator مصرف می‌کنه)
- `service/QaSearchService.java` → کل pipeline: **embed → searchQa (pgvector) → rerank → قبول/رد بر اساس آستانه**
- `client/IntentClient.java` (+`Fake`/`Real`) → تشخیص نیت مکالمه (FAQ/قیمت/کلاس/LLM) — یه سرویس runtime، نه بخشی از مدل داده

### `modules/embedding/`
تبدیل متن به بردار عددی، برای هم جست‌وجوی qa_pairs و هم کش معنایی.
- `client/EmbeddingClient.java` (+`Fake`/`OpenAi`) — بخش ۵ رو حتماً بخونید، این ماژول فعلاً **موقتیه**
- `service/EmbeddingService.java` — لایه‌ی نازک روی کلاینت، برای تزریق راحت‌تر جای دیگه

### `modules/rerank/`
بعد از اینکه `searchQa` چند کاندید برگردوند، این ماژول اون‌ها رو دوباره امتیازدهی می‌کنه تا بهترین جواب انتخاب بشه (صرفاً شباهت برداری کافی نیست).
- `client/RerankClient.java` (+`Fake`/`Real`)
- `dto/RerankCandidateInput.java`, `dto/RerankedCandidate.java`

### `modules/search/`
ابزارهای کمکیِ محاسبه‌ی شباهت، مستقل از یه دیتابیس یا API خاص:
- `service/VectorSimilarityService.java` — کسینوس شباهت بین دو بردار
- `service/VectorSearchResult.java`, `RerankService.java` — استفاده می‌شن توسط `FakeRerankClient` و کش معنایی

### `modules/cache/`
کش کردن جواب‌ها در Redis، دو مسیر مکمل:
- `getCachedAnswer(qaId)` / `cacheAnswer(qaId, ...)` — کش بر اساس qaId شناخته‌شده (برای وقتی از قبل می‌دونیم جواب مال کدوم qa_pair بوده)
- `find(question)` / `save(question, answer)` — کش معنایی واقعی: قبل از هر پردازشی (حتی قبل از تشخیص نیت)، با embedding سوال، دنبال یه سوال قبلیِ به‌اندازه‌کافی مشابه (`semantic.cache.similarity-threshold`, پیش‌فرض `0.92`) می‌گرده

### `modules/chatlog/`
⚠️ این ماژول **دو تا زیرسیستم کاملاً جدا** داره که هدف متفاوتی دارن — این رو حتماً به یاد داشته باشید:

| | `ChatLog` / `Feedback` (Postgres) | `ChatSession` / `MessageDoc` (Mongo) |
|---|---|---|
| هدف | **لاگ تحلیلی**: گزارش‌گیری، آمار، فیدبک کاربر | **حافظه‌ی مکالمه**: حل follow-up ("چند؟" یعنی چی؟)، نمایش تاریخچه به کاربر |
| کجا نوشته میشه | بعد از هر پاسخ موفق (`ChatLogService.saveLog`, داخل `OrchestratorService`) | همون‌جا هم، ولی جدا (`ChatSessionService.addMessage`) |
| مدل داده | Entity رابطه‌ای (`chat_logs`, `feedback` با FK) | Document غیررابطه‌ای (`ChatSession` شامل لیست `MessageDoc`) |
| endpoint | `/api/chat-logs`, `/api/feedback` | `/api/chat-sessions`, `/api/chat` |

بقیه‌ی فایل‌های این ماژول:
- `client/LLMClient.java` (+`Fake`/`Real`) — آخرین fallback وقتی نه کش نه qa_pairs جواب داد
- `controller/ChatController.java` — endpoint اصلی چت که فرانت باهاش کار می‌کنه (`POST /api/chat`)
- `dto/ChatRequest.java`, `ChatResponse.java` — قرارداد API با فرانت

### `orchestration/`
مغز اصلی سیستم.
- `service/OrchestratorService.java` — تنها جایی که همه‌چیز بالا رو به هم وصل می‌کنه؛ ترتیب پردازش هر پیام:
  1. resolve/create session (Mongo)
  2. شفاف‌سازی follow-up با تاریخچه
  3. چک کش معنایی (`SemanticCacheService.find`) — اگه هیت شد، مستقیم جواب برمی‌گرده
  4. تشخیص نیت (`IntentClient`)
  5. مسیریابی: FAQ/پیش‌فرض → `QaSearchService` → اگه چیزی پیدا نشد → `LLMClient` | PRICING/CLASS_SEARCH → جستجوی دوره
  6. ذخیره‌ی تاریخچه (Mongo) + کش (Redis) + لاگ تحلیلی (Postgres) — هر سه مستقل و fail-open (خطای هرکدوم باعث خراب شدن جواب کاربر نمیشه)
- `util/CourseQueryNormalizer.java` — نرمال‌سازی اسم دوره‌ها (مثلاً "جاوا اسکریپت"/"جاوااسکریپت" یکی حساب بشن)

---

## ۴. وضعیت هر ماژول (فاز ۱)

| ماژول | وضعیت |
|---|---|
| course, career | ✅ کامل، پایدار |
| qa | ✅ مدل/CRUD کامل + جست‌وجوی معنایی الان وصله (قبلاً فقط اسکلتش بود) |
| chatlog (ChatLog/Feedback) | ✅ بازنویسی و فیکس‌شده |
| chatlog (ChatSession/Mongo) | ✅ از یه برنچ قدیمی آورده و با ماژول‌های بالا هماهنگ شد |
| cache | ✅ کامل + متد `find`/`save` معنایی جدید اضافه شد |
| embedding | ⚠️ **موقتی** — الان Fake (بر پایه‌ی همپوشانی کلمات، نه یه مدل واقعی). طبق تصمیم استاد باید توسط تیم پایتون انجام بشه؛ `OpenAiEmbeddingClient` هست ولی عمداً خاموشه (پروفایل `openai`، هیچ‌وقت پیش‌فرض فعال نمیشه) |
| rerank, intent, llm | ⚠️ Fake پیش‌فرضه؛ `Real*` آماده‌ست، فقط منتظر آدرس واقعی سرویس‌های پایتونه |
| `mongo/document/OcrImageDocument.java` | 🗑 کد مرده، بازمانده‌ی ماژول OCR حذف‌شده (OCR الان یه سرویس جدا/`ocr-service` هست) — پاک‌سازیش pending هست |

---

## ۵. پروفایل‌های اسپرینگ (خیلی مهم، حتماً بخونید)

`spring.profiles.active` توی `application.yaml` تعیین می‌کنه کدوم Fake/Real کلاینت‌ها فعال بشن:

| پروفایل | چی فعال میشه |
|---|---|
| `local` (پیش‌فرض) | همه‌چیز Fake — بدون نیاز به هیچ سرویس بیرونی، بدون نیاز به API key. برای dev/تست همینو نگه دارید. |
| `prod` | `RealIntentClient`, `RealLLMClient`, `RealRerankClient` فعال میشن (به `clients.intent/llm/rerank.base-url` وصل میشن). **embedding همچنان Fake می‌مونه** حتی زیر `prod` — چون هنوز سرویس پایتونش آماده نیست. |
| `openai` | فقط `OpenAiEmbeddingClient` رو فعال می‌کنه (تماس مستقیم جاوا با OpenAI). این یه گزینه‌ی جایگزینِ احتمالیه، **مسیر اصلی نیست** — با `prod` قاطیش نکنید (باعث خطای "no unique bean" میشه). |

فعال‌سازی: `SPRING_PROFILES_ACTIVE=prod` (env var) یا در `docker-compose.yml`.

**قدم بعدی وقتی تیم پایتون سرویس embedding رو آماده کرد:** یه `RealEmbeddingClient` جدید، دقیقاً هم‌شکل `RealIntentClient`/`RealLLMClient`/`RealRerankClient`، که به `clients.embedding.base-url` وصل بشه — هیچ تغییر معماری دیگه‌ای لازم نیست.

---

## ۶. تنظیمات مهم (`application.yaml`)

```yaml
spring.profiles.active        # local | prod | openai (بخش ۵)
qa.search.top-k               # چند کاندید از pgvector گرفته بشه قبل از rerank
qa.search.min-vector-similarity   # آستانه‌ی اولیه‌ی pgvector (فیلتر خشن)
qa.search.min-rerank-score    # آستانه‌ی نهایی بعد از rerank (اگه پایین‌تر بود → میره سراغ LLM)
semantic.cache.similarity-threshold  # آستانه‌ی هیت کش معنایی (پیش‌فرض 0.92)
clients.intent/llm/rerank.base-url   # آدرس سرویس‌های پایتون (فقط زیر prod استفاده میشه)
embedding.dimensions          # باید همیشه با ستون vector(384) در دیتابیس یکی باشه
```

متغیرهای محیطی دیتابیس/Mongo/Redis (`DB_HOST`, `MONGO_HOST`, `REDIS_HOST`, ...) توی
`docker-compose.yml` ست شدن و نیازی به دست‌زدن نیست مگر بخواید جدا از دیتابیس local بزنید.

---

## ۷. اجرا

دو حالت اجرا داریم؛ کدومو انتخاب کنید بستگی به این داره که فقط می‌خواید تست بزنید یا می‌خواید کد رو دیباگ کنید.

### حالت الف — همه‌چیز با داکر (پیشنهاد پیش‌فرض برای تست)

```bash
docker-compose up -d --build
```
همین یه دستور، Postgres + MongoDB + Redis + خودِ اپ رو با تنظیمات درست بالا میاره (پورت‌ها، env varها، همه از قبل توی `docker-compose.yml` تنظیم شدن — نیازی به هیچ env variable دستی نیست).

بعد از بالا اومدن:
- Swagger: `http://localhost:8080/swagger-ui.html`
- تست‌کننده‌ی endpointها: بخش ۷.۳ رو ببینید

**لاگ‌ها کجاست؟**
```bash
docker logs -f platform-app
```
`-f` یعنی زنده دنبالش کن (مثل تیل کردن). چون همه‌مون بک‌اندیم و همه باید کل لاگ رو ببینیم، سطح لاگ روی `DEBUG` تنظیم شده (`logging.level.com.example.platform: DEBUG` توی `application.yaml`) — یعنی همه‌چیز از جزئیات هر مرحله‌ی orchestration تا کوئری‌های JPA رو می‌بینید. هر درخواست به `/api/chat` یه `traceId` داره که اول هر خط لاگ چاپ میشه؛ برای دنبال کردن کامل یه درخواست خاص:
```bash
docker logs platform-app | grep "traceId=d839189f"
```

اگه فقط دیتابیس‌ها رو داکر بخواید بالا بیارید (بدون اپ)، برای این‌که مثلاً یکی از حالت ب استفاده کنه:
```bash
docker-compose up -d postgres mongodb redis
```

### حالت ب — اپ از IntelliJ، دیتابیس‌ها از داکر (برای دیباگ/breakpoint)

اگه می‌خواید کد رو خط‌به‌خط دیباگ کنید یا از hot-reload IDE استفاده کنید:

```bash
docker-compose up -d postgres mongodb redis
```

بعد توی IntelliJ، از dropdown بالای صفحه (کنار دکمه‌ی Run)، به‌جای `PlatformApplication` معمولی، این رو انتخاب کنید:
```
PlatformApplication (local, DB via docker)
```
این Run Configuration از قبل توی `.run/` پروژه commit شده (با pull گرفتن خودش میاد، نیازی به تنظیم دستی env variable نیست). env varهای موجود توش (`DB_PORT=5440`, `SERVER_PORT=8080`, ...) دقیقاً برای این سناریو تنظیم شدن، چون وقتی اپ **بیرون** شبکه‌ی داکره، باید از پورت‌های **نگاشت‌شده‌ی بیرونی** (نه پورت داخلی کانتینرها) استفاده کنه.

**لاگ‌ها کجاست؟** مستقیم توی تب **Run** پایین IntelliJ — چون اپ خودش همون‌جا اجرا میشه، نه داخل داکر.

⚠️ اگه این Run Configuration رو نمی‌بینید، مطمئن شید آخرین pull رو زدید و پوشه‌ی `.run/` توی ریشه‌ی پروژه هست.

### ۷.۳ — تست‌کننده‌ی سریع همه‌ی endpointها

فایل `tools/api-tester.html` رو (بعد از بالا اومدن پروژه با هرکدوم از دو حالت بالا) با دابل‌کلیک مستقیم توی مرورگر باز کنید — هیچ سرور/نصبی لازم نیست. یه صفحه‌ی ساده‌ست که همه‌ی endpointهای پیاده‌سازی‌شده رو با مقادیر پیش‌فرض آماده لیست کرده؛ فقط دکمه‌ی «ارسال» رو بزنید و جواب خام JSON رو همون‌جا ببینید. URL/بدنه‌ی هر درخواست هم قابل ویرایشه قبل از ارسال.

اگه بک‌اند روی پورت دیگه‌ای غیر از ۸۰۸۰ بالا اومده، بالای همون صفحه یه فیلد «Base URL» هست، عوضش کنید.

---

## ۸. نقطه‌ی اتصال با تیم‌های دیگه


- **فرانت:** `POST /api/chat` با بدنه‌ی `ChatRequest` (question, sessionId اختیاری, userId اختیاری) →
  جواب `ChatResponse` (answer, sessionId, success, ...). سشن رو از پاسخ اول بگیرید و توی
  درخواست‌های بعدی همون مکالمه بفرستید تا تاریخچه/follow-up کار کنه.
- **تیم پایتون:** سه اندپوینت لازمه تا `Real*` کلاینت‌ها رو بدون تغییر کد جاوا فعال کنیم:
  intent detection, LLM completion, rerank — و بعداً یه اندپوینت embedding (بخش ۵).
  فرمت دقیق request/body توی هر `Real*Client.java` مستنده.

---

## ۹. نکات باز / برای بحث تیمی

- کد مرده‌ی OCR (`mongo/document/OcrImageDocument.java`, وابستگی tess4j در `pom.xml`,
  کانفیگ‌های OCR در `Dockerfile`/`docker-compose.yml`) هنوز پاک‌سازی نشده روی این برنچ.
- دو تا برنچ گیت‌هاب قدیمی (`Qa-module` با m کوچیک) منسوخ و ناسازگارن — قبل از merge حذف بشن.
- آستانه‌های `qa.search.min-rerank-score` و `semantic.cache.similarity-threshold` فعلاً
  مقادیر پیش‌فرضِ گرفته‌شده از یه پیاده‌سازی مشابه‌ان، نه چیزی که روی داده‌ی واقعی تیون شده باشه.