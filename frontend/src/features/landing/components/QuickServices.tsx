import { quickServices } from '../data/landingContent';

export function QuickServices() {
  return (
    <section className="grid gap-4 py-8 sm:grid-cols-2 lg:grid-cols-4">
      {quickServices.map((service) => {
        const Icon = service.icon;
        return (
          <article
            key={service.title}
            className="group rounded-[2rem] border border-[var(--color-border)] bg-[var(--color-surface)] p-6 text-center shadow-[var(--shadow-card)] transition hover:-translate-y-1"
          >
            <div
              className="mx-auto grid h-16 w-16 place-items-center rounded-3xl text-white shadow-lg transition group-hover:scale-105"
              style={{ backgroundColor: service.tone }}
            >
              <Icon size={30} />
            </div>
            <h3 className="mt-5 text-lg font-black">{service.title}</h3>
            <p className="mt-3 text-sm leading-7 text-[var(--color-muted)]">{service.description}</p>
          </article>
        );
      })}
    </section>
  );
}
