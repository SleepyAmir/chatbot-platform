import { Paperclip, Plus, Send, Sparkles } from 'lucide-react';
import { FormEvent, useRef, useState } from 'react';
import { useChat } from '../features/chat/hooks/useChat';
import { StateBlock } from '../shared/ui';

const suggestions = [
  'قیمت دوره جاوا چقدره؟',
  'برای بک‌اند از کجا شروع کنم؟',
  'دوره‌های مرتبط با Python چیه؟',
];

export function ChatPage() {
  const [input, setInput] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const { messages, sessionId, loading, historyLoading, error, bottomRef, sendMessage, startNewSession } = useChat();

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    void sendMessage(input, file).then(() => {
      setInput('');
      setFile(null);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    });
  };

  return (
    <div className="flex h-[calc(100vh-8rem)] flex-col gap-4">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h3 className="text-xl font-bold">دستیار آموزشی</h3>
          <p className="mt-1 text-sm text-[var(--color-muted)]">
            اتصال به <code className="text-xs">POST /api/chat</code>
            {sessionId ? ` — سشن: ${sessionId.slice(0, 8)}…` : ' — سشن جدید با اولین پیام ساخته می‌شود'}
          </p>
        </div>
        <button
          type="button"
          onClick={startNewSession}
          className="inline-flex items-center justify-center gap-2 rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-2.5 text-sm font-semibold text-[var(--color-text)] transition hover:border-[var(--color-primary)]"
        >
          <Plus size={16} />
          مکالمه جدید
        </button>
      </div>

      <div className="flex min-h-0 flex-1 flex-col rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] shadow-[var(--shadow-card)]">
        <div className="flex-1 space-y-4 overflow-y-auto p-4 sm:p-6">
          {historyLoading ? <StateBlock title="در حال بارگذاری تاریخچه..." /> : null}

          {!historyLoading && messages.length === 0 ? (
            <div className="flex h-full flex-col items-center justify-center text-center">
              <div className="grid h-16 w-16 place-items-center rounded-3xl bg-[color-mix(in_oklab,var(--color-primary)_16%,transparent)] text-[var(--color-primary)]">
                <Sparkles size={28} />
              </div>
              <h4 className="mt-5 text-lg font-bold">سوالت را بپرس</h4>
              <p className="mt-2 max-w-md text-sm leading-7 text-[var(--color-muted)]">
                درباره دوره‌ها، قیمت، مسیر شغلی و FAQ می‌توانی سوال بپرسی. پاسخ از cache، QA یا course lookup می‌آید.
              </p>
              <div className="mt-6 flex flex-wrap justify-center gap-2">
                {suggestions.map((suggestion) => (
                  <button
                    key={suggestion}
                    type="button"
                    onClick={() => setInput(suggestion)}
                    className="rounded-full border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-2 text-sm text-[var(--color-text)] transition hover:border-[var(--color-primary)]"
                  >
                    {suggestion}
                  </button>
                ))}
              </div>
            </div>
          ) : null}

          {messages.map((message) => (
            <div
              key={message.id}
              className={[
                'max-w-[85%] rounded-3xl px-4 py-3 text-sm leading-7 sm:max-w-[75%]',
                message.role === 'user'
                  ? 'mr-auto bg-[var(--color-primary)] text-white'
                  : message.error
                    ? 'ml-auto border border-red-300 bg-red-50 text-red-800 dark:border-red-800 dark:bg-red-950/40 dark:text-red-200'
                    : 'ml-auto bg-[var(--color-page)] text-[var(--color-text)]',
              ].join(' ')}
            >
              <p className="whitespace-pre-wrap">{message.content}</p>
            </div>
          ))}

          {loading ? (
            <div className="ml-auto max-w-[75%] rounded-3xl bg-[var(--color-page)] px-4 py-3 text-sm text-[var(--color-muted)]">
              در حال پردازش...
            </div>
          ) : null}

          <div ref={bottomRef} />
        </div>

        <form onSubmit={handleSubmit} className="border-t border-[var(--color-border)] p-4">
          {file ? (
            <div className="mb-3 flex items-center justify-between rounded-2xl bg-[var(--color-page)] px-4 py-2 text-sm">
              <span className="truncate text-[var(--color-muted)]">📎 {file.name}</span>
              <button type="button" onClick={() => { setFile(null); if (fileInputRef.current) fileInputRef.current.value = ''; }} className="text-[var(--color-primary)]">
                حذف
              </button>
            </div>
          ) : null}

          {error ? <p className="mb-2 text-xs text-red-500">{error}</p> : null}

          <div className="flex gap-2">
            <input ref={fileInputRef} type="file" className="hidden" onChange={(e) => setFile(e.target.files?.[0] ?? null)} />
            <button
              type="button"
              onClick={() => fileInputRef.current?.click()}
              title="ارسال فایل (فعلاً پردازش OCR وصل نیست)"
              className="grid h-12 w-12 shrink-0 place-items-center rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] text-[var(--color-muted)] transition hover:text-[var(--color-primary)]"
            >
              <Paperclip size={18} />
            </button>
            <input
              value={input}
              onChange={(event) => setInput(event.target.value)}
              placeholder="سوالت را بنویس..."
              disabled={loading}
              className="flex-1 rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-3 text-sm text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] placeholder:text-[var(--color-muted)] focus:ring-4 disabled:opacity-60"
            />
            <button
              type="submit"
              disabled={loading || !input.trim()}
              className="inline-flex h-12 items-center gap-2 rounded-2xl bg-[var(--color-primary)] px-5 font-bold text-white shadow-lg shadow-[var(--color-primary-soft)] transition hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
            >
              <Send size={16} />
              <span className="hidden sm:inline">ارسال</span>
            </button>
          </div>
          <p className="mt-2 text-xs text-[var(--color-muted)]">
            ارسال فایل از multipart پشتیبانی می‌شود؛ فعلاً بک‌اند فایل را پردازش نمی‌کند.
          </p>
        </form>
      </div>
    </div>
  );
}
