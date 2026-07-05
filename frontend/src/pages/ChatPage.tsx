export function ChatPage() {
  return (
    <div className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6 shadow-[var(--shadow-card)]">
      <h3 className="text-xl font-bold">چت‌بات</h3>
      <p className="mt-3 leading-8 text-[var(--color-muted)]">
        این صفحه جایگاه UI چت نسخه اول است. بعد از آماده شدن endpoint نهایی `/api/chat`
        در orchestration، همین صفحه به pipeline اصلی وصل می‌شود.
      </p>
      <div className="mt-6 rounded-3xl bg-[var(--color-page)] p-5">
        <div className="rounded-2xl bg-[color-mix(in_oklab,var(--color-primary)_14%,transparent)] p-4 text-sm text-[var(--color-primary)]">
          سلام، سوالت درباره دوره‌ها و مسیر شغلی را اینجا می‌پرسی.
        </div>
        <div className="mt-4 flex gap-3">
          <input
            disabled
            placeholder="Endpoint چت هنوز آماده نیست..."
            className="flex-1 rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-3 text-sm"
          />
          <button disabled className="rounded-2xl bg-[var(--color-surface-strong)] px-5 py-3 text-sm text-[var(--color-muted)]">
            ارسال
          </button>
        </div>
      </div>
    </div>
  );
}
