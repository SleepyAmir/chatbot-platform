import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getCourse, getCourseDetails } from '../api/courses.api';
import { getCareersByCourse } from '../api/careers.api';
import { getQaPairsByCourse } from '../api/qa.api';
import { StateBlock } from '../shared/ui';
import type { Course, CourseCareer, CourseDetail } from '../types/course';
import type { QaPair } from '../types/qa';

export function CourseDetailPage() {
  const { id } = useParams();
  const courseId = Number(id);
  const [course, setCourse] = useState<Course | null>(null);
  const [details, setDetails] = useState<CourseDetail | null>(null);
  const [careers, setCareers] = useState<CourseCareer[]>([]);
  const [qaPairs, setQaPairs] = useState<QaPair[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!courseId) {
      setError('شناسه دوره معتبر نیست');
      setLoading(false);
      return;
    }

    Promise.all([
      getCourse(courseId),
      getCourseDetails(courseId).catch(() => null),
      getCareersByCourse(courseId).catch(() => []),
      getQaPairsByCourse(courseId).catch(() => []),
    ])
      .then(([courseData, detailsData, careersData, qaData]) => {
        setCourse(courseData);
        setDetails(detailsData);
        setCareers(careersData);
        setQaPairs(qaData);
        setError(null);
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [courseId]);

  if (loading) {
    return <StateBlock title="در حال دریافت جزئیات دوره..." />;
  }

  if (error || !course) {
    return <StateBlock title="خطا در دریافت جزئیات" description={error ?? 'دوره پیدا نشد'} />;
  }

  return (
    <div className="space-y-5">
      <Link to="/courses" className="text-sm font-bold text-[var(--color-primary)]">
        بازگشت به لیست دوره‌ها
      </Link>

      <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6 shadow-sm">
        <span className="text-xs font-bold text-[var(--color-primary)]">#{course.id}</span>
        <h3 className="mt-2 text-2xl font-bold">{course.name}</h3>
        {course.lessonUrl ? (
          <a href={course.lessonUrl} target="_blank" rel="noreferrer" className="mt-3 inline-block text-sm text-[var(--color-primary)]">
            {course.lessonUrl}
          </a>
        ) : null}
      </section>

      {details ? (
        <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {[
            { label: 'قیمت', value: `${Number(details.price).toLocaleString('fa-IR')} تومان` },
            { label: 'استاد', value: details.teacher },
            { label: 'مدت', value: details.duration },
            { label: 'شاخه', value: details.branch },
          ].map((item) => (
            <article key={item.label} className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm">
              <p className="text-xs text-[var(--color-muted)]">{item.label}</p>
              <p className="mt-2 font-bold">{item.value}</p>
            </article>
          ))}
        </section>
      ) : (
        <StateBlock title="جزئیات قیمت/استاد ثبت نشده" />
      )}

      <div className="grid gap-5 lg:grid-cols-2">
        <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm">
          <h4 className="font-bold">مسیرهای شغلی مرتبط</h4>
          <div className="mt-4 space-y-3">
            {careers.length ? (
              careers.map((item) => (
                <Link
                  key={`${item.courseId}-${item.careerId}`}
                  to={`/careers/${item.careerId}`}
                  className="block rounded-2xl bg-[var(--color-page)] p-4 transition hover:ring-2 hover:ring-[var(--color-primary-soft)]"
                >
                  <p className="font-semibold">{item.careerTitle}</p>
                  <p className="mt-1 text-xs text-[var(--color-muted)]">ارتباط: {(item.relevance * 100).toFixed(0)}%</p>
                </Link>
              ))
            ) : (
              <p className="text-sm text-[var(--color-muted)]">مسیر شغلی مرتبطی ثبت نشده.</p>
            )}
          </div>
        </section>

        <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm">
          <div className="flex items-center justify-between">
            <h4 className="font-bold">سوالات متداول این دوره</h4>
            <Link to="/faq" className="text-xs font-bold text-[var(--color-primary)]">
              همه FAQ
            </Link>
          </div>
          <div className="mt-4 space-y-3">
            {qaPairs.length ? (
              qaPairs.slice(0, 5).map((qa) => (
                <article key={qa.id} className="rounded-2xl bg-[var(--color-page)] p-4">
                  <p className="text-sm font-semibold">{qa.question}</p>
                  <p className="mt-2 text-sm leading-7 text-[var(--color-muted)]">{qa.answer}</p>
                </article>
              ))
            ) : (
              <p className="text-sm text-[var(--color-muted)]">سوالی برای این دوره ثبت نشده.</p>
            )}
          </div>
        </section>
      </div>
    </div>
  );
}
