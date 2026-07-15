import { ArrowLeft, ChevronLeft, ChevronRight, Sparkles } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { heroCards } from '../data/landingContent';

const slideDuration = 5_000;

export function HeroShowcase() {
  const { t } = useTranslation();
  const [activeSlide, setActiveSlide] = useState(0);
  const [isPaused, setIsPaused] = useState(false);
  const remainingTime = useRef(slideDuration);
  const timerStartedAt = useRef(Date.now());
  const departmentTags = t('landing.tags', { returnObjects: true }) as string[];
  const heroContent = t('landing.heroes', { returnObjects: true }) as Array<{ title: string; subtitle: string }>;

  useEffect(() => {
    if (isPaused) return;

    timerStartedAt.current = Date.now();
    const timer = window.setTimeout(() => {
      remainingTime.current = slideDuration;
      setActiveSlide((current) => (current + 1) % heroCards.length);
    }, remainingTime.current);

    return () => window.clearTimeout(timer);
  }, [activeSlide, isPaused]);

  const showPrevious = () => {
    remainingTime.current = slideDuration;
    setActiveSlide((current) => (current - 1 + heroCards.length) % heroCards.length);
  };

  const showNext = () => {
    remainingTime.current = slideDuration;
    setActiveSlide((current) => (current + 1) % heroCards.length);
  };

  const pauseSlider = () => {
    remainingTime.current = Math.max(0, remainingTime.current - (Date.now() - timerStartedAt.current));
    setIsPaused(true);
  };

  return (
    <section className="grid items-center gap-8 py-4 lg:grid-cols-[1.08fr_0.92fr] lg:gap-12 lg:py-12">
      <div
        className="group relative order-2 overflow-hidden rounded-[2.5rem] border border-white/20 bg-slate-950 shadow-[0_32px_90px_rgb(15_23_42/24%)] lg:order-1"
        onMouseEnter={pauseSlider}
        onMouseLeave={() => setIsPaused(false)}
      >
        <div className="relative min-h-[470px] sm:min-h-[520px] lg:min-h-[560px]">
          {heroCards.map((card, index) => (
            <div
              key={card.image}
              className={`absolute inset-0 transition-all duration-700 ease-out ${index === activeSlide ? 'scale-100 opacity-100' : 'pointer-events-none scale-105 opacity-0'}`}
              aria-hidden={index !== activeSlide}
            >
              <img src={card.image} alt={heroContent[index].title} className="h-full w-full object-cover" />
              <div className="absolute inset-0 bg-[linear-gradient(180deg,rgba(2,6,23,0.04)_15%,rgba(2,6,23,0.88)_100%)]" />
              <div className="absolute inset-x-0 bottom-0 p-6 text-white sm:p-9">
                <span className="inline-flex rounded-full border border-white/20 bg-white/15 px-3 py-1.5 text-xs font-bold backdrop-blur-md">
                  {t('landing.selectedDepartment')}
                </span>
                <h2 className="mt-4 max-w-xl text-2xl font-black leading-10 sm:text-3xl">{heroContent[index].title}</h2>
                <p className="mt-2 max-w-lg text-sm leading-7 text-white/78 sm:text-base">{heroContent[index].subtitle}</p>
              </div>
            </div>
          ))}

          <button type="button" onClick={showPrevious} aria-label={t('common.previous')} className="absolute left-4 top-1/2 z-10 grid h-11 w-11 -translate-y-1/2 place-items-center rounded-full border border-white/20 bg-black/20 text-white opacity-90 backdrop-blur-md transition hover:scale-105 hover:bg-white hover:text-slate-950 sm:left-5">
            <ChevronLeft size={21} />
          </button>
          <button type="button" onClick={showNext} aria-label={t('common.next')} className="absolute right-4 top-1/2 z-10 grid h-11 w-11 -translate-y-1/2 place-items-center rounded-full border border-white/20 bg-black/20 text-white opacity-90 backdrop-blur-md transition hover:scale-105 hover:bg-white hover:text-slate-950 sm:right-5">
            <ChevronRight size={21} />
          </button>

          <div className="absolute inset-x-6 top-6 z-10 flex items-center justify-between gap-4 sm:inset-x-8">
            <div className="flex gap-2">
              {heroCards.map((card, index) => (
                <button
                  key={card.image}
                  type="button"
                  onClick={() => { remainingTime.current = slideDuration; setActiveSlide(index); }}
                  aria-label={`${index + 1}`}
                  className={`h-2 rounded-full transition-all ${index === activeSlide ? 'w-8 bg-white' : 'w-2 bg-white/45 hover:bg-white/75'}`}
                />
              ))}
            </div>
            <span className="rounded-full border border-white/20 bg-black/20 px-3 py-1 text-xs font-bold text-white backdrop-blur-md">
              {String(activeSlide + 1).padStart(2, '0')} / {String(heroCards.length).padStart(2, '0')}
            </span>
          </div>
        </div>

        <div className="absolute inset-x-7 bottom-3 z-20 h-0.5 overflow-hidden rounded-full bg-white/20">
          <span key={activeSlide} className={`slide-progress block h-full origin-left rounded-full bg-white/80 ${isPaused ? 'paused' : ''}`} />
        </div>
      </div>

      <div className="order-1 space-y-6 lg:order-2">
        <span className="inline-flex items-center gap-2 rounded-full border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-2 text-sm font-bold text-[var(--color-primary)] shadow-sm">
          <Sparkles size={17} />
          {t('landing.platformBadge')}
        </span>
        <div>
          <h1 className="max-w-2xl text-3xl font-black leading-[1.5] sm:text-4xl md:text-5xl">
            {t('landing.title')}
          </h1>
          <p className="mt-5 max-w-xl text-sm leading-8 text-[var(--color-muted)] sm:text-base sm:leading-9">
            {t('landing.description')}
          </p>
        </div>
        <div className="flex flex-wrap gap-3">
          <Link to="/careers" className="inline-flex items-center gap-2 rounded-2xl bg-[var(--color-primary)] px-6 py-3.5 text-sm font-black text-white shadow-lg shadow-[var(--color-primary-soft)] transition hover:-translate-y-1">
            {t('landing.viewCareers')}
            <ArrowLeft size={17} />
          </Link>
          <Link to="/chat" className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-6 py-3.5 text-sm font-black text-[var(--color-text)] shadow-sm transition hover:-translate-y-1 hover:border-[var(--color-primary)]">
            {t('landing.startChat')}
          </Link>
        </div>
        <div className="flex flex-wrap gap-2 pt-1">
          {departmentTags.map((tag) => (
            <span key={tag} className="rounded-full border border-[var(--color-border)] bg-[color-mix(in_oklab,var(--color-surface)_86%,transparent)] px-4 py-2 text-xs font-bold text-[var(--color-muted)]">
              {tag}
            </span>
          ))}
        </div>
      </div>
    </section>
  );
}
