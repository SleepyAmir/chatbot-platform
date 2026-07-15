import { useEffect, useState } from 'react';
import { ChevronDown, HelpCircle, Search } from 'lucide-react';
import { useTranslation } from 'react-i18next';
import { getAllIntents, getQaPairsByIntent, searchQaPairs } from '../api/qa.api';
import { PageHeader, StateBlock } from '../shared/ui';
import type { Intent, QaPair } from '../types/qa';

export function QaPage() {
  const { t } = useTranslation();
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
      <PageHeader title={t('faq.title')} description={t('faq.subtitle')} icon={<HelpCircle size={23} />} />

      <div className="flex flex-col gap-3 rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-3 shadow-sm md:flex-row">
        <label className="flex flex-1 items-center gap-3 rounded-2xl bg-[var(--color-page)] px-4 focus-within:ring-4 focus-within:ring-[var(--color-primary-soft)]">
          <Search size={18} className="text-[var(--color-muted)]" />
          <input value={keyword} onChange={(event) => { setKeyword(event.target.value); setSelectedIntent(''); }} disabled={Boolean(selectedIntent)} placeholder={t('faq.search')} className="min-w-0 flex-1 bg-transparent py-3 text-sm outline-none placeholder:text-[var(--color-muted)] disabled:opacity-50" />
        </label>
        <select
          value={selectedIntent}
          onChange={(event) => {
            setSelectedIntent(event.target.value);
            if (event.target.value) setKeyword('');
          }}
          className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-3 text-sm outline-none focus:ring-4 focus:ring-[var(--color-primary-soft)]"
        >
          <option value="">{t('faq.allCategories')}</option>
          {intents.map((intent) => (
            <option key={intent.id} value={intent.name}>
              {intent.name}
            </option>
          ))}
        </select>
      </div>

      {loading ? <StateBlock title={t('faq.loading')} /> : null}
      {error ? <StateBlock title={t('common.error')} description={error} /> : null}

      {!loading && !error ? (
        <div className="space-y-3">
          {qaPairs.length ? (
            qaPairs.map((qa) => (
              <article
                key={qa.id}
                className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm transition hover:border-[var(--color-primary)]"
              >
                <button
                  type="button"
                  onClick={() => setExpandedId(expandedId === qa.id ? null : qa.id)}
                  className="w-full text-start"
                >
                  <div className="flex items-start justify-between gap-3">
                    <p className="font-bold leading-7">{qa.question}</p>
                    <span className="flex shrink-0 items-center gap-2 text-xs text-[var(--color-muted)]">#{qa.id}<ChevronDown size={17} className={`transition ${expandedId === qa.id ? 'rotate-180 text-[var(--color-primary)]' : ''}`} /></span>
                  </div>
                  {qa.courseName ? (
                    <p className="mt-2 text-xs text-[var(--color-primary)]">{t('faq.course', { name: qa.courseName })}</p>
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
            <StateBlock title={t('faq.empty')} />
          )}
        </div>
      ) : null}
    </div>
  );
}
