import { useEffect, useState } from 'react';
import { ArrowUpRight, GraduationCap, Search } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { getCourses } from '../api/courses.api';
import { PageHeader, Pagination, StateBlock } from '../shared/ui';
import type { Course } from '../types/course';

export function CoursesPage() {
  const { t } = useTranslation();
  const [courses, setCourses] = useState<Course[]>([]);
  const [keyword, setKeyword] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setPage(0);
  }, [keyword]);

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      setLoading(true);
      getCourses({ keyword, page, size: 12 })
        .then((result) => {
          setCourses(result.content);
          setTotalPages(result.totalPages);
          setTotalElements(result.totalElements);
          setError(null);
        })
        .catch((err: Error) => setError(err.message))
        .finally(() => setLoading(false));
    }, 250);

    return () => window.clearTimeout(timeout);
  }, [keyword, page]);

  return (
    <div className="space-y-5">
      <PageHeader
        title={t('courses.title')}
        description={t('courses.readOnlyCount', { count: totalElements })}
        icon={<GraduationCap size={23} />}
        action={(
          <label className="flex items-center gap-3 rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-4 shadow-sm transition focus-within:border-[var(--color-primary)] focus-within:ring-4 focus-within:ring-[var(--color-primary-soft)]">
            <Search size={18} className="shrink-0 text-[var(--color-muted)]" />
            <input
              value={keyword}
              onChange={(event) => setKeyword(event.target.value)}
              placeholder={t('courses.search')}
              className="min-w-0 flex-1 bg-transparent py-3 text-sm text-[var(--color-text)] outline-none placeholder:text-[var(--color-muted)]"
            />
          </label>
        )}
      />

      {loading ? <StateBlock title={t('courses.loading')} /> : null}
      {error ? <StateBlock title={t('courses.loadError')} description={error} /> : null}

      {!loading && !error ? (
        <>
          {courses.length ? <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {courses.map((course) => (
              <Link
                key={course.id}
                to={`/courses/${course.id}`}
                className="group relative overflow-hidden rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm transition duration-300 hover:-translate-y-1 hover:border-[var(--color-primary)] hover:shadow-[var(--shadow-card)]"
              >
                <div className="absolute inset-x-0 top-0 h-1 bg-[linear-gradient(90deg,var(--color-primary),var(--color-accent))] opacity-0 transition group-hover:opacity-100" />
                <div className="flex items-start justify-between gap-4">
                  <div className="grid h-11 w-11 shrink-0 place-items-center rounded-2xl bg-[var(--color-surface-strong)] text-[var(--color-primary)] transition group-hover:bg-[var(--color-primary)] group-hover:text-white">
                    <GraduationCap size={20} />
                  </div>
                  <span className="text-xs font-bold text-[var(--color-primary)]">#{course.id}</span>
                </div>
                <h4 className="mt-4 font-bold leading-7 text-[var(--color-text)]">{course.name}</h4>
                {course.lessonUrl ? (
                  <p className="mt-3 flex items-center gap-1.5 truncate text-sm text-[var(--color-primary)]"><ArrowUpRight size={15} />{course.lessonUrl}</p>
                ) : (
                  <p className="mt-3 text-sm text-[var(--color-muted)]">{t('courses.noLessonLink')}</p>
                )}
              </Link>
            ))}
          </div> : <StateBlock title={t('empty.courses')} />}
          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </>
      ) : null}
    </div>
  );
}
