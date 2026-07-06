import { departments } from '../data/landingContent';

export function DepartmentsSection() {
  return (
    <section className="py-10">
      <div className="mb-8 flex items-end justify-between gap-4">
        <div>
          <span className="text-sm font-black text-[var(--color-primary)]">دپارتمان‌ها</span>
          <h2 className="mt-2 text-2xl font-black md:text-3xl">شاخه‌های اصلی آموزش</h2>
        </div>
        <a href="/courses" className="hidden rounded-full border border-[var(--color-border)] px-5 py-3 text-sm font-bold md:inline-flex">
          مشاهده همه
        </a>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {departments.map((department) => {
          const Icon = department.icon;
          return (
            <article
              key={department.title}
              className="flex gap-4 rounded-[2rem] border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm transition hover:-translate-y-1 hover:shadow-[var(--shadow-card)]"
            >
              <div className="grid h-14 w-14 shrink-0 place-items-center rounded-2xl bg-[color-mix(in_oklab,var(--color-primary)_12%,transparent)] text-[var(--color-primary)]">
                <Icon size={26} />
              </div>
              <div>
                <h3 className="font-black">{department.title}</h3>
                <p className="mt-2 text-sm leading-7 text-[var(--color-muted)]">{department.description}</p>
              </div>
            </article>
          );
        })}
      </div>
    </section>
  );
}
