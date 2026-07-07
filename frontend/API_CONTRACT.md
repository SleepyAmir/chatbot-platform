# API Contract — تا این مرحله از پروژه

این سند دقیقاً همون چیزیه که الان روی بک‌اند پیاده‌سازی و تست شده. هر endpoint که پایینه، واقعاً کار می‌کنه (نه برنامه‌ریزی‌شده برای بعد).

**نکته‌ی عمومی:** همه‌ی endpointها (به‌جز `/api/chat-sessions/*`) پاسخ رو توی این پوشش برمی‌گردونن:
```json
{ "success": true, "message": "...", "data": { ... } }
```
یعنی همون چیزی که `data` نگه می‌داره، دقیقاً شکلیه که پایین‌تر براش نوشتم.

خطاها هم همیشه با کد HTTP مناسب (404, 400, ...) و بدنه‌ی `{success: false, message: "..."}` برمی‌گردن.

---

## چت (Orchestration) — `/api/chat`

### `POST /api/chat`
دو نسخه داره، بسته به `Content-Type`:
- `application/json` → برای پیام متنی معمولی
- `multipart/form-data` → وقتی فایل هم قراره ارسال بشه (فیلد `file` — **توجه: فعلاً هیچ پردازشی روی فایل انجام نمیشه، فقط دریافت میشه و نادیده گرفته میشه**؛ OCR واقعی هنوز وصل نیست)

**Request:**
```json
{
  "question": "قیمت دوره جاوا چقدره؟",
  "sessionId": "اختیاری - اگه نفرستی، یه سشن جدید ساخته میشه",
  "userId": "اختیاری"
}
```

**Response (`data`):**
```json
{
  "success": true,
  "answer": "💰 قیمت Java: 2500000 تومان",
  "error": null,
  "sessionId": "70c6f0f4-...",
  "traceId": "d839189f",
  "elapsedMs": 80,
  "matchedQaId": null,
  "confidence": null,
  "modelUsed": "course_lookup"
}
```
- `sessionId` رو حتماً نگه دار و توی پیام بعدی همین مکالمه بفرست، وگرنه هر پیام یه مکالمه‌ی جدید حساب میشه (بدون تاریخچه).
- `modelUsed` یکیه از: `"cache"`, `"qa_pairs"`, `"llm"`, `"course_lookup"` — صرفاً برای دیباگ/نمایش داخلیه، لازم نیست توی UI نشونش بدی.
- `success: false` یعنی خطای منطقی (نه HTTP error) — مثلاً سوال خالی؛ `error` توضیحش رو داره.

---

## تاریخچه‌ی مکالمه (Mongo) — `/api/chat-sessions`

⚠️ این سه‌تا **بدون پوشش `ApiResponse`** هستن، مستقیم آبجکت خام برمی‌گردونن (فرق داره با بقیه‌ی پروژه، حواستون باشه).

### `POST /api/chat-sessions`
```json
// Request
{ "userId": "اختیاری، خالی باشه guest-user میشه" }

// Response (خام، بدون data/success)
{
  "id": "...", "sessionId": "...", "userId": "...",
  "createdAt": "...", "updatedAt": "...",
  "messages": [], "lastTopic": null, "lastEntityId": null
}
```

### `POST /api/chat-sessions/{sessionId}/messages`
```json
// Request
{ "role": "user", "content": "متن پیام" }  // role فقط "user" یا "assistant"
// Response: همون ChatSession کامل (بالا)
```

### `GET /api/chat-sessions/{sessionId}/history`
```json
// Response: آرایه‌ای از پیام‌ها
[{ "role": "user", "content": "...", "timestamp": "..." }]
```

معمولاً لازم نیست مستقیم اینا رو صدا بزنی — `POST /api/chat` خودش پشت‌صحنه این کارو می‌کنه. این‌ها بیشتر برای دیباگ/نمایش تاریخچه‌ن.

---

## دوره‌ها — `/api/courses`

