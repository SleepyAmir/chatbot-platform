import { useEffect, useState } from 'react';
import { getAllIntents, getQaPairsByIntent, searchQaPairs } from '../api/qa.api';
import { StateBlock } from '../shared/ui';
import type { Intent, QaPair } from '../types/qa';

export function QaPage() {
  const [qaPairs, setQaPairs] = useState<QaPair[]>([]);
  const [intents, setIntents] = useState<Intent[]>([]);
  const [keyword, setKeyword] = useState('');
  const [selectedIntent, setSelectedIntent] = useState('');
  const [expandedId, setExpandedId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getAllIntents()
      .then(setIntents)
      .catch(() => setIntents([]));
  }, []);

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      setLoading(true);
      const request = selectedIntent
        ? getQaPairsByIntent(selectedIntent)
        : searchQaPairs(keyword || undefined);

      request
        .then((data) => {
          setQaPairs(data);
          setError(null);
        })
        .catch((err: Error) => setError(err.message))
        .finally(() => setLoading(false));
    }, 250);

    return () => window.clearTimeout(timeout);
  }, [keyword, selectedIntent]);

  return (
    <div className="space-y-5">
      <div>
        <h3 className="text-xl font-bold">بانک سوال‌وجواب (FAQ)</h3>
        <p className="mt-1 text-sm text-[var(--color-muted)]">
          مرور دستی QA — جستجوی هوشمند از طریق <code className="text-xs">/api/chat</code> انجام می‌شود
        </p>
      </div>

      <div className="flex flex-col gap-3 md:flex-row">
        <input
          value={keyword}
          onChange={(event) => {
            setKeyword(event.target.value);
            setSelectedIntent('');
          }}
          disabled={Boolean(selectedIntent)}
          placeholder="جستجوی کلمه‌کلیدی..."
          className="flex-1 rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-3 text-sm outline-none ring-[var(--color-primary-soft)] placeholder:text-[var(--color-muted)] focus:ring-4 disabled:opacity-50"
        />
        <select
          value={selectedIntent}
          onChange={(event) => {
            setSelectedIntent(event.target.value);
            if (event.target.value) setKeyword('');
          }}
          className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-3 text-sm outline-none focus:ring-4 focus:ring-[var(--color-primary-soft)]"
        >
          <option value="">همه دسته‌ها</option>
          {intents.map((intent) => (
            <option key={intent.id} value={intent.name}>
              {intent.name}
            </option>
          ))}
        </select>
      </div>

      {loading ? <StateBlock title="در حال دریافت سوالات..." /> : null}
      {error ? <StateBlock title="خطا" description={error} /> : null}

      {!loading && !error ? (
        <div className="space-y-3">
          {qaPairs.length ? (
            qaPairs.map((qa) => (
              <article
                key={qa.id}
                className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm"
              >
                <button
                  type="button"
                  onClick={() => setExpandedId(expandedId === qa.id ? null : qa.id)}
                  className="w-full text-right"
                >
                  <div className="flex items-start justify-between gap-3">
                    <p className="font-bold leading-7">{qa.question}</p>
                    <span className="shrink-0 text-xs text-[var(--color-muted)]">#{qa.id}</span>
                  </div>
                  {qa.courseName ? (
                    <p className="mt-2 text-xs text-[var(--color-primary)]">دوره: {qa.courseName}</p>
                  ) : null}
                </button>
                {expandedId === qa.id ? (
                  <p className="mt-4 rounded-2xl bg-[var(--color-page)] p-4 text-sm leading-8 text-[var(--color-muted)]">
                    {qa.answer}
                  </p>
                ) : null}
              </article>
            ))
          ) : (
            <StateBlock title="سوالی یافت نشد" />
          )}
        </div>
      ) : null}
    </div>
  );
}
