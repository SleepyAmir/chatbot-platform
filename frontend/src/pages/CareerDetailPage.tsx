import { FormEvent, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import {
  addCareerRequirement,
  addCareerSkill,
  deleteCareer,
  getCareer,
  getCareerCourses,
  getCareerRequirements,
  getCareerSkills,
  linkCourseCareer,
  updateCareer,
} from '../api/careers.api';
import { getAllCourses } from '../api/courses.api';
import { StateBlock } from '../shared/ui';
import type { Career, CareerRequirement, CareerSkill } from '../types/career';
import type { Course, CourseCareer } from '../types/course';

export function CareerDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const careerId = Number(id);

  const [career, setCareer] = useState<Career | null>(null);
  const [skills, setSkills] = useState<CareerSkill[]>([]);
  const [requirements, setRequirements] = useState<CareerRequirement[]>([]);
  const [courses, setCourses] = useState<CourseCareer[]>([]);
  const [allCourses, setAllCourses] = useState<Course[]>([]);

  const [editTitle, setEditTitle] = useState('');
  const [editDescription, setEditDescription] = useState('');
  const [editSourceUrl, setEditSourceUrl] = useState('');
  const [newSkill, setNewSkill] = useState('');
  const [reqChunkIndex, setReqChunkIndex] = useState(0);
  const [reqText, setReqText] = useState('');
  const [linkCourseId, setLinkCourseId] = useState('');
  const [linkRelevance, setLinkRelevance] = useState('0.8');

  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  const reload = () => {
    if (!careerId) return Promise.resolve();

    return Promise.all([
      getCareer(careerId),
      getCareerSkills(careerId),
      getCareerRequirements(careerId),
      getCareerCourses(careerId),
    ]).then(([careerData, skillData, requirementData, courseData]) => {
      setCareer(careerData);
      setEditTitle(careerData.title);
      setEditDescription(careerData.description ?? '');
      setEditSourceUrl(careerData.sourceUrl ?? '');
      setSkills(skillData);
      setRequirements(requirementData);
      setCourses(courseData);
    });
  };

  useEffect(() => {
    if (!careerId) {
      setError('شناسه شغل معتبر نیست');
      setLoading(false);
      return;
    }

    Promise.all([reload(), getAllCourses().catch(() => [])])
      .then(([, courseList]) => {
        setAllCourses(courseList as Course[]);
        setError(null);
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [careerId]);

  const handleUpdate = (event: FormEvent) => {
    event.preventDefault();
    if (!careerId) return;
    setActionLoading(true);
    updateCareer(careerId, {
      title: editTitle,
      description: editDescription || undefined,
      sourceUrl: editSourceUrl || undefined,
    })
      .then(() => reload())
      .catch((err: Error) => setError(err.message))
      .finally(() => setActionLoading(false));
  };

  const handleDelete = () => {
    if (!careerId || !window.confirm('این مسیر شغلی حذف شود؟')) return;
    setActionLoading(true);
    deleteCareer(careerId)
      .then(() => navigate('/careers'))
      .catch((err: Error) => setError(err.message))
      .finally(() => setActionLoading(false));
  };

  const handleAddSkill = (event: FormEvent) => {
    event.preventDefault();
    if (!careerId || !newSkill.trim()) return;
    setActionLoading(true);
    addCareerSkill(careerId, newSkill.trim())
      .then(() => {
        setNewSkill('');
        return reload();
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setActionLoading(false));
  };

  const handleAddRequirement = (event: FormEvent) => {
    event.preventDefault();
    if (!careerId || !reqText.trim()) return;
    setActionLoading(true);
    addCareerRequirement(careerId, { chunkIndex: reqChunkIndex, requirementText: reqText.trim() })
      .then(() => {
        setReqText('');
        return reload();
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setActionLoading(false));
  };

  const handleLinkCourse = (event: FormEvent) => {
    event.preventDefault();
    const courseId = Number(linkCourseId);
    const relevance = Number(linkRelevance);
    if (!courseId || relevance < 0 || relevance > 1) return;
    setActionLoading(true);
    linkCourseCareer(courseId, { careerId, relevance })
      .then(() => {
        setLinkCourseId('');
        return reload();
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setActionLoading(false));
  };

  if (loading) {
    return <StateBlock title="در حال دریافت جزئیات شغل..." />;
  }

  if (error && !career) {
    return <StateBlock title="خطا در دریافت جزئیات" description={error} />;
  }

  if (!career) {
    return <StateBlock title="شغل پیدا نشد" />;
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <Link to="/careers" className="text-sm font-bold text-[var(--color-primary)]">
          بازگشت به لیست مشاغل
        </Link>
        <button
          type="button"
          onClick={handleDelete}
          disabled={actionLoading}
          className="rounded-2xl border border-red-300 px-4 py-2 text-sm font-semibold text-red-600 transition hover:bg-red-50 disabled:opacity-50"
        >
          حذف مسیر شغلی
        </button>
      </div>

      {error ? <StateBlock title="خطا" description={error} /> : null}

      <form onSubmit={handleUpdate} className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6 shadow-sm">
        <span className="text-xs font-bold text-[var(--color-warning)]">#{career.id}</span>
        <label className="mt-3 block text-sm text-[var(--color-muted)]">
          عنوان
          <input
            required
            value={editTitle}
            onChange={(e) => setEditTitle(e.target.value)}
            className="mt-2 w-full rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-3 font-bold text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] focus:ring-4"
          />
        </label>
        <label className="mt-4 block text-sm text-[var(--color-muted)]">
          توضیحات
          <textarea
            value={editDescription}
            onChange={(e) => setEditDescription(e.target.value)}
            rows={4}
            className="mt-2 w-full rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-3 text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] focus:ring-4"
          />
        </label>
        <label className="mt-4 block text-sm text-[var(--color-muted)]">
          لینک منبع
          <input
            value={editSourceUrl}
            onChange={(e) => setEditSourceUrl(e.target.value)}
            className="mt-2 w-full rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-3 text-[var(--color-text)] outline-none ring-[var(--color-primary-soft)] focus:ring-4"
          />
        </label>
        <button
          type="submit"
          disabled={actionLoading}
          className="mt-4 rounded-2xl bg-[var(--color-primary)] px-5 py-2.5 text-sm font-bold text-white disabled:opacity-60"
        >
          ذخیره تغییرات
        </button>
      </form>

      <div className="grid gap-5 lg:grid-cols-2">
        <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm">
          <h4 className="font-bold">مهارت‌ها</h4>
          <div className="mt-4 flex flex-wrap gap-2">
            {skills.length ? (
              skills.map((skill) => (
                <span key={skill.id} className="rounded-full bg-[color-mix(in_oklab,var(--color-primary)_14%,transparent)] px-3 py-2 text-sm font-semibold text-[var(--color-primary)]">
                  {skill.skillName}
                </span>
              ))
            ) : (
              <p className="text-sm text-[var(--color-muted)]">مهارتی ثبت نشده.</p>
            )}
          </div>
          <form onSubmit={handleAddSkill} className="mt-4 flex gap-2">
            <input
              value={newSkill}
              onChange={(e) => setNewSkill(e.target.value)}
              placeholder="نام مهارت"
              className="flex-1 rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-2.5 text-sm outline-none focus:ring-4 focus:ring-[var(--color-primary-soft)]"
            />
            <button type="submit" disabled={actionLoading} className="rounded-2xl bg-[var(--color-primary)] px-4 py-2.5 text-sm font-bold text-white disabled:opacity-60">
              افزودن
            </button>
          </form>
        </section>

        <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm">
          <h4 className="font-bold">نیازمندی‌ها</h4>
          <div className="mt-4 max-h-64 space-y-3 overflow-y-auto">
            {requirements.length ? (
              requirements.map((requirement) => (
                <article key={requirement.id} className="rounded-2xl bg-[var(--color-page)] p-4">
                  <div className="flex items-center justify-between text-xs text-[var(--color-muted)]">
                    <span>Chunk {requirement.chunkIndex}</span>
                    {requirement.hasEmbedding ? <span>دارای embedding</span> : null}
                  </div>
                  <p className="mt-2 text-sm leading-7">{requirement.requirementText}</p>
                </article>
              ))
            ) : (
              <p className="text-sm text-[var(--color-muted)]">نیازمندی ثبت نشده.</p>
            )}
          </div>
          <form onSubmit={handleAddRequirement} className="mt-4 space-y-3">
            <input
              type="number"
              min={0}
              value={reqChunkIndex}
              onChange={(e) => setReqChunkIndex(Number(e.target.value))}
              placeholder="Chunk index"
              className="w-full rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-2.5 text-sm outline-none focus:ring-4 focus:ring-[var(--color-primary-soft)]"
            />
            <textarea
              value={reqText}
              onChange={(e) => setReqText(e.target.value)}
              rows={3}
              placeholder="متن نیازمندی"
              className="w-full rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-2.5 text-sm outline-none focus:ring-4 focus:ring-[var(--color-primary-soft)]"
            />
            <button type="submit" disabled={actionLoading} className="rounded-2xl bg-[var(--color-primary)] px-4 py-2.5 text-sm font-bold text-white disabled:opacity-60">
              افزودن نیازمندی
            </button>
          </form>
        </section>
      </div>

      <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm">
        <h4 className="font-bold">دوره‌های مرتبط</h4>
        <div className="mt-4 grid gap-3 sm:grid-cols-2">
          {courses.length ? (
            courses.map((item) => (
              <Link
                key={`${item.courseId}-${item.careerId}`}
                to={`/courses/${item.courseId}`}
                className="rounded-2xl bg-[var(--color-page)] p-4 transition hover:ring-2 hover:ring-[var(--color-primary-soft)]"
              >
                <p className="font-semibold">{item.courseName}</p>
                <p className="mt-1 text-xs text-[var(--color-muted)]">ارتباط: {(item.relevance * 100).toFixed(0)}%</p>
              </Link>
            ))
          ) : (
            <p className="text-sm text-[var(--color-muted)]">دوره‌ای لینک نشده.</p>
          )}
        </div>

        <form onSubmit={handleLinkCourse} className="mt-5 grid gap-3 sm:grid-cols-[1fr_120px_auto]">
          <select
            value={linkCourseId}
            onChange={(e) => setLinkCourseId(e.target.value)}
            required
            className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-2.5 text-sm outline-none focus:ring-4 focus:ring-[var(--color-primary-soft)]"
          >
            <option value="">انتخاب دوره</option>
            {allCourses.map((course) => (
              <option key={course.id} value={course.id}>
                {course.name}
              </option>
            ))}
          </select>
          <input
            type="number"
            min={0}
            max={1}
            step={0.05}
            value={linkRelevance}
            onChange={(e) => setLinkRelevance(e.target.value)}
            title="relevance (0 تا 1)"
            className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-page)] px-4 py-2.5 text-sm outline-none focus:ring-4 focus:ring-[var(--color-primary-soft)]"
          />
          <button type="submit" disabled={actionLoading} className="rounded-2xl bg-[var(--color-primary)] px-4 py-2.5 text-sm font-bold text-white disabled:opacity-60">
            لینک دوره
          </button>
        </form>
      </section>
    </div>
  );
}
