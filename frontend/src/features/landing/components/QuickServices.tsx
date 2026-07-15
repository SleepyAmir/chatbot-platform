import { quickServices } from '../data/landingContent';
import { useTranslation } from 'react-i18next';

export function QuickServices() {
  const { t } = useTranslation();
  const content = t('landing.services', { returnObjects: true }) as Array<{ title: string; description: string }>;
  return (
    <section className="grid gap-4 py-8 sm:grid-cols-2 lg:grid-cols-4">
      {quickServices.map((service, index) => {
        const Icon = service.icon;
        const text = content[index];
        return (
          <article
            key={text.title}
            className="group rounded-[2rem] border border-[var(--color-border)] bg-[var(--color-surface)] p-6 text-center shadow-[var(--shadow-card)] transition hover:-translate-y-1"
          >
            <div
              className="mx-auto grid h-16 w-16 place-items-center rounded-3xl text-white shadow-lg transition group-hover:scale-105"
              style={{ backgroundColor: service.tone }}
            >
              <Icon size={30} />
            </div>
            <h3 className="mt-5 text-lg font-black">{text.title}</h3>
            <p className="mt-3 text-sm leading-7 text-[var(--color-muted)]">{text.description}</p>
          </article>
        );
      })}
    </section>
  );
}