| متد | مسیر | توضیح |
|---|---|---|
| GET | `/api/courses/all` | همه‌ی دوره‌ها، بدون صفحه‌بندی |
| GET | `/api/courses?keyword=&page=0&size=12&sort=id,desc` | جستجو/لیست با صفحه‌بندی (`keyword` اختیاری) |
| GET | `/api/courses/{id}` | یک دوره |
| GET | `/api/courses/by-name?name=Java` | تطابق دقیق نام |
| GET | `/api/courses/{id}/details` | جزئیات قیمت/استاد/مدت یک دوره |
| GET | `/api/courses/details/search?keyword=&page=&size=` | جستجو در جزئیات دوره‌ها |

**`Course`:**
```json
{ "id": 1, "name": "Java", "lessonUrl": "https://..." }
```
**`CourseDetail`:**
```json
{ "id": 1, "courseId": 1, "price": "2500000", "teacher": "استاد رضایی", "duration": "۴۰ ساعت", "branch": "بک‌اند" }
```

⚠️ **فعلاً هیچ endpoint ساخت/ویرایش/حذف دوره فعال نیست** (فقط خواندنی — کد ساختش توی کنترلر هست ولی کامنت شده).

---

## مسیرهای شغلی — `/api/careers`

| متد | مسیر | توضیح |
|---|---|---|
| GET | `/api/careers/all` | همه، بدون صفحه‌بندی |
| GET | `/api/careers?keyword=&page=&size=` | لیست/جستجو با صفحه‌بندی |
| GET | `/api/careers/{id}` | یک مسیر شغلی |
| POST | `/api/careers` | ساخت (`{title, description, sourceUrl}`, فقط `title` اجباری) |
| PUT | `/api/careers/{id}` | ویرایش (همون شکل POST) |
| DELETE | `/api/careers/{id}` | حذف |
| GET | `/api/careers/{id}/skills` | مهارت‌های یک مسیر شغلی |
| POST | `/api/careers/{id}/skills` | افزودن مهارت (`{skillName}`) |
| GET | `/api/careers/{id}/requirements` | نیازمندی‌های یک مسیر شغلی |
| POST | `/api/careers/{id}/requirements` | افزودن نیازمندی (`{chunkIndex, requirementText, embedding?}` — embedding باید دقیقاً ۳۸۴ عدد باشه اگه بفرستی، اختیاریه) |
| POST | `/api/careers/search` | جستجوی معنایی (پایین توضیح دادم) |
| GET | `/api/courses/{courseId}/careers` | مسیرهای شغلی مرتبط با یه دوره |
| POST | `/api/courses/{courseId}/careers` | لینک کردن دوره به مسیر شغلی (`{careerId, relevance}` بین ۰ و ۱) |
| GET | `/api/careers/{id}/courses` | دوره‌های مرتبط با یه مسیر شغلی |

**`Career`:**
```json
{ "id": 1, "title": "توسعه‌دهنده بک‌اند", "description": "...", "sourceUrl": null, "createdAt": "..." }
```
**`CareerSkill`:** `{ "id": 1, "careerId": 1, "skillName": "Java" }`
**`CareerRequirement`:** `{ "id": 1, "careerId": 1, "chunkIndex": 0, "requirementText": "...", "hasEmbedding": true, "createdAt": "..." }`
**`CourseCareer`:** `{ "courseId": 1, "courseName": "Java", "careerId": 1, "careerTitle": "...", "relevance": 0.95 }`

### `POST /api/careers/search`
```json
// Request
{ "embedding": [0.01, 0.02, ...], "topK": 5, "minSimilarity": 0.5 }
// دقیقاً 384 عدد لازمه
```
⚠️ **این endpoint یه بردار ۳۸۴بعدی حاضر از سمت caller می‌خواد** — فرانت به‌تنهایی نمی‌تونه این بردار رو از یه متن آزاد بسازه (نیاز به embedding سرور داره). فعلاً این endpoint برای استفاده‌ی مستقیم فرانت آماده نیست، مگر یه لایه‌ی واسط اضافه بشه.
```json
// Response
[{ "requirementId": 1, "careerId": 1, "careerTitle": "...", "chunkIndex": 0, "requirementText": "...", "similarity": 0.87, "createdAt": "..." }]
```

