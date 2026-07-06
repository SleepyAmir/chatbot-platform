import { AlertCircle, Bot } from 'lucide-react';
import { Link } from 'react-router-dom';

export function OcrPage() {
  return (
    <div className="space-y-5">
      <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6 shadow-sm">
        <div className="flex items-start gap-4">
          <div className="grid h-12 w-12 shrink-0 place-items-center rounded-2xl bg-[color-mix(in_oklab,var(--color-warning)_18%,transparent)] text-[var(--color-warning)]">
            <AlertCircle size={24} />
          </div>
          <div>
            <h3 className="text-xl font-bold">استعلام مدارک (OCR)</h3>
            <p className="mt-2 text-sm leading-8 text-[var(--color-muted)]">
              endpoint اختصاصی OCR فعلاً فعال نیست. طبق API Contract، تصمیم معماری OCR هنوز نهایی نشده.
            </p>
          </div>
        </div>
      </section>

      <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6 shadow-sm">
        <h4 className="font-bold">جایگزین موقت</h4>
        <p className="mt-2 text-sm leading-8 text-[var(--color-muted)]">
          می‌توانی از دستیار آموزشی فایل ضمیمه کنی — بک‌اند فعلاً فایل را دریافت می‌کند ولی پردازش OCR انجام نمی‌دهد.
        </p>
        <Link
          to="/chat"
          className="mt-5 inline-flex items-center gap-2 rounded-2xl bg-[var(--color-primary)] px-5 py-3 text-sm font-bold text-white shadow-lg shadow-[var(--color-primary-soft)]"
        >
          <Bot size={18} />
          رفتن به دستیار آموزشی
        </Link>
      </section>
    </div>
  );
}
