# Chatbot Platform

بک‌اند یک پلتفرم چت‌بات آموزشی (فارسی) با معماری RAG (Retrieval-Augmented
Generation). این سرویس به زبان **Java / Spring Boot** نوشته شده و نقش
**ارکستراتور اصلی** را دارد: درخواست چت کاربر را می‌گیرد، تاریخچه و کش را
مدیریت می‌کند، و برای تشخیص نیت (intent)، rerank، و پاسخ‌گویی نهایی (LLM) به
چند **سرویس پایتون مجزا** (مدل‌های NLP/LLM) از طریق HTTP وصل می‌شود.

> اگر تیم شما سرویس‌های پایتون را پیاده‌سازی می‌کند، بخش
> [قرارداد API سرویس‌های پایتون](#قرارداد-api-سرویسهای-پایتون) مهم‌ترین
> بخش این فایل برای شماست.

---

## فهرست

- [معماری کلی](#معماری-کلی)
- [پشته‌ی فنی](#پشته‌ی-فنی)
- [پیش‌نیازها](#پیشنیازها)
- [راه‌اندازی سریع (Local)](#راهاندازی-سریع-local)
- [پروفایل‌ها (local / dev / test / prod)](#پروفایلها-local--dev--test--prod)
- [متغیرهای محیطی](#متغیرهای-محیطی)
- [قرارداد API سرویس‌های پایتون](#قرارداد-api-سرویسهای-پایتون)
- [اندپوینت‌های REST پروژه](#اندپوینتهای-rest-پروژه)
- [دیتابیس و Migration‌ها](#دیتابیس-و-migrationها)
- [اجرای تست‌ها](#اجرای-تستها)
- [ساختار پروژه](#ساختار-پروژه)
- [وضعیت فعلی / محدودیت‌های شناخته‌شده](#وضعیت-فعلی--محدودیتهای-شناختهشده)

---

## معماری کلی

```
کاربر
  │
  ▼
ChatController  (POST /api/chat)
  │
  ▼
OrchestratorService  ──────────────────────────────────────────┐
  │                                                              │
  ├─ 1) ساخت/بازیابی session و تاریخچه  ───── MongoDB             │
  ├─ 2) شفاف‌سازی سوالات پیگیری (follow-up)                       │
  ├─ 3) چک کش معنایی (Semantic Cache)  ───── Redis                │
  ├─ 4) تشخیص نیت (Intent)  ──────────────── سرویس پایتون (HTTP)  │
  ├─ 5a) FAQ          → جست‌وجوی متنی ساده در qa_pairs              │
  ├─ 5b) Pricing/Class → جست‌وجو در PostgreSQL (courses)            │
  ├─ 5c) سایر موارد   → جست‌وجوی برداری (pgvector) + Rerank         │
  │                       ──────────────────── سرویس پایتون (HTTP) │
  │                     سپس در صورت نبود نتیجه کافی → LLM           │
  │                       ──────────────────── سرویس پایتون (HTTP) │
  └─ 6) ذخیره در تاریخچه (Mongo) + کش (Redis)                      │
                                                                    │
PostgreSQL (+pgvector) ◄── courses, qa_pairs, career_requirements ─┘
MongoDB                ◄── chat_sessions, ocr_images
Redis                  ◄── semantic cache
```

سرویس‌های پایتون که این پروژه با آن‌ها صحبت می‌کند (هرکدام یک maicroservice
جدا، با URL مستقل):

| سرویس | کاری که انجام می‌دهد | فراخوانی‌شده توسط |
|---|---|---|
| **Intent Service** | تشخیص نیت سوال کاربر (`faq` / `pricing` / `class_search` / `llm`) | `RealIntentClient` |
| **Rerank Service** | rerank نتایج جست‌وجوی برداری qa_pairs | `RealRerankClient` |
| **LLM Service** | پاسخ‌گویی آزاد وقتی هیچ مسیر دیگری جواب نداد | `RealLLMClient` |
| **OCR Service** | استخراج متن از تصویر (اختیاری، در حال حاضر در مسیر چت متصل نیست) | `RealOCRClient` |

> **نکته:** embedding (تبدیل متن به بردار برای جست‌وجوی معنایی) فعلاً مستقیماً
> از **OpenAI API** گرفته می‌شود (`OpenAiEmbeddingClient`)، نه از یک سرویس
> پایتون داخلی شما. اگر بخواهید این بخش هم به یک سرویس پایتون داخلی منتقل
> شود، باید `EmbeddingClient` را برای پروفایل `prod` بازنویسی کنید.

---

## پشته‌ی فنی

- **Java 21**, **Spring Boot 4.1.0**
- **PostgreSQL 17 + pgvector** — داده‌ی رابطه‌ای (دوره‌ها، qa_pairs) + جست‌وجوی برداری
- **MongoDB 8** — تاریخچه‌ی چت، تصاویر OCR (با Mongock برای migration)
- **Redis 8** — کش معنایی + Spring Cache
- **Flyway** — migration پایگاه‌داده‌ی رابطه‌ای
- **MapStruct** — نگاشت Entity ↔ DTO
- **Tess4J (Tesseract)** — OCR محلی (پروفایل local)
- **springdoc-openapi** — مستندسازی API (Swagger UI)
- **Docker / docker-compose** — ارکستراسیون سرویس‌های زیرساختی

---

## پیش‌نیازها

- JDK 21
- Maven (یا از `./mvnw` داخل پروژه استفاده کنید)
- Docker و docker-compose (برای PostgreSQL، MongoDB، Redis)

---

## راه‌اندازی سریع (Local)

```bash
# ۱. متغیرهای محیطی را از روی نمونه بسازید
cp template.env .env

# ۲. سرویس‌های زیرساختی را بالا بیاورید (Postgres + Mongo + Redis)
docker compose up -d postgres mongo redis

# ۳. پروژه را اجرا کنید (پروفایل پیش‌فرض: local)
./mvnw spring-boot:run
```

بعد از بالا آمدن:

- اپلیکیشن: `http://localhost:8081`
- Swagger UI: `http://localhost:8081/swagger-ui.html`

> پورت‌های پیش‌فرض: PostgreSQL روی هاست از طریق **`5440`** در دسترس است
> (نه `5432` — آن پورت داخلی کانتینر است)، و خود اپلیکیشن روی **`8081`**.
> این مقادیر در `template.env`، `application.yaml` و `docker-compose.yml`
> هماهنگ نگه داشته شده‌اند.

در پروفایل `local`، تمام سرویس‌های پایتون (Intent، Rerank، LLM، OCR) با
نسخه‌های **Fake** جایگزین می‌شوند (داده‌ی ساختگی برمی‌گردانند) — یعنی برای
توسعه‌ی محلی **هیچ سرویس پایتونی لازم نیست بالا باشد.**

---

## پروفایل‌ها (local / dev / test / prod)

این پروژه برای هر سرویس خارجی دو پیاده‌سازی دارد: `Fake*Client` (داده‌ی
ساختگی، بدون شبکه) و `Real*Client` (تماس HTTP واقعی). انتخاب بین آن‌ها با
Spring Profile کنترل می‌شود:

| پروفایل | Intent / Rerank / LLM / OCR | Embedding | کاربرد |
|---|---|---|---|
| `local` (پیش‌فرض) | Fake | Fake (هش متن، بدون API واقعی) | توسعه‌ی روزمره بدون نیاز به سرویس‌های پایتون یا کلید OpenAI |
| `dev` | Fake | Fake | محیط تست داخلی مشابه local |
| `test` | Fake (به‌علاوه‌ی OCR) | — | اجرای تست‌های خودکار |
| `prod` | **Real** (HTTP به سرویس‌های پایتون) | **Real** (OpenAI API) | استقرار واقعی |

برای اجرا با پروفایل `prod`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

در این حالت، URL هر سرویس پایتون باید از طریق متغیر محیطی مربوطه (جدول زیر)
ست شود، وگرنه مقدار پیش‌فرض (`http://intent-service` و مشابه — مناسب فقط
برای شبکه‌ی داخلی Docker با همین نام سرویس) استفاده می‌شود.

---

## متغیرهای محیطی

| متغیر | پیش‌فرض | توضیح |
|---|---|---|
| `DB_HOST` | `localhost` | هاست PostgreSQL |
| `DB_PORT` | `5440` | پورت PostgreSQL **در دسترس از هاست** (نه پورت داخلی کانتینر) |
| `DB_NAME` | `platform_db` | نام دیتابیس |
| `DB_USERNAME` | `platform_user` | یوزر دیتابیس |
| `DB_PASSWORD` | `platform123` | پسورد دیتابیس |
| `MONGO_HOST` | `localhost` | هاست MongoDB |
| `MONGO_PORT` | `27017` | پورت MongoDB |
| `MONGO_DB` | `platform_mongo` | نام دیتابیس Mongo |
| `REDIS_HOST` | `localhost` | هاست Redis |
| `REDIS_PORT` | `6379` | پورت Redis |
| `SERVER_PORT` | `8081` | پورت خود اپلیکیشن |
| `OCR_TESSDATA_PATH` | `src/main/resources/tessdata` | مسیر مدل زبان Tesseract (پروفایل local) |
| `OCR_LANGUAGE` | `fas` | زبان OCR محلی |
| `OPENAI_API_KEY` | _(خالی)_ | کلید OpenAI، فقط برای پروفایل `prod` (embedding) لازم است |
| `INTENT_SERVICE_URL` | `http://intent-service` | **آدرس سرویس پایتون تشخیص نیت** |
| `RERANK_SERVICE_URL` | `http://rerank-service` | **آدرس سرویس پایتون rerank** |
| `LLM_SERVICE_URL` | `http://llm-service` | **آدرس سرویس پایتون LLM** |
| `OCR_SERVICE_URL` | `http://ocr-service` | **آدرس سرویس پایتون OCR** |
| `QA_SEARCH_TOP_K` | `10` | تعداد کاندیدای اولیه‌ی جست‌وجوی برداری qa_pairs |
| `QA_SEARCH_MIN_VECTOR_SIMILARITY` | `0.55` | حد آستانه‌ی شباهت کسینوسی برای ورود به مرحله‌ی rerank |
| `QA_SEARCH_MIN_RERANK_SCORE` | `0.78` | حد آستانه‌ی نهایی برای قبول پاسخ qa_pairs (وگرنه fallback به LLM) |

این متغیرها در `template.env` با مقادیر مناسب برای اجرای **اپ روی هاست +
زیرساخت در Docker** از پیش تنظیم شده‌اند. آن را کپی کنید:

```bash
cp template.env .env
```

---

## قرارداد API سرویس‌های پایتون

هر سرویس پایتون باید دقیقاً این قرارداد JSON را پیاده‌سازی کند. تمام تماس‌ها
از سمت جاوا با `WebClient` و body از نوع JSON (یا multipart برای OCR) ارسال
می‌شوند.

### 1) Intent Service

```
POST {INTENT_SERVICE_URL}/detect
```

**Request:**
```json
{
  "text": "قیمت کلاس جاوا چقدر است؟"
}
```

**Response:**
```json
{
  "intent": "pricing",
  "confidence": 0.93
}
```

- `intent` باید یکی از این چهار مقدار باشد (**حروف کوچک، دقیقاً همین رشته‌ها**):
  `"faq"`, `"pricing"`, `"class_search"`, `"llm"`.
- مقدار غیرمنتظره، خالی، یا تایم‌اوت (۵ ثانیه) → جاوا به‌صورت خودکار fallback
  می‌کند به `"llm"` و درخواست را ادامه می‌دهد (سیستم کرش نمی‌کند).
- `confidence` فقط برای لاگ استفاده می‌شود؛ منطق مسیریابی به آن وابسته نیست.

### 2) Rerank Service

```
POST {RERANK_SERVICE_URL}/rerank
```

**Request:**
```json
{
  "query": "دوره جاوا چقدره؟",
  "candidates": [
    { "id": "12", "text": "هزینه دوره جاوا چنده", "vectorScore": 0.81 },
    { "id": "27", "text": "ثبت‌نام دوره پایتون",   "vectorScore": 0.64 }
  ]
}
```

**Response:**
```json
{
  "results": [
    { "id": "12", "finalScore": 0.93 },
    { "id": "27", "finalScore": 0.41 }
  ]
}
```

- `id` رشته‌ای و بدون قید خاصی است (فقط باید با `id` همان کاندیدای ورودی مطابقت داشته باشد).
- ترتیب آیتم‌های `results` مهم نیست؛ جاوا خودش بر اساس `finalScore` مرتب می‌کند.
- در صورت خطا یا تایم‌اوت (۸ ثانیه)، جاوا به‌صورت خودکار fallback می‌کند به
  همان ترتیب `vectorScore` خام (کیفیت پایین‌تر، اما سیستم سرپا می‌ماند).

### 3) LLM Service

```
POST {LLM_SERVICE_URL}/ask
```

**Request:**
```json
{
  "question": "تاریخچه گفتگو:\nuser: ...\nassistant: ...\n\nسوال کاربر: ...\nپاسخ کوتاه و صمیمی:"
}
```

**Response:**
```json
{
  "answer": "متن پاسخ نهایی که مستقیماً به کاربر نمایش داده می‌شود."
}
```

- فیلد `question` در واقع کل prompt نهایی (شامل تاریخچه‌ی گفتگو) است، نه فقط
  آخرین جمله‌ی کاربر — منطق ساخت این prompt در `OrchestratorService.buildPrompt`
  انجام می‌شود.
- تایم‌اوت: ۱۵ ثانیه. در صورت خطا/تایم‌اوت/پاسخ خالی، جاوا یک پیام عذرخواهی
  ثابت فارسی برمی‌گرداند (سیستم کرش نمی‌کند).

### 4) OCR Service (اختیاری)

```
POST {OCR_SERVICE_URL}/ocr
Content-Type: multipart/form-data
```

**Request:** یک فیلد فایل با کلید `file` (تصویر).

**Response:**
```json
{
  "text": "متن استخراج‌شده از تصویر",
  "confidence": 0.95
}
```

> این سرویس فعلاً به مسیر اصلی چت (`OrchestratorService`) وصل نیست؛ فقط از
> طریق اندپوینت مجزای `POST /api/v1/ocr/upload` قابل استفاده است (نگاه کنید
> به بخش [اندپوینت‌ها](#اندپوینتهای-rest-پروژه)).

---

## اندپوینت‌های REST پروژه

### چت

| متد | مسیر | توضیح |
|---|---|---|
| `POST` | `/api/chat` (JSON) | ارسال سوال، دریافت پاسخ نهایی |
| `POST` | `/api/chat` (multipart) | مثل بالا، با امکان پیوست فایل (فیلد `file` فعلاً در ارکستراتور پردازش نمی‌شود) |
| `POST` | `/api/chat-sessions` | ساخت session جدید (`userId` اختیاری؛ خالی → `guest-user`) |
| `POST` | `/api/chat-sessions/{sessionId}/messages` | افزودن دستی یک پیام به تاریخچه |
| `GET`  | `/api/chat-sessions/{sessionId}/history` | دریافت کامل تاریخچه‌ی یک session |

نمونه‌ی درخواست چت:

```bash
curl -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "قیمت کلاس جاوا چقدر است؟"}'
```

پاسخ:

```json
{
  "success": true,
  "answer": "💰 قیمت Java: 4500000 تومان",
  "error": null,
  "sessionId": "f3c1a2e4-..."
}
```

برای ادامه‌ی همان گفتگو (سوال پیگیری)، `sessionId` دریافتی را در درخواست بعدی
ارسال کنید:

```bash
curl -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{"question": "مدرسش کیه؟", "sessionId": "f3c1a2e4-..."}'
```

### دوره‌ها (Course) — فقط خواندنی در فاز فعلی

| متد | مسیر | توضیح |
|---|---|---|
| `GET` | `/api/courses/all` | تمام دوره‌ها بدون صفحه‌بندی |
| `GET` | `/api/courses?keyword=...` | جست‌وجو/لیست با صفحه‌بندی (`?page=&size=&sort=`) |
| `GET` | `/api/courses/{id}` | دوره با شناسه |
| `GET` | `/api/courses/by-name?name=...` | دوره با نام دقیق |
| `GET` | `/api/courses/{id}/details` | جزئیات یک دوره (قیمت، مدرس، مدت و...) |
| `GET` | `/api/courses/details/search?keyword=...` | جست‌وجو در جزئیات دوره‌ها |

> اندپوینت‌های create/update/delete دوره عمداً در این فاز پروژه پیاده‌سازی
> نشده‌اند (در `CourseController` به‌صورت کامنت موجودند، برای فاز بعدی).

### OCR (مستقل از مسیر چت)

| متد | مسیر | توضیح |
|---|---|---|
| `POST` | `/api/v1/ocr/upload` | آپلود تصویر، استخراج متن، ذخیره در Mongo |
| `GET`  | `/api/v1/ocr/{id}` | دریافت متن استخراج‌شده‌ی یک تصویر ذخیره‌شده |
| `GET`  | `/api/v1/ocr/{id}/image` | دانلود تصویر اصلی |

### فرمت پاسخ عمومی

اندپوینت‌های `course` و `ocr` از یک پوشش (envelope) یکسان استفاده می‌کنند:

```json
{
  "success": true,
  "message": "Success",
  "data": { ... }
}
```

اندپوینت‌های `chat` و `chat-sessions` به‌جای این پوشش، DTOهای اختصاصی خودشان
(`ChatResponse`, `ChatSession`, ...) را مستقیماً برمی‌گردانند.

### Swagger / OpenAPI

مستندات تعاملی کامل (تولید خودکار از کد) همیشه در دسترس است:

```
http://localhost:8081/swagger-ui.html
```

---

## دیتابیس و Migration‌ها

PostgreSQL با Flyway مدیریت می‌شود (`src/main/resources/db/migration`):

| فایل | محتوا |
|---|---|
| `V1__init_courses.sql` | جدول `courses` و `course_details` |
| `V2__qa_intent_embedding.sql` | جدول `qa_pairs` (با ستون بردار pgvector)، توابع جست‌وجوی برداری |
| `V3__career_module.sql` | جدول‌های مسیر شغلی/پیش‌نیاز |
| `V4__chatlog_feedback.sql` | جدول‌ها/view‌های گزارش‌گیری چت |

MongoDB با **Mongock** مدیریت می‌شود (پکیج
`com.example.platform.config.migration`) — مجموعه‌های `chat_sessions` و
`ocr_images` را می‌سازد.

> هیچ migration دستی لازم نیست — هم Flyway و هم Mongock در زمان بالا آمدن
> اپلیکیشن خودکار اجرا می‌شوند.

---

## اجرای تست‌ها

```bash
./mvnw test
```

تست‌های موجود:
- `CourseQueryNormalizerTest` — نگاشت نام زبان‌های برنامه‌نویسی (فارسی/انگلیسی) به کلیدواژه‌ی جست‌وجو
- `PlatformApplicationTests` — تست بالا آمدن Spring context

---

## ساختار پروژه

```
src/main/java/com/example/platform/
├── orchestration/          # هسته‌ی منطقی: OrchestratorService + CourseQueryNormalizer
├── modules/
│   ├── chatlog/             # session، تاریخچه، DTOهای چت، LLMClient
│   ├── qa/                  # FAQ، جست‌وجوی qa_pairs، IntentClient
│   ├── rerank/               # RerankClient (Fake/Real) + فرمول هیبریدی امتیازدهی
│   ├── embedding/            # EmbeddingClient (OpenAI / Fake)
│   ├── cache/                # SemanticCacheService (Redis)
│   ├── course/               # CRUD (فعلاً فقط خواندنی) دوره‌ها
│   ├── search/                # VectorSimilarityService، PgVectorUtils
│   └── ocr/                   # OCRClient (Fake/Real) + اندپوینت مستقل OCR
├── common/                  # ApiResponse، GlobalExceptionHandler، ChatIntents
├── infrastructure/           # کانفیگ‌های Redis/WebClient/OpenAI
└── testui/                   # صفحه‌ی داخلی تست چت با Thymeleaf (فقط dev)
```

---

## وضعیت فعلی / محدودیت‌های شناخته‌شده

برای شفافیت کامل با تیم‌های مصرف‌کننده (از جمله تیم پایتون):

- آپلود فایل در `POST /api/chat` در سطح DTO/Controller پشتیبانی می‌شود، اما
  `OrchestratorService` فعلاً فیلد `file` را پردازش نمی‌کند (OCR هنوز در
  مسیر چت سیم‌کشی نشده — به‌صورت عمدی، طبق تصمیم فعلی پروژه).
- اندپوینت‌های create/update/delete برای `Course`/`CourseDetail` در این فاز
  غیرفعال‌اند (در کد کامنت شده‌اند، نه حذف شده).
- کش معنایی (`SemanticCacheService`) با رشد زیاد تعداد آیتم‌های کش، از نظر
  پیچیدگی محاسباتی همچنان O(n) است (هرچند رفت‌وبرگشت شبکه به Redis به O(1)
  کاهش یافته است)؛ برای مقیاس بسیار بزرگ، گزینه‌ی بعدی انتقال به
  pgvector یا RediSearch خواهد بود.
- نگاشت نام دوره‌ها (`CourseQueryNormalizer`) فعلاً یک Map ثابت در کد جاوا
  است؛ افزودن دوره/زبان جدید نیاز به تغییر کد و دیپلوی مجدد دارد (نه یک
  جدول دیتابیس قابل‌ویرایش از پنل ادمین).