---

## بانک سوال-جواب — `/api/qa`, `/api/qa-embeddings`, `/api/qa-intents`, `/api/intents`

فقط خواندنی، برای نمایش/جستجوی دستی توی FAQ — **جستجوی هوشمند qa_pairs از طریق `/api/chat` انجام میشه، نه این‌ها**. اگه فرانت نیازی به یه صفحه‌ی مرور FAQ داره، اینا کافیه:

| متد | مسیر | توضیح |
|---|---|---|
| GET | `/api/qa/all` | همه‌ی QA pairها |
| GET | `/api/qa?keyword=` | جستجوی کلمه‌کلیدی |
| GET | `/api/qa/{id}` | یک QA pair |
| GET | `/api/qa/{id}/detail` | با جزئیات (intent‌ها + متادیتای embedding) |
| GET | `/api/qa/by-course/{courseId}` | QAهای مرتبط با یه دوره |
| GET | `/api/qa/by-intent?name=faq` | QAهای یه دسته‌بندی خاص |
| GET | `/api/intents/all` | لیست دسته‌بندی‌ها (`faq`, `pricing`, `class_search`) |

**`QaPair`:** `{ "id": 1, "question": "...", "answer": "...", "courseId": 1, "courseName": "Java" }`

---

## لاگ تحلیلی و فیدبک — `/api/chat-logs`, `/api/feedback`

معمولاً فرانت مستقیم بهشون کاری نداره (بک‌اند خودش بعد از هر `/api/chat` این‌ها رو پر می‌کنه)، ولی اگه یه صفحه‌ی «تاریخچه‌ی گفتگوهای من» یا دکمه‌ی 👍👎 لازم داری:

| متد | مسیر | توضیح |
|---|---|---|
| GET | `/api/chat-logs?sessionId=&page=&size=` | لیست لاگ‌ها |
| GET | `/api/chat-logs/{id}` | یک لاگ |
| GET | `/api/chat-logs/session/{sessionId}` | همه‌ی لاگ‌های یه سشن |
| POST | `/api/feedback` | ثبت فیدبک: `{ "logId": 1, "rating": 1, "comment": "اختیاری" }` — `rating` فقط `1` یا `-1-` |
| GET | `/api/feedback/log/{logId}` | فیدبک‌های یه لاگ |

**استفاده‌ی پیشنهادی برای دکمه‌ی 👍👎:** بعد از `POST /api/chat`، جواب `ChatResponse` فعلاً `logId` رو برنمی‌گردونه (فقط `traceId`/`sessionId`) — یعنی الان راه مستقیمی برای وصل کردن فیدبک به یه پیام خاص از سمت فرانت نیست. **این یه گپ واقعیه، اگه این فیچر لازمه بگو تا `logId` رو هم به `ChatResponse` اضافه کنم.**

---

## کش — `/api/cache`
داخلی/مدیریتیه، احتمالاً فرانت نیازی بهش نداره:
- `GET /api/cache/top-queries?limit=10` → `[{ "query": "...", "count": 12 }]`
- `DELETE /api/cache/flush`

---

## چیزهایی که هنوز آماده نیست (به‌جای حدس زدن، صریح می‌نویسم)
- ساخت/ویرایش/حذف دوره — کامنت شده، فعال نیست
- OCR (`/api/v1/ocr/upload` که فرانت قبلاً براش صفحه ساخته) — تصمیم معماری‌ش هنوز باز است
- اتصال فیدبک به یه پیام مشخص از چت (بالا توضیح دادم)
- `/api/careers/search` از سمت فرانت مستقیم قابل استفاده نیست (نیاز به embedding سرور)
