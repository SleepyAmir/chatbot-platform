import { ArrowLeft, Sparkles } from 'lucide-react';
import { departmentTags, heroCards } from '../data/landingContent';

export function HeroShowcase() {
  return (
    <section className="grid items-center gap-8 py-6 lg:grid-cols-[1.25fr_0.85fr] lg:gap-10 lg:py-14">
      <div className="relative order-2 min-h-[520px] sm:min-h-[430px] lg:order-1 lg:min-h-[390px]">
        <div className="absolute inset-x-4 top-8 h-80 rounded-[2rem] bg-[var(--color-surface-strong)] shadow-[var(--shadow-card)] sm:inset-x-8 sm:h-72 sm:rounded-[2.2rem]" />
        <div className="absolute inset-x-2 top-4 h-80 rotate-[-2deg] rounded-[2rem] bg-[color-mix(in_oklab,var(--color-text)_16%,var(--color-surface))] shadow-[var(--shadow-card)] sm:inset-x-4 sm:h-72 sm:rotate-[-3deg] sm:rounded-[2.2rem]" />
        <article className="relative rounded-[2rem] border border-[var(--color-border)] bg-[var(--color-surface)] p-3 shadow-[var(--shadow-card)] sm:rounded-[2.4rem] sm:p-4">
          <div className="grid min-h-[460px] overflow-hidden rounded-[1.5rem] bg-[var(--color-page)] sm:min-h-80 sm:rounded-[1.8rem] md:grid-cols-[0.9fr_1.1fr]">
            <div className="relative flex items-center justify-center overflow-hidden bg-[color-mix(in_oklab,var(--color-accent)_18%,var(--color-surface))] p-5">
              <img
                src={heroCards[0].image}
                alt={heroCards[0].title}
                className="h-44 w-44 rounded-full object-cover ring-8 ring-white/40 sm:h-56 sm:w-56"
              />
              <span className="absolute bottom-6 right-6 rounded-full bg-[var(--color-surface)] px-3 py-1 text-xs font-bold text-[var(--color-accent)] shadow">
                آنلاین
              </span>
            </div>
            <div className="flex flex-col justify-center p-5 sm:p-7">
              <span className="mb-4 w-fit rounded-full bg-[color-mix(in_oklab,var(--color-primary)_14%,transparent)] px-4 py-2 text-xs font-bold text-[var(--color-primary)]">
                دپارتمان منتخب
              </span>
              <h2 className="text-xl font-black leading-9 sm:text-2xl sm:leading-10">{heroCards[0].title}</h2>
              <p className="mt-3 text-sm leading-7 text-[var(--color-muted)]">{heroCards[0].subtitle}</p>
              <div className="mt-6 grid grid-cols-1 gap-2 text-xs font-semibold text-[var(--color-muted)] sm:grid-cols-2">
                {departmentTags.slice(0, 6).map((tag) => (
                  <span key={tag} className="rounded-full bg-[var(--color-surface)] px-3 py-2">
                    {tag}
                  </span>
                ))}
              </div>
            </div>
          </div>
          <button className="absolute left-4 top-1/2 hidden h-12 w-12 -translate-y-1/2 place-items-center rounded-full bg-[var(--color-surface)] text-[var(--color-text)] shadow-lg sm:grid">
            <ArrowLeft size={20} />
          </button>
        </article>
        <div className="mt-5 flex justify-center gap-2">
          {heroCards.map((card, index) => (
            <span
              key={card.title}
              className={`h-2 rounded-full transition-all ${index === 0 ? 'w-8 bg-[var(--color-primary)]' : 'w-2 bg-[var(--color-border)]'}`}
            />
          ))}
        </div>
      </div>

      <div className="order-1 space-y-5 lg:order-2 lg:space-y-6">
        <span className="inline-flex items-center gap-2 rounded-full border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-2 text-sm font-bold text-[var(--color-primary)]">
          <Sparkles size={17} />
          پلتفرم آموزشی هوشمند
        </span>
        <div>
          <h1 className="max-w-xl text-3xl font-black leading-[1.55] sm:text-4xl md:text-5xl">
            مسیر آموزش تا بازار کار را هوشمند انتخاب کن
          </h1>
          <p className="mt-4 max-w-xl text-sm leading-8 text-[var(--color-muted)] sm:mt-5 sm:text-base sm:leading-9">
            این نسخه، پایه فرانت برای سامانه چت‌بات آموزشی است؛ با تمرکز روی دوره‌ها،
            مسیرهای شغلی، OCR مدارک و تجربه کاربری مدرن برای توسعه تیمی.
          </p>
        </div>
        <div className="flex flex-wrap gap-3">
          <a
            href="/careers"
            className="rounded-full bg-[var(--color-primary)] px-6 py-3 text-sm font-black text-white shadow-lg shadow-[var(--color-primary-soft)] transition hover:-translate-y-0.5"
          >
            مشاهده مسیرهای شغلی
          </a>
          <a
            href="/chat"
            className="rounded-full border border-[var(--color-border)] bg-[var(--color-surface)] px-6 py-3 text-sm font-black text-[var(--color-text)] transition hover:-translate-y-0.5"
          >
            شروع گفت‌وگو
          </a>
        </div>
        <div className="flex flex-wrap gap-2">
          {departmentTags.map((tag) => (
            <span
              key={tag}
              className="rounded-full border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-2 text-xs font-bold text-[var(--color-muted)]"
            >
              {tag}
            </span>
          ))}
        </div>
      </div>
    </section>
  );
}
