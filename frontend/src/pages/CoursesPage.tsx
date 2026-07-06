import { useEffect, useState } from 'react';
import { getCourses } from '../api/courses.api';
import { StateBlock } from '../shared/ui';
import type { Course } from '../types/course';

export function CoursesPage() {
  const [courses, setCourses] = useState<Course[]>([]);
  const [keyword, setKeyword] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      setLoading(true);
      getCourses(keyword)
        .then((page) => {
          setCourses(page.content);
          setError(null);
        })
        .catch((err: Error) => setError(err.message))
        .finally(() => setLoading(false));
    }, 250);

    return () => window.clearTimeout(timeout);
  }, [keyword]);

  return (
    <div className="space-y-5">
      <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <div>
          <h3 className="text-xl font-bold">دوره‌ها</h3>
          <p className="mt-1 text-sm text-[var(--color-muted)]">مصرف مستقیم API دوره‌های Spring Boot</p>
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
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {courses.map((course) => (
            <article key={course.id} className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm">
              <span className="text-xs font-bold text-[var(--color-primary)]">#{course.id}</span>
              <h4 className="mt-2 font-bold text-[var(--color-text)]">{course.name}</h4>
              {course.lessonUrl ? (
                <a className="mt-3 block truncate text-sm text-[var(--color-primary)]" href={course.lessonUrl}>
                  {course.lessonUrl}
                </a>
              ) : (
                <p className="mt-3 text-sm text-[var(--color-muted)]">لینک درس ثبت نشده</p>
              )}
            </article>
          ))}
        </div>
      ) : null}
    </div>
  );
}
