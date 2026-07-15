import { useEffect, useState } from 'react';
import { ArrowLeft, BookOpen, BriefcaseBusiness, CircleHelp } from 'lucide-react';
import { Link, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { getCourse, getCourseDetails } from '../api/courses.api';
import { getCareersByCourse } from '../api/careers.api';
import { getQaPairsByCourse } from '../api/qa.api';
import { StateBlock } from '../shared/ui';
import type { Course, CourseCareer, CourseDetail } from '../types/course';
import type { QaPair } from '../types/qa';

export function CourseDetailPage() {
  const { t, i18n } = useTranslation();
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
      setError(t('courses.invalidId'));
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
  }, [courseId, t]);

  if (loading) {
    return <StateBlock title={t('courses.loadingDetails')} />;
  }

  if (error || !course) {
    return <StateBlock title={t('courses.detailsError')} description={error ?? t('courses.notFound')} />;
  }

  return (
    <div className="space-y-5">
      <Link to="/courses" className="inline-flex items-center gap-2 rounded-full border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-2 text-sm font-bold text-[var(--color-primary)] shadow-sm transition hover:-translate-y-0.5 hover:border-[var(--color-primary)]">
        <ArrowLeft size={16} />
        {t('courses.back')}
      </Link>

      <section className="relative overflow-hidden rounded-[2.25rem] border border-[var(--color-border)] bg-[linear-gradient(135deg,color-mix(in_oklab,var(--color-primary)_16%,var(--color-surface)),var(--color-surface))] p-6 shadow-[var(--shadow-card)] sm:p-8">
        <div className="pointer-events-none absolute -end-16 -top-20 h-56 w-56 rounded-full bg-[var(--color-primary-soft)] blur-3xl" />
        <div className="relative flex items-start gap-4">
          <div className="grid h-14 w-14 shrink-0 place-items-center rounded-2xl bg-[var(--color-primary)] text-white shadow-lg shadow-[var(--color-primary-soft)]"><BookOpen size={25} /></div>
          <div><span className="text-xs font-bold text-[var(--color-primary)]">#{course.id}</span>
        <h3 className="mt-2 text-2xl font-black sm:text-3xl">{course.name}</h3>
        {course.lessonUrl ? (
          <a href={course.lessonUrl} target="_blank" rel="noreferrer" className="mt-3 inline-block text-sm text-[var(--color-primary)]">
            {course.lessonUrl}
          </a>
        ) : null}</div></div>
      </section>

      {details ? (
        <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {[
            { label: t('courses.price'), value: `${Number(details.price).toLocaleString(i18n.language === 'fa' ? 'fa-IR' : 'en-US')} ${t('courses.currency')}` },
            { label: t('courses.teacher'), value: details.teacher },
            { label: t('courses.duration'), value: details.duration },
            { label: t('courses.branch'), value: details.branch },
          ].map((item) => (
            <article key={item.label} className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm">
              <p className="text-xs text-[var(--color-muted)]">{item.label}</p>
              <p className="mt-2 font-bold">{item.value}</p>
            </article>
          ))}
        </section>
      ) : (
        <StateBlock title={t('courses.noDetails')} />
      )}

      <div className="grid gap-5 lg:grid-cols-2">
        <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm">
          <h4 className="flex items-center gap-2 font-bold"><BriefcaseBusiness size={19} className="text-[var(--color-primary)]" />{t('courses.relatedCareers')}</h4>
          <div className="mt-4 space-y-3">
            {careers.length ? (
              careers.map((item) => (
                <Link
                  key={`${item.courseId}-${item.careerId}`}
                  to={`/careers/${item.careerId}`}
                  className="block rounded-2xl bg-[var(--color-page)] p-4 transition hover:ring-2 hover:ring-[var(--color-primary-soft)]"
                >
                  <p className="font-semibold">{item.careerTitle}</p>
                  <p className="mt-1 text-xs text-[var(--color-muted)]">{t('courses.relevance', { value: (item.relevance * 100).toFixed(0) })}</p>
                </Link>
              ))
            ) : (
              <p className="text-sm text-[var(--color-muted)]">{t('courses.noCareers')}</p>
            )}
          </div>
        </section>

        <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm">
          <div className="flex items-center justify-between">
            <h4 className="flex items-center gap-2 font-bold"><CircleHelp size={19} className="text-[var(--color-primary)]" />{t('courses.courseFaq')}</h4>
            <Link to="/faq" className="text-xs font-bold text-[var(--color-primary)]">
              {t('courses.allFaq')}
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
              <p className="text-sm text-[var(--color-muted)]">{t('courses.noFaq')}</p>
            )}
          </div>
        </section>
      </div>
    </div>
  );
}
