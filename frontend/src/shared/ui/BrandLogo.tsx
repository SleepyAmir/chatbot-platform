type BrandLogoProps = {
  className?: string;
  imageClassName?: string;
  showText?: boolean;
  subtitle?: string;
  alwaysShowSubtitle?: boolean;
};

export function BrandLogo({
  className = '',
  imageClassName = '',
  showText = true,
  subtitle = 'MFTPlus Chatbot Platform',
  alwaysShowSubtitle = false,
}: BrandLogoProps) {
  return (
    <div className={['flex items-center gap-3', className].join(' ')}>
      <div className="grid h-15 w-15 shrink-0 place-items-center overflow-hidden rounded-2xl bg-[white] p-0 shadow-lg shadow-[var(--color-primary-soft)] ring-1 ring-[var(--color-border)] sm:h-12 sm:w-12 sm:rounded-3xl sm:p-1.5">
        <img
          src="/images/mft-logo.png"
          alt="لوگوی مجتمع فنی تهران"
          className={['h-full w-full object-contain', imageClassName].join(' ')}
        />
      </div>
      {showText ? (
        <div>
          <p className="text-sm font-black">مجتمع فنی تهران</p>
          {subtitle ? (
            <p
              className={[
                'text-xs text-[var(--color-muted)]',
                alwaysShowSubtitle ? 'block' : 'hidden sm:block',
              ].join(' ')}
            >
              {subtitle}
            </p>
          ) : null}
        </div>
      ) : null}
    </div>
  );
}
