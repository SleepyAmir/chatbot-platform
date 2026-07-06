import { FormEvent, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { createCareer, getCareers } from '../api/careers.api';
import { Pagination, StateBlock } from '../shared/ui';
import type { Career } from '../types/career';

export function CareersPage() {
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
        <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div>
            <h3 className="text-xl font-bold">مسیرهای شغلی</h3>
            <p className="mt-1 text-sm text-[var(--color-muted)]">CRUD کامل ماژول Career & Job Market</p>
          </div>
          <input
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            placeholder="جستجوی عنوان شغل..."
            className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-3 text-sm text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] placeholder:text-[var(--color-muted)] focus:ring-4"
          />
        </div>

        {loading ? <StateBlock title="در حال دریافت مشاغل..." /> : null}
        {error ? <StateBlock title="خطا" description={error} /> : null}

        {!loading && !error ? (
          <>
            <div className="grid gap-4 md:grid-cols-2">
              {careers.map((career) => (
                <Link
                  key={career.id}
                  to={`/careers/${career.id}`}
                  className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm transition hover:-translate-y-0.5 hover:border-[var(--color-primary)] hover:shadow-[var(--shadow-card)]"
                >
                  <span className="text-xs font-bold text-[var(--color-warning)]">#{career.id}</span>
                  <h4 className="mt-2 font-bold text-[var(--color-text)]">{career.title}</h4>
                  <p className="mt-3 line-clamp-3 text-sm leading-7 text-[var(--color-muted)]">
                    {career.description || 'توضیحی برای این شغل ثبت نشده است.'}
                  </p>
                </Link>
              ))}
            </div>
            <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        ) : null}
      </section>

      <form onSubmit={handleSubmit} className="h-fit rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-[var(--shadow-card)]">
        <h4 className="font-bold">افزودن مسیر شغلی</h4>
        <label className="mt-4 block text-sm text-[var(--color-muted)]">
          عنوان *
          <input
            required
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            className="mt-2 w-full rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-3 text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] focus:ring-4"
          />
        </label>
        <label className="mt-4 block text-sm text-[var(--color-muted)]">
          توضیحات
          <textarea
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            rows={4}
            className="mt-2 w-full rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-3 text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] focus:ring-4"
          />
        </label>
        <label className="mt-4 block text-sm text-[var(--color-muted)]">
          لینک منبع
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
          {saving ? 'در حال ذخیره...' : 'ذخیره'}
        </button>
      </form>
    </div>
  );
}
