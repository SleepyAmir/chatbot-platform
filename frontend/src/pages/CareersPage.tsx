import { FormEvent, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { createCareer, getCareers } from '../api/careers.api';
import { StateBlock } from '../shared/ui';
import type { Career } from '../types/career';

export function CareersPage() {
  const [careers, setCareers] = useState<Career[]>([]);
  const [keyword, setKeyword] = useState('');
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadCareers = () => {
    setLoading(true);
    getCareers(keyword)
      .then((page) => {
        setCareers(page.content);
        setError(null);
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    const timeout = window.setTimeout(loadCareers, 250);
    return () => window.clearTimeout(timeout);
  }, [keyword]);

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    createCareer({ title, description })
      .then(() => {
        setTitle('');
        setDescription('');
        loadCareers();
      })
      .catch((err: Error) => setError(err.message));
  };

  return (
    <div className="grid gap-5 xl:grid-cols-[1fr_360px]">
      <section className="space-y-5">
        <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div>
            <h3 className="text-xl font-bold">مشاغل</h3>
            <p className="mt-1 text-sm text-[var(--color-muted)]">CRUD اولیه ماژول Career & Job Market</p>
          </div>
          <input
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            placeholder="جستجوی عنوان شغل..."
            className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-3 text-sm text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] placeholder:text-[var(--color-muted)] focus:ring-4"
          />
        </div>

        {loading ? <StateBlock title="در حال دریافت مشاغل..." /> : null}
        {error ? <StateBlock title="خطا در دریافت داده" description={error} /> : null}

        {!loading && !error ? (
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
        ) : null}
      </section>

      <form onSubmit={handleSubmit} className="h-fit rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-[var(--shadow-card)]">
        <h4 className="font-bold">افزودن شغل جدید</h4>
        <label className="mt-4 block text-sm text-[var(--color-muted)]">
          عنوان
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
            rows={5}
            className="mt-2 w-full rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-3 text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] focus:ring-4"
          />
        </label>
        <button className="mt-4 w-full rounded-2xl bg-[var(--color-primary)] px-4 py-3 font-bold text-white shadow-lg shadow-[var(--color-primary-soft)]">
          ذخیره شغل
        </button>
      </form>
    </div>
  );
}
