import { Bot, MessagesSquare } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

export function AssistantCta() {
  const { t } = useTranslation();
  return (
    <section className="relative my-10 overflow-hidden rounded-[2.5rem] border border-[var(--color-border)] bg-[linear-gradient(135deg,color-mix(in_oklab,var(--color-primary)_18%,var(--color-surface)),var(--color-surface))] p-7 shadow-[var(--shadow-card)] md:p-10">
      <div className="absolute -left-16 -top-16 h-44 w-44 rounded-full bg-[var(--color-primary-soft)] blur-2xl" />
      <div className="relative grid gap-8 lg:grid-cols-[1fr_320px] lg:items-center">
        <div>
          <span className="inline-flex items-center gap-2 rounded-full bg-[var(--color-surface)] px-4 py-2 text-sm font-black text-[var(--color-primary)]">
            <MessagesSquare size={17} />
            {t('landing.assistantTitle')}
          </span>
          <h2 className="mt-5 text-2xl font-black leading-10 md:text-3xl">
            {t('landing.assistantHeading')}
          </h2>
          <p className="mt-4 max-w-2xl text-sm leading-8 text-[var(--color-muted)]">
            {t('landing.assistantDescription')}
          </p>
          <Link
            to="/chat"
            className="mt-6 inline-flex items-center gap-2 rounded-2xl bg-[var(--color-primary)] px-5 py-3 text-sm font-bold text-white shadow-lg shadow-[var(--color-primary-soft)]"
          >
            <Bot size={18} />
            {t('landing.startChat')}
          </Link>
        </div>
        <div className="rounded-[2rem] border border-[var(--color-border)] bg-[var(--color-surface)] p-5">
          <div className="flex items-center gap-3">
            <div className="grid h-12 w-12 place-items-center rounded-2xl bg-[var(--color-primary)] text-white">
              <Bot size={24} />
            </div>
            <div>
              <p className="font-black">MFT Assistant</p>
              <p className="text-xs text-[var(--color-muted)]">{t('landing.assistantReady')}</p>
            </div>
          </div>
          <div className="mt-5 rounded-2xl bg-[var(--color-page)] p-4 text-sm leading-7 text-[var(--color-muted)]">
            {t('landing.assistantExample')}
          </div>
        </div>
      </div>
    </section>
  );
}
