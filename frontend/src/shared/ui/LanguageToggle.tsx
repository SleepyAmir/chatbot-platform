import { Languages } from 'lucide-react';
import { useTranslation } from 'react-i18next';
import { useLanguageStore } from '../state/languageStore';

export function LanguageToggle() {
  const { t } = useTranslation();
  const { language, toggleLanguage } = useLanguageStore();
  const nextLanguage = language === 'fa' ? 'EN' : 'FA';

  return (
    <button
      type="button"
      onClick={toggleLanguage}
      className="inline-flex h-11 items-center justify-center gap-2 rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-3 text-sm font-bold text-[var(--color-text)] shadow-sm transition hover:border-[var(--color-primary)] hover:text-[var(--color-primary)]"
      aria-label={t(language === 'fa' ? 'language.switchToEnglish' : 'language.switchToPersian')}
      title={t(language === 'fa' ? 'language.switchToEnglish' : 'language.switchToPersian')}
    >
      <Languages size={18} />
      <span dir="ltr">{nextLanguage}</span>
    </button>
  );
}
