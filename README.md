# Chatbot Platform

پلتفرم چت‌بات هوشمند با پشتیبانی از زبان فارسی، معماری RAG، کش معنایی، و یکپارچه‌سازی با سرویس‌های پایتون.

---

## فهرست مطالب

- [معماری کلی](#معماری-کلی)
- [پیش‌نیازها](#پیش‌نیازها)
- [راه‌اندازی سریع](#راه‌اندازی-سریع)
- [متغیرهای محیطی](#متغیرهای-محیطی)
- [ماژول‌ها](#ماژول‌ها)
- [جریان پردازش چت](#جریان-پردازش-چت)
- [پایگاه داده‌ها](#پایگاه-داده‌ها)
- [سرویس‌های پایتون](#سرویس‌های-پایتون)
- [API Reference](#api-reference)
- [پروفایل‌ها و محیط توسعه](#پروفایل‌ها-و-محیط-توسعه)
- [لاگ‌گیری و ردیابی](#لاگ‌گیری-و-ردیابی)
- [تست‌ها](#تست‌ها)

---

## معماری کلی

```
                        ┌─────────────────────────────────────────────┐
                        │              Spring Boot (Java)              │
                        │                   :8081                      │
                        │                                              │
  Client ──────────────▶│  OrchestratorService                        │
                        │       │                                      │
                        │       ├─▶ SemanticCacheService (Redis)       │
                        │       ├─▶ IntentClient ──────────────────────┼──▶ Python intent-service
                        │       ├─▶ FAQService                         │
                        │       ├─▶ CourseService (PostgreSQL)         │
                        │       ├─▶ QaSearchService                    │
                        │       │       ├─▶ pgvector (ANN Search)      │
                        │       │       └─▶ RerankClient ──────────────┼──▶ Python rerank-service
                        │       ├─▶ LLMClient ────────────────────────┼──▶ Python llm-service
                        │       └─▶ ChatSessionService (MongoDB)       │
                        │                                              │
                        │  OcrController                               │
                        │       └─▶ OCRClient ──────────────────────── ┼──▶ Python ocr-service
                        └─────────────────────────────────────────────┘
                                   │            │            │
                              PostgreSQL     MongoDB        Redis
                          (courses, qa,    (sessions,    (semantic
                           pgvector)         ocr docs)     cache)
```

پروژه از یک معماری ترکیبی Java + Python استفاده می‌کند:

- **Java (Spring Boot):** هسته اصلی، مدیریت جریان، پایگاه داده، کش و API
- **Python (microservices):** تشخیص نیت، rerank، OCR، و مدل زبانی
- **PostgreSQL + pgvector:** داده‌های ساختاریافته و جستجوی برداری
- **MongoDB:** تاریخچه مکالمات و اسناد OCR
- **Redis:** کش معنایی مبتنی بر embedding

---

## پیش‌نیازها

| ابزار | نسخه پیشنهادی |
|---|---|
| Java | 21+ |
| Maven | 3.9+ |
| Docker + Docker Compose | 24+ |
| Python services | به صورت جداگانه (ر.ک. [سرویس‌های پایتون](#سرویس‌های-پایتون)) |

---

## راه‌اندازی سریع

### ۱. کلون پروژه

```bash
git clone <repo-url>
cd chatbot-platform
```

### ۲. تنظیم متغیرهای محیطی

```bash
cp template.env .env
# مقادیر مورد نیاز را در .env ویرایش کنید
```

### ۳. راه‌اندازی زیرساخت (Docker)

```bash
docker compose up -d postgres mongodb redis
```

این دستور PostgreSQL (با pgvector)، MongoDB و Redis را راه‌اندازی می‌کند.

### ۴. اجرای اپلیکیشن

```bash
# محیط local (بدون نیاز به سرویس‌های پایتون — از Fake clientها استفاده می‌شود)
./mvnw spring-boot:run

# محیط prod (با سرویس‌های واقعی پایتون)
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### ۵. بررسی سلامت

```
http://localhost:8081/swagger-ui.html
```

---

## متغیرهای محیطی

| متغیر | پیش‌فرض | توضیح |
|---|---|---|
| `DB_HOST` | `localhost` | هاست PostgreSQL |
| `DB_PORT` | `5440` | پورت PostgreSQL |
| `DB_NAME` | `platform_db` | نام دیتابیس |
| `DB_USERNAME` | `platform_user` | کاربر دیتابیس |
| `DB_PASSWORD` | `platform123` | رمز دیتابیس |
| `MONGO_HOST` | `localhost` | هاست MongoDB |
| `MONGO_PORT` | `27017` | پورت MongoDB |
| `MONGO_DB` | `platform_mongo` | نام دیتابیس Mongo |
| `REDIS_HOST` | `localhost` | هاست Redis |
| `REDIS_PORT` | `6379` | پورت Redis |
| `SERVER_PORT` | `8081` | پورت اپلیکیشن |
| `OPENAI_API_KEY` | — | کلید API برای embedding (در صورت استفاده از OpenAI) |
| `EMBEDDING_DIMENSIONS` | `384` | ابعاد embedding (باید با ستون pgvector هماهنگ باشد) |
| `INTENT_SERVICE_URL` | `http://intent-service` | آدرس سرویس تشخیص نیت پایتون |
| `LLM_SERVICE_URL` | `http://llm-service` | آدرس سرویس مدل زبانی پایتون |
| `RERANK_SERVICE_URL` | `http://rerank-service` | آدرس سرویس rerank پایتون |
| `OCR_SERVICE_URL` | `http://ocr-service` | آدرس سرویس OCR پایتون |
| `QA_SEARCH_TOP_K` | `10` | تعداد کاندیداهای pgvector قبل از rerank |
| `QA_SEARCH_MIN_VECTOR_SIMILARITY` | `0.55` | آستانه فیلتر اولیه برداری |
| `QA_SEARCH_MIN_RERANK_SCORE` | `0.78` | آستانه پذیرش بعد از rerank |

> **نکته امنیتی:** فایل `template.env` را برای مقادیر واقعی کپی کنید و هرگز `.env` را commit نکنید.

---

## ماژول‌ها

### `orchestration`

هسته اصلی پردازش چت. `OrchestratorService` جریان کامل یک درخواست را مدیریت می‌کند:

1. ایجاد/بارگذاری session
2. Resolve کردن سوالات follow-up با تاریخچه (مثلاً «چند؟» → «قیمت دوره پایتون چند؟»)
3. بررسی کش معنایی
4. تشخیص نیت
5. مسیریابی به handler
6. ذخیره در تاریخچه و کش

### `cache`

کش معنایی مبتنی بر Redis. هر جواب با embedding سوالش ذخیره می‌شود. در lookup، cosine similarity بین سوال جدید و همه سوالات کش حساب می‌شود؛ اگر بهترین امتیاز بالای **0.92** باشد، جواب مستقیم برگردانده می‌شود و تمام pipeline دور زده می‌شود.

### `qa`

زنجیره RAG روی `qa_pairs`:

```
embed(question) → pgvector ANN (top-k=10, min=0.55) → Python Rerank (min=0.78) → answer
```

در صورت شکست در هر مرحله، سیستم به جای خطا به سمت LLM fallback می‌کند.

### `chatlog`

مدیریت session و تاریخچه مکالمات در MongoDB. تاریخچه آخرین ۶ پیام برای ساخت prompt به LLM ارسال می‌شود.

### `course`

مدیریت دوره‌ها و جزئیات (قیمت، مدرس، مدت، پیش‌نیاز). handler‌های `pricing` و `class_search` از این ماژول استفاده می‌کنند.

### `embedding`

واسط بین Java و سرویس embedding. در `local` از `FakeEmbeddingClient` (بردار شبه‌تصادفی ثابت) و در `prod` از OpenAI `text-embedding-3-small` استفاده می‌شود.

### `ocr`

دریافت تصویر، OCR با Tesseract (زبان فارسی `fas`)، ذخیره نتیجه در MongoDB. در `prod` به سرویس پایتون delegate می‌شود.

### `rerank`

واسط با سرویس rerank پایتون. در صورت عدم دسترسی به سرویس، به ترتیب vector score fallback می‌کند (کیفیت کمتر، اما سیستم سرپا می‌ماند).

---

## جریان پردازش چت

```
POST /api/chat
        │
        ▼
  [session resolution]
  sessionId موجود → بارگذاری تاریخچه
  sessionId خالی  → ساخت session جدید
        │
        ▼
  [follow-up resolution]
  سوال کوتاه + تاریخچه → سوال کامل‌شده
        │
        ▼
  [semantic cache lookup]  ──── HIT ────▶ برگشت جواب فوری
        │ MISS
        ▼
  [intent detection] (Python)
        │
        ├── faq         ──▶ FAQService (جواب ثابت) ──▶ اگر نبود: LLM pipeline
        ├── pricing     ──▶ CourseService (قیمت دوره)
        ├── class_search──▶ CourseService (لیست دوره)
        └── llm         ──▶ QaSearchService (RAG) ──▶ اگر نبود: LLMClient
                │
                ▼
        [save to history + cache]
                │
                ▼
        ChatResponse { answer, traceId, elapsedMs, sessionId }
```

---

## پایگاه داده‌ها

### PostgreSQL (migrations با Flyway)

| Migration | محتوا |
|---|---|
| `V1__init_courses` | جداول `courses` و `course_details` |
| `V2__qa_intent_embedding` | `qa_pairs`، `qa_embeddings` با pgvector(384)، ایندکس ivfflat، تابع `search_qa()` |
| `V3__career_module` | جداول `careers`، `career_skills`، `career_requirements` |
| `V4__chatlog_feedback` | جداول chatlog و feedback |

#### تابع `search_qa()`

```sql
SELECT * FROM search_qa(
    query_embedding := '[0.1, 0.2, ...]'::vector(384),
    top_k           := 10,
    min_similarity  := 0.55
);
```

### MongoDB (migrations با Mongock)

| Collection | محتوا |
|---|---|
| `chat_sessions` | session‌ها و تاریخچه پیام‌ها |
| `ocr_images` | نتایج OCR به همراه متادیتا |

### Redis

کلیدهای کش معنایی با prefix `semantic_cache:` و یک Set سراسری `semantic_cache:keys` برای نگه‌داشتن فهرست کلیدها.

---

## سرویس‌های پایتون

این سرویس‌ها **در docker-compose اصلی نیستند** و باید جداگانه راه‌اندازی شوند. Java در پروفایل `local` از پیاده‌سازی‌های Fake استفاده می‌کند، بنابراین برای توسعه لازم نیستند.

### Intent Service

```
POST {INTENT_SERVICE_URL}/detect
Body:  { "text": "قیمت دوره پایتون چنده؟" }
Response: { "intent": "pricing", "confidence": 0.95 }
```

مقادیر معتبر intent: `faq` | `pricing` | `class_search` | `llm`

> **نکته:** مقادیر باید lowercase برگردانده شوند.

### Rerank Service

```
POST {RERANK_SERVICE_URL}/rerank
Body: {
  "query": "دوره جاوا",
  "candidates": [
    { "id": "12", "text": "هزینه دوره جاوا", "vectorScore": 0.81 }
  ]
}
Response: {
  "results": [
    { "id": "12", "finalScore": 0.93 }
  ]
}
```

### LLM Service

```
POST {LLM_SERVICE_URL}/chat
Body:  { "prompt": "تاریخچه گفتگو:\n...\nسوال کاربر: ..." }
Response: { "answer": "..." }
```

### OCR Service

```
POST {OCR_SERVICE_URL}/ocr
Content-Type: multipart/form-data
Body: file=<image>
Response: { "text": "..." }
```

---

## API Reference

مستندات کامل Swagger پس از راه‌اندازی در آدرس زیر در دسترس است:

```
http://localhost:8081/swagger-ui.html
```

### نمونه درخواست چت

```bash
curl -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "question": "قیمت دوره پایتون چنده؟",
    "sessionId": "",
    "userId": "user-123"
  }'
```

```json
{
  "success": true,
  "answer": "💰 قیمت دوره Python: ۱۲۰۰۰۰۰ تومان",
  "traceId": "a3f1c2d4",
  "elapsedMs": 243,
  "sessionId": "sess-uuid-..."
}
```

### آپلود تصویر برای OCR

```bash
curl -X POST http://localhost:8081/api/ocr/upload \
  -F "file=@/path/to/image.png"
```

---

## پروفایل‌ها و محیط توسعه

| پروفایل | `IntentClient` | `EmbeddingClient` | `RerankClient` | `LLMClient` | `OCRClient` |
|---|---|---|---|---|---|
| `local` (پیش‌فرض) | Fake | Fake | Fake | Fake | Fake |
| `prod` | Real (Python) | OpenAI | Real (Python) | Real (Python) | Real (Python) |

در محیط `local` تمام سرویس‌های خارجی با پیاده‌سازی‌های Fake جایگزین می‌شوند که رفتار قابل پیش‌بینی و ثابت دارند — بدون نیاز به هیچ سرویس Python یا OpenAI.

---

## لاگ‌گیری و ردیابی

هر درخواست چت یک `traceId` هشت‌کاراکتری یکتا دریافت می‌کند که در MDC ثبت می‌شود و در تمام لاگ‌های زیرسیستم‌ها قابل مشاهده است:

```
2026-06-21 10:30:12.543 INFO  [http-nio-1] [traceId=a3f1c2d4] OrchestratorService - Incoming query: 'قیمت دوره پایتون' (Session: sess-abc)
2026-06-21 10:30:12.671 INFO  [http-nio-1] [traceId=a3f1c2d4] SemanticCacheService - Semantic cache MISS. bestScore=0.71
2026-06-21 10:30:12.720 INFO  [http-nio-1] [traceId=a3f1c2d4] OrchestratorService - Detected intent: 'pricing'
2026-06-21 10:30:12.891 INFO  [http-nio-1] [traceId=a3f1c2d4] OrchestratorService - chat-orchestration summary: session_resolution=12ms, cache_lookup=128ms, intent_detection=48ms, handle_pricing=83ms, total=271ms
```

برای فیلتر لاگ یک درخواست:

```bash
grep "traceId=a3f1c2d4" app.log
```

---

## تست‌ها

```bash
# اجرای تمام تست‌ها
./mvnw test

# تست یک کلاس خاص
./mvnw test -Dtest=FakeEmbeddingClientTest
```

### تست‌های موجود

| کلاس | توضیح |
|---|---|
| `FakeEmbeddingClientTest` | اعتبارسنجی رفتار Fake embedding (ابعاد، determinism) |
| `FakeRerankClientTest` | بررسی ترتیب‌بندی Fake rerank |
| `RealIntentClientTest` | تست `resolveIntent()` برای نرمال‌سازی lowercase/uppercase |
| `PlatformApplicationTests` | تست بارگذاری context |

---

## ساختار پروژه

```
src/main/java/com/example/platform/
├── common/
│   ├── constant/       # ChatIntents
│   ├── exception/      # GlobalExceptionHandler
│   ├── response/       # ApiResponse
│   └── util/
│       ├── PgVectorUtils.java
│       ├── StepTimer.java
│       └── text/PersianTextUtils.java
├── config/             # RepositoryConfig, DB migrations
├── infrastructure/     # CacheConfig, DevCorsConfig, WebClientConfig
├── modules/
│   ├── cache/          # SemanticCacheService
│   ├── chatlog/        # ChatController, ChatSessionService, LLMClient
│   ├── course/         # CourseService, CourseDetailService
│   ├── embedding/      # EmbeddingService, OpenAiEmbeddingClient
│   ├── ocr/            # OcrController, OcrService
│   ├── qa/             # QaSearchService, FAQService, IntentClient
│   ├── rerank/         # RerankClient
│   └── search/         # VectorSimilarityService, RerankService
├── mongo/              # OcrImageDocument
└── orchestration/
    ├── service/OrchestratorService.java   ← نقطه ورود اصلی
    └── util/CourseQueryNormalizer.java

src/main/resources/
├── application.yml
├── db/migration/       # Flyway SQL migrations
├── static/index.html   # فرانت‌اند
└── tessdata/fas.traineddata
```

---

## نکات توسعه

**تغییر ابعاد embedding:** مقدار `EMBEDDING_DIMENSIONS` در `application.yml` باید با ستون `vector(N)` در `qa_embeddings` هماهنگ باشد. برای تغییر، یک migration جدید Flyway بنویسید و FakeEmbeddingClient را نیز به‌روزرسانی کنید.

**افزودن intent جدید:** مقدار ثابت را به `ChatIntents.java` اضافه کنید، آن را به `ALLOWED_INTENTS` در `RealIntentClient` اضافه کنید، و یک handler در `OrchestratorService` تعریف کنید.

**تنظیم آستانه‌های جستجو:** مقادیر `QA_SEARCH_MIN_VECTOR_SIMILARITY` و `QA_SEARCH_MIN_RERANK_SCORE` را از طریق متغیر محیطی تنظیم کنید — بدون نیاز به build مجدد.
