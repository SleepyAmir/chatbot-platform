import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getCareer, getCareerRequirements, getCareerSkills } from '../api/careers.api';
import { StateBlock } from '../shared/ui';
import type { Career, CareerRequirement, CareerSkill } from '../types/career';

export function CareerDetailPage() {
  const { id } = useParams();
  const careerId = Number(id);
  const [career, setCareer] = useState<Career | null>(null);
  const [skills, setSkills] = useState<CareerSkill[]>([]);
  const [requirements, setRequirements] = useState<CareerRequirement[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!careerId) {
      setError('شناسه شغل معتبر نیست');
      setLoading(false);
      return;
    }

    Promise.all([
      getCareer(careerId),
      getCareerSkills(careerId),
      getCareerRequirements(careerId),
    ])
      .then(([careerData, skillData, requirementData]) => {
        setCareer(careerData);
        setSkills(skillData);
        setRequirements(requirementData);
        setError(null);
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, [careerId]);

  if (loading) {
    return <StateBlock title="در حال دریافت جزئیات شغل..." />;
  }

  if (error || !career) {
    return <StateBlock title="خطا در دریافت جزئیات" description={error ?? 'شغل پیدا نشد'} />;
  }

  return (
    <div className="space-y-5">
      <Link to="/careers" className="text-sm font-bold text-[var(--color-primary)]">
        بازگشت به لیست مشاغل
      </Link>

      <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6 shadow-sm">
        <span className="text-xs font-bold text-[var(--color-warning)]">#{career.id}</span>
        <h3 className="mt-2 text-2xl font-bold">{career.title}</h3>
        <p className="mt-4 leading-8 text-[var(--color-muted)]">{career.description || 'توضیحی ثبت نشده است.'}</p>
      </section>

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
              <p className="text-sm text-[var(--color-muted)]">مهارتی ثبت نشده است.</p>
            )}
          </div>
        </section>

        <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm">
          <h4 className="font-bold">نیازمندی‌ها</h4>
          <div className="mt-4 space-y-3">
            {requirements.length ? (
              requirements.map((requirement) => (
                <article key={requirement.id} className="rounded-2xl bg-[var(--color-page)] p-4">
                  <span className="text-xs text-[var(--color-muted)]">Chunk {requirement.chunkIndex}</span>
                  <p className="mt-2 text-sm leading-7 text-[var(--color-text)]">{requirement.requirementText}</p>
                </article>
              ))
            ) : (
              <p className="text-sm text-[var(--color-muted)]">نیازمندی ثبت نشده است.</p>
            )}
          </div>
        </section>
      </div>
    </div>
  );
}
