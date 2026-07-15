import type { ReactNode } from 'react';

type PageHeaderProps = {
  title: string;
  description?: string;
  icon: ReactNode;
  action?: ReactNode;
};

export function PageHeader({ title, description, icon, action }: PageHeaderProps) {
  return (
    <section className="relative overflow-hidden rounded-[2rem] border border-[var(--color-border)] bg-[linear-gradient(135deg,color-mix(in_oklab,var(--color-primary)_10%,var(--color-surface)),var(--color-surface))] p-5 shadow-sm sm:p-6">
      <div className="pointer-events-none absolute -end-16 -top-20 h-48 w-48 rounded-full bg-[var(--color-primary-soft)] blur-3xl" />
      <div className="relative flex flex-col gap-5 md:flex-row md:items-center md:justify-between">
        <div className="flex min-w-0 items-center gap-4">
          <div className="grid h-12 w-12 shrink-0 place-items-center rounded-2xl bg-[var(--color-primary)] text-white shadow-lg shadow-[var(--color-primary-soft)]">
            {icon}
          </div>
          <div className="min-w-0">
            <h1 className="text-xl font-black text-[var(--color-text)] sm:text-2xl">{title}</h1>
            {description ? <p className="mt-1.5 text-sm leading-7 text-[var(--color-muted)]">{description}</p> : null}
          </div>
        </div>
        {action ? <div className="shrink-0 md:max-w-sm">{action}</div> : null}
      </div>
    </section>
  );
}
