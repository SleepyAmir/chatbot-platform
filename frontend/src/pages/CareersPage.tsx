import { FormEvent, useEffect, useState } from 'react';
import { BriefcaseBusiness, Plus, Search } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { createCareer, getCareers } from '../api/careers.api';
import { PageHeader, Pagination, StateBlock } from '../shared/ui';
import type { Career } from '../types/career';

export function CareersPage() {
  const { t } = useTranslation();
  const [careers, setCareers] = useState<Career[]>([]);
  const [keyword, setKeyword] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [sourceUrl, setSourceUrl] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadCareers = () => {
    setLoading(true);
    getCareers({ keyword, page, size: 12 })
      .then((result) => {
        setCareers(result.content);
        setTotalPages(result.totalPages);
        setError(null);
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    setPage(0);
  }, [keyword]);

  useEffect(() => {
    const timeout = window.setTimeout(loadCareers, 250);
    return () => window.clearTimeout(timeout);
  }, [keyword, page]);

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSaving(true);
    createCareer({ title, description: description || undefined, sourceUrl: sourceUrl || undefined })
      .then(() => {
        setTitle('');
        setDescription('');
        setSourceUrl('');
        loadCareers();
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setSaving(false));
  };

  return (
    <div className="grid gap-5 xl:grid-cols-[1fr_360px]">
      <section className="space-y-5">
        <PageHeader
          title={t('careers.pageTitle')}
          description={t('careers.subtitle')}
          icon={<BriefcaseBusiness size={23} />}
          action={(
            <label className="flex items-center gap-3 rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-4 shadow-sm transition focus-within:border-[var(--color-primary)] focus-within:ring-4 focus-within:ring-[var(--color-primary-soft)]">
              <Search size={18} className="shrink-0 text-[var(--color-muted)]" />
              <input value={keyword} onChange={(event) => setKeyword(event.target.value)} placeholder={t('careers.search')} className="min-w-0 flex-1 bg-transparent py-3 text-sm outline-none placeholder:text-[var(--color-muted)]" />
            </label>
          )}
        />

        {loading ? <StateBlock title={t('careers.loading')} /> : null}
        {error ? <StateBlock title={t('common.error')} description={error} /> : null}

        {!loading && !error ? (
          <>
            {careers.length ? <div className="grid gap-4 md:grid-cols-2">
              {careers.map((career) => (
                <Link
                  key={career.id}
                  to={`/careers/${career.id}`}
                  className="group relative overflow-hidden rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm transition duration-300 hover:-translate-y-1 hover:border-[var(--color-warning)] hover:shadow-[var(--shadow-card)]"
                >
                  <div className="flex items-start justify-between gap-4">
                    <div className="grid h-11 w-11 place-items-center rounded-2xl bg-[color-mix(in_oklab,var(--color-warning)_16%,transparent)] text-[var(--color-warning)] transition group-hover:scale-105"><BriefcaseBusiness size={20} /></div>
                    <span className="text-xs font-bold text-[var(--color-warning)]">#{career.id}</span>
                  </div>
                  <h4 className="mt-4 font-bold leading-7 text-[var(--color-text)]">{career.title}</h4>
                  <p className="mt-3 line-clamp-3 text-sm leading-7 text-[var(--color-muted)]">
                    {career.description || t('careers.noDescription')}
                  </p>
                </Link>
              ))}
            </div> : <StateBlock title={t('empty.careers')} />}
            <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        ) : null}
      </section>

      <form onSubmit={handleSubmit} className="h-fit rounded-[2rem] border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-[var(--shadow-card)] xl:sticky xl:top-28">
        <div className="flex items-center gap-3 border-b border-[var(--color-border)] pb-4">
          <div className="grid h-10 w-10 place-items-center rounded-2xl bg-[var(--color-primary)] text-white"><Plus size={19} /></div>
          <h4 className="font-bold">{t('careers.addCareer')}</h4>
        </div>
        <label className="mt-4 block text-sm text-[var(--color-muted)]">
          {t('careers.titleLabel')}
          <input
            required
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            className="mt-2 w-full rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-3 text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] focus:ring-4"
          />
        </label>
        <label className="mt-4 block text-sm text-[var(--color-muted)]">
          {t('careers.description')}
          <textarea
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            rows={4}
            className="mt-2 w-full rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-3 text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] focus:ring-4"
          />
        </label>
        <label className="mt-4 block text-sm text-[var(--color-muted)]">
          {t('careers.sourceUrl')}
          <input
            value={sourceUrl}
            onChange={(event) => setSourceUrl(event.target.value)}
            className="mt-2 w-full rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-3 text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] focus:ring-4"
          />
        </label>
        <button
          disabled={saving}
          className="mt-4 w-full rounded-2xl bg-[var(--color-primary)] px-4 py-3 font-bold text-white shadow-lg shadow-[var(--color-primary-soft)] disabled:opacity-60"
        >
          {saving ? t('common.saving') : t('common.save')}
        </button>
      </form>
    </div>
  );
}
