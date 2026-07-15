import { Paperclip, Plus, Send, Sparkles } from 'lucide-react';
import { FormEvent, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useChat } from '../features/chat/hooks/useChat';
import { StateBlock } from '../shared/ui';

export function ChatPage() {
  const { t } = useTranslation();
  const suggestions = t('chat.suggestions', { returnObjects: true }) as string[];
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
      <div className="relative flex flex-col gap-4 overflow-hidden rounded-[2rem] border border-[var(--color-border)] bg-[linear-gradient(135deg,color-mix(in_oklab,var(--color-primary)_12%,var(--color-surface)),var(--color-surface))] p-4 shadow-sm sm:flex-row sm:items-center sm:justify-between sm:p-5">
        <div className="pointer-events-none absolute -end-14 -top-20 h-44 w-44 rounded-full bg-[var(--color-primary-soft)] blur-3xl" />
        <div className="relative flex items-center gap-4">
          <div className="grid h-12 w-12 shrink-0 place-items-center rounded-2xl bg-[var(--color-primary)] text-white shadow-lg shadow-[var(--color-primary-soft)]"><Sparkles size={23} /></div>
          <div>
          <h3 className="text-xl font-bold">{t('chat.title')}</h3>
          <p className="mt-1 text-sm text-[var(--color-muted)]">
            {t('chat.connected')}
            {' — '}{sessionId ? t('chat.session', { id: sessionId.slice(0, 8) }) : t('chat.newSessionHint')}
          </p>
          </div>
        </div>
        <button
          type="button"
          onClick={startNewSession}
          className="relative inline-flex items-center justify-center gap-2 rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-2.5 text-sm font-semibold text-[var(--color-text)] shadow-sm transition hover:-translate-y-0.5 hover:border-[var(--color-primary)]"
        >
          <Plus size={16} />
          {t('chat.newConversation')}
        </button>
      </div>

      <div className="flex min-h-0 flex-1 flex-col overflow-hidden rounded-[2rem] border border-[var(--color-border)] bg-[color-mix(in_oklab,var(--color-surface)_94%,transparent)] shadow-[var(--shadow-card)] backdrop-blur-sm">
        <div className="scrollbar-hidden flex-1 space-y-4 overflow-y-auto p-4 sm:p-6">
          {historyLoading ? <StateBlock title={t('chat.historyLoading')} /> : null}

          {!historyLoading && messages.length === 0 ? (
            <div className="flex h-full flex-col items-center justify-center text-center">
              <div className="grid h-16 w-16 place-items-center rounded-3xl bg-[color-mix(in_oklab,var(--color-primary)_16%,transparent)] text-[var(--color-primary)]">
                <Sparkles size={28} />
              </div>
              <h4 className="mt-5 text-lg font-bold">{t('chat.ask')}</h4>
              <p className="mt-2 max-w-md text-sm leading-7 text-[var(--color-muted)]">
                {t('chat.description')}
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
                'max-w-[85%] rounded-3xl px-4 py-3 text-sm leading-7 shadow-sm sm:max-w-[75%]',
                message.role === 'user'
                  ? 'ml-auto bg-[var(--color-primary)] text-white'
                  : message.error
                    ? 'mr-auto border border-red-300 bg-red-50 text-red-800 dark:border-red-800 dark:bg-red-950/40 dark:text-red-200'
                    : 'mr-auto bg-[var(--color-page)] text-[var(--color-text)]',
              ].join(' ')}
            >
              <p className="whitespace-pre-wrap">{message.content}</p>
            </div>
          ))}

          {loading ? (
            <div className="mr-auto max-w-[75%] rounded-3xl bg-[var(--color-page)] px-4 py-3 text-sm text-[var(--color-muted)]">
              {t('chat.processing')}
            </div>
          ) : null}

          <div ref={bottomRef} />
        </div>

        <form onSubmit={handleSubmit} className="border-t border-[var(--color-border)] p-4">
          {file ? (
            <div className="mb-3 flex items-center justify-between rounded-2xl bg-[var(--color-page)] px-4 py-2 text-sm">
              <span className="truncate text-[var(--color-muted)]">📎 {file.name}</span>
              <button type="button" onClick={() => { setFile(null); if (fileInputRef.current) fileInputRef.current.value = ''; }} className="text-[var(--color-primary)]">
                {t('chat.removeFile')}
              </button>
            </div>
          ) : null}

          {error ? <p className="mb-2 text-xs text-red-500">{error}</p> : null}

          <div className="flex gap-2">
            <input ref={fileInputRef} type="file" className="hidden" onChange={(e) => setFile(e.target.files?.[0] ?? null)} />
            <button
              type="button"
              onClick={() => fileInputRef.current?.click()}
              title={t('chat.attachTitle')}
              className="grid h-12 w-12 shrink-0 place-items-center rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] text-[var(--color-muted)] transition hover:text-[var(--color-primary)]"
            >
              <Paperclip size={18} />
            </button>
            <input
              value={input}
              onChange={(event) => setInput(event.target.value)}
              placeholder={t('chat.placeholder')}
              disabled={loading}
              className="flex-1 rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-3 text-sm text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] placeholder:text-[var(--color-muted)] focus:ring-4 disabled:opacity-60"
            />
            <button
              type="submit"
              disabled={loading || !input.trim()}
              className="inline-flex h-12 items-center gap-2 rounded-2xl bg-[var(--color-primary)] px-5 font-bold text-white shadow-lg shadow-[var(--color-primary-soft)] transition hover:opacity-90 disabled:cursor-not-allowed disabled:opacity-50"
            >
              <Send size={16} />
              <span className="hidden sm:inline">{t('chat.send')}</span>
            </button>
          </div>
          <p className="mt-2 text-xs text-[var(--color-muted)]">
            {t('chat.fileHint')}
          </p>
        </form>
      </div>
    </div>
  );
}
