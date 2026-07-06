import {
  ArrowRight,
  Bot,
  BriefcaseBusiness,
  Compass,
  GraduationCap,
  Home,
  Sparkles,
} from 'lucide-react';
import { Link, useLocation } from 'react-router-dom';

const quickLinks = [
  { to: '/', label: 'خانه', icon: Home, tone: 'var(--color-primary)' },
  { to: '/courses', label: 'دوره‌ها', icon: GraduationCap, tone: 'var(--color-success)' },
  { to: '/careers', label: 'مسیر شغلی', icon: BriefcaseBusiness, tone: 'var(--color-warning)' },
  { to: '/chat', label: 'دستیار', icon: Bot, tone: 'var(--color-accent)' },
];

export function NotFoundPage() {
  const { pathname } = useLocation();

  return (
    <section className="relative flex min-h-[calc(100vh-10rem)] items-center justify-center overflow-hidden py-10">
      <div
        aria-hidden
        className="pointer-events-none absolute -left-24 top-8 h-72 w-72 rounded-full bg-[var(--color-primary-soft)] blur-3xl"
      />
      <div
        aria-hidden
        className="pointer-events-none absolute -bottom-16 -right-16 h-80 w-80 rounded-full bg-[color-mix(in_oklab,var(--color-accent)_22%,transparent)] blur-3xl"
      />
      <div
        aria-hidden
        className="pointer-events-none absolute left-1/2 top-1/2 h-96 w-96 -translate-x-1/2 -translate-y-1/2 rounded-full bg-[color-mix(in_oklab,var(--color-success)_10%,transparent)] blur-3xl"
      />

      <div className="relative w-full max-w-3xl text-center">
        <div className="mx-auto mb-8 grid h-20 w-20 place-items-center rounded-[2rem] border border-[var(--color-border)] bg-[var(--color-surface)] shadow-[var(--shadow-card)]">
          <Compass size={36} className="text-[var(--color-primary)]" strokeWidth={1.75} />
        </div>

        <p
          className="select-none bg-[linear-gradient(135deg,var(--color-primary),var(--color-accent))] bg-clip-text text-[clamp(5.5rem,18vw,9rem)] font-black leading-none tracking-tight text-transparent"
          aria-hidden
        >
          404
        </p>

        <div className="relative -mt-2">
          <span className="inline-flex items-center gap-2 rounded-full border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-2 text-xs font-bold text-[var(--color-primary)] shadow-sm">
            <Sparkles size={14} />
            صفحه‌ای که دنبالش بودی اینجا نیست
          </span>

          <h1 className="mt-6 text-2xl font-black leading-10 sm:text-3xl">
            گم شدی؟ نگران نباش — راه برگشت رو داری
          </h1>
          <p className="mx-auto mt-4 max-w-xl text-sm leading-8 text-[var(--color-muted)]">
            آدرس <code dir="ltr" className="rounded-lg bg-[var(--color-surface-strong)] px-2 py-0.5 text-xs text-[var(--color-text)]">{pathname}</code>{' '}
            در سامانه ثبت نشده. شاید لینک قدیمی باشه یا اشتباه تایپ شده.
          </p>
        </div>

        <div className="mt-8 flex flex-wrap items-center justify-center gap-3">
          <Link
            to="/"
            className="inline-flex items-center gap-2 rounded-2xl bg-[var(--color-primary)] px-6 py-3.5 text-sm font-bold text-white shadow-lg shadow-[var(--color-primary-soft)] transition hover:-translate-y-0.5 hover:opacity-95"
          >
            <Home size={18} />
            بازگشت به خانه
          </Link>
          <Link
            to="/chat"
            className="inline-flex items-center gap-2 rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-6 py-3.5 text-sm font-bold text-[var(--color-text)] shadow-sm transition hover:-translate-y-0.5 hover:border-[var(--color-primary)]"
          >
            <Bot size={18} />
            پرسیدن از دستیار
          </Link>
        </div>

        <div className="mt-12 grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
          {quickLinks.map((link) => {
            const Icon = link.icon;
            return (
              <Link
                key={link.to}
                to={link.to}
                className="group flex items-center justify-between rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-4 text-right shadow-sm transition hover:-translate-y-1 hover:border-[var(--color-primary)] hover:shadow-[var(--shadow-card)]"
              >
                <span className="text-sm font-bold text-[var(--color-text)]">{link.label}</span>
                <span
                  className="grid h-10 w-10 place-items-center rounded-2xl text-white transition group-hover:scale-105"
                  style={{ backgroundColor: link.tone }}
                >
                  <Icon size={18} />
                </span>
              </Link>
            );
          })}
        </div>

        <p className="mt-10 inline-flex items-center gap-2 text-xs text-[var(--color-muted)]">
          <ArrowRight size={14} className="rotate-180" />
          MFTPlus — مسیر درست همیشه از منوی بالا هم در دسترسه
        </p>
      </div>
    </section>
  );
}
