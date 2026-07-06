# راهنمای بیلد فرانت‌اند (React + Spring Boot)

این سند برای توسعه‌دهندگانی است که می‌خواهند UI پروژه را بیلد بگیرند و از طریق Spring Boot سرو کنند.

## پیش‌نیازها

| ابزار | نسخه |
|-------|------|
| Java | 21 |
| Maven | 3.9+ |
| Node.js (فقط برای توسعه محلی) | >= 20.19.0 |
| npm | >= 10 |

## ساختار

```text
frontend/                          ← سورس React + Vite + Tailwind
src/main/resources/static/         ← خروجی بیلد (بعد از npm run build)
src/main/java/.../FrontendController.java  ← SPA routing در Spring Boot
```

Vite خروجی بیلد را مستقیماً در `src/main/resources/static` می‌ریزد (`frontend/vite.config.ts`).

## روش ۱ — بیلد کامل با Maven (پیشنهادی برای CI/Production)

Maven به‌صورت خودکار Node.js را دانلود می‌کند، وابستگی‌های npm را نصب می‌کند و فرانت را بیلد می‌گیرد:

```bash
./mvnw clean package -DskipTests
```

یا فقط بیلد فرانت بدون کامپایل Java:

```bash
./mvnw generate-resources -DskipTests
```

سپس اجرای برنامه:

```bash
./mvnw spring-boot:run
```

اپلیکیشن روی `http://localhost:8080` بالا می‌آید.

## روش ۲ — توسعه محلی با Hot Reload

ترمینال ۱ — بک‌اند:

```bash
./mvnw spring-boot:run
```

ترمینال ۲ — فرانت (Vite dev server با proxy به API):

```bash
cd frontend
npm install
npm run dev
```

UI روی `http://localhost:5173` و درخواست‌های `/api/*` به پورت 8080 پروکسی می‌شوند.

## روش ۳ — بیلد دستی فرانت

```bash
cd frontend
npm install
npm run build
```

خروجی در `src/main/resources/static/` قرار می‌گیرد. بعد `./mvnw spring-boot:run` را اجرا کنید.

## SPA Routing

`FrontendController` مسیرهای زیر را به `index.html` forward می‌کند تا React Router کار کند:

- `/`
- `/courses`
- `/careers` و `/careers/{id}`
- `/ocr`
- `/chat`

APIها همچنان از `/api/**` سرو می‌شوند.

## صفحات و ماژول‌ها

| مسیر | صفحه | API |
|------|------|-----|
| `/` | داشبورد | — |
| `/courses` | دوره‌ها | `/api/courses` |
| `/careers` | مشاغل (Career Module) | `/api/careers` |
| `/careers/:id` | جزئیات شغل | `/api/careers/{id}` |
| `/chat` | چت | `/api/chatlog` |
| `/ocr` | OCR | `/api/ocr` |

> **نکته:** ماژول Career باید روی بک‌اند فعال باشد (برنچ `feature/career-module` یا master به‌روز).

## عیب‌یابی

| مشکل | راه‌حل |
|------|--------|
| صفحه سفید بعد از refresh | مطمئن شوید `FrontendController` در classpath است |
| 404 روی `/careers` | `./mvnw generate-resources` یا `npm run build` را دوباره اجرا کنید |
| خطای CORS در dev | از `npm run dev` استفاده کنید (proxy فعال است) |
| فونت‌ها لود نمی‌شوند | `public/fonts/` را چک کنید؛ بعد از بیلد در `static/fonts/` کپی می‌شوند |

## مستندات بیشتر

ساختار پوشه‌های فرانت: [`frontend/README.md`](../frontend/README.md)
