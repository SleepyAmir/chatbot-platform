type BrandLogoProps = {
  className?: string;
  imageClassName?: string;
  textClassName?: string;
  showText?: boolean;
  subtitle?: string;
  alwaysShowSubtitle?: boolean;
};

export function BrandLogo({
  className = '',
  imageClassName = '',
  textClassName = '',
  showText = true,
  subtitle,
  alwaysShowSubtitle = false,
}: BrandLogoProps) {
  const { t } = useTranslation();
  const resolvedSubtitle = subtitle ?? t('brand.subtitle');
  return (
    <div className={['flex items-center gap-3', className].join(' ')}>
      <div className="grid h-15 w-15 shrink-0 place-items-center overflow-hidden rounded-2xl bg-[white] p-0 shadow-lg shadow-[var(--color-primary-soft)] ring-1 ring-[var(--color-border)] sm:h-12 sm:w-12 sm:rounded-3xl sm:p-1.5">
        <img
          src="/images/mft-logo.png"
          alt={t('brand.logoAlt')}
          className={['h-full w-full object-contain', imageClassName].join(' ')}
        />
      </div>
      {showText ? (
        <div className={textClassName}>
          <p className="text-sm font-black">{t('brand.name')}</p>
          {resolvedSubtitle ? (
            <p
              className={[
                'text-xs text-[var(--color-muted)]',
                alwaysShowSubtitle ? 'block' : 'hidden sm:block',
              ].join(' ')}
            >
              {resolvedSubtitle}
            </p>
          ) : null}
        </div>
      ) : null}
    </div>
  );
}
import { useTranslation } from 'react-i18next';
