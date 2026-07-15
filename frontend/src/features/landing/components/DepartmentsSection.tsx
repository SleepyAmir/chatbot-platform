import { departments } from '../data/landingContent';
import { useTranslation } from 'react-i18next';

export function DepartmentsSection() {
  const { t } = useTranslation();
  const content = t('landing.departmentCards', { returnObjects: true }) as Array<{ title: string; description: string }>;
  return (
    <section className="py-10">
      <div className="mb-8 flex items-end justify-between gap-4">
        <div>
          <span className="text-sm font-black text-[var(--color-primary)]">{t('landing.departments')}</span>
          <h2 className="mt-2 text-2xl font-black md:text-3xl">{t('landing.mainBranches')}</h2>
        </div>
        <a href="/courses" className="hidden rounded-full border border-[var(--color-border)] px-5 py-3 text-sm font-bold md:inline-flex">
          {t('landing.viewAll')}
        </a>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {departments.map((department, index) => {
          const Icon = department.icon;
          const text = content[index];
          return (
            <article
              key={text.title}
              className="flex gap-4 rounded-[2rem] border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm transition hover:-translate-y-1 hover:shadow-[var(--shadow-card)]"
            >
              <div className="grid h-14 w-14 shrink-0 place-items-center rounded-2xl bg-[color-mix(in_oklab,var(--color-primary)_12%,transparent)] text-[var(--color-primary)]">
                <Icon size={26} />
              </div>
              <div>
                <h3 className="font-black">{text.title}</h3>
                <p className="mt-2 text-sm leading-7 text-[var(--color-muted)]">{text.description}</p>
              </div>
            </article>
          );
        })}
      </div>
    </section>
  );
}
