import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getCourses } from '../api/courses.api';
import { Pagination, StateBlock } from '../shared/ui';
import type { Course } from '../types/course';

export function CoursesPage() {
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
      <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <div>
          <h3 className="text-xl font-bold">دوره‌ها</h3>
          <p className="mt-1 text-sm text-[var(--color-muted)]">
            فقط خواندنی — {totalElements} دوره
          </p>
        </div>
        <input
          value={keyword}
          onChange={(event) => setKeyword(event.target.value)}
          placeholder="جستجوی نام دوره..."
          className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-3 text-sm text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] placeholder:text-[var(--color-muted)] focus:ring-4"
        />
      </div>

      {loading ? <StateBlock title="در حال دریافت دوره‌ها..." /> : null}
      {error ? <StateBlock title="خطا در دریافت داده" description={error} /> : null}

      {!loading && !error ? (
        <>
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {courses.map((course) => (
              <Link
                key={course.id}
                to={`/courses/${course.id}`}
                className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm transition hover:-translate-y-0.5 hover:border-[var(--color-primary)] hover:shadow-[var(--shadow-card)]"
              >
                <span className="text-xs font-bold text-[var(--color-primary)]">#{course.id}</span>
                <h4 className="mt-2 font-bold text-[var(--color-text)]">{course.name}</h4>
                {course.lessonUrl ? (
                  <p className="mt-3 truncate text-sm text-[var(--color-primary)]">{course.lessonUrl}</p>
                ) : (
                  <p className="mt-3 text-sm text-[var(--color-muted)]">لینک درس ثبت نشده</p>
                )}
              </Link>
            ))}
          </div>
          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </>
      ) : null}
    </div>
  );
}
