import { AlertCircle, Bot, ScanLine } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { PageHeader } from '../shared/ui';

export function OcrPage() {
  const { t } = useTranslation();
  return (
    <div className="space-y-5">
      <PageHeader title={t('ocr.title')} description={t('ocr.unavailable')} icon={<ScanLine size={23} />} />
      <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6 shadow-sm">
        <div className="flex items-start gap-4">
          <div className="grid h-12 w-12 shrink-0 place-items-center rounded-2xl bg-[color-mix(in_oklab,var(--color-warning)_18%,transparent)] text-[var(--color-warning)]">
            <AlertCircle size={24} />
          </div>
          <div>
            <h3 className="text-lg font-bold">{t('ocr.alternative')}</h3>
            <p className="mt-2 text-sm leading-8 text-[var(--color-muted)]">
              {t('ocr.alternativeText')}
            </p>
          </div>
        </div>
      </section>

      <section className="relative overflow-hidden rounded-[2rem] border border-[var(--color-border)] bg-[linear-gradient(135deg,color-mix(in_oklab,var(--color-primary)_14%,var(--color-surface)),var(--color-surface))] p-6 shadow-[var(--shadow-card)]">
        <div className="pointer-events-none absolute -end-16 -bottom-20 h-48 w-48 rounded-full bg-[var(--color-primary-soft)] blur-3xl" />
        <h4 className="relative text-lg font-black">{t('chat.title')}</h4>
        <p className="relative mt-2 max-w-2xl text-sm leading-8 text-[var(--color-muted)]">{t('chat.description')}</p>
        <Link
          to="/chat"
          className="relative mt-5 inline-flex items-center gap-2 rounded-2xl bg-[var(--color-primary)] px-5 py-3 text-sm font-bold text-white shadow-lg shadow-[var(--color-primary-soft)] transition hover:-translate-y-1"
        >
          <Bot size={18} />
          {t('ocr.goToAssistant')}
        </Link>
      </section>
    </div>
  );
}
