import { Bot, Menu, X } from 'lucide-react';
import { useEffect, useState } from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { mainNavigation } from '../../shared/lib/navigation';
import { useLanguageStore } from '../../shared/state/languageStore';
import { BrandLogo, LanguageToggle, ThemeToggle } from '../../shared/ui';

export function AppShell() {
  const { t } = useTranslation();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const language = useLanguageStore((state) => state.language);
  const isRtl = language === 'fa';
  const closeMenu = () => setIsMenuOpen(false);

  useEffect(() => {
    document.body.style.overflow = isMenuOpen ? 'hidden' : '';

    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        closeMenu();
      }
    };

    window.addEventListener('keydown', handleEscape);

    return () => {
      document.body.style.overflow = '';
      window.removeEventListener('keydown', handleEscape);
    };
  }, [isMenuOpen]);

  return (
    <div className="min-h-screen overflow-hidden bg-[var(--color-page)] text-[var(--color-text)]">
      <header className="sticky top-0 z-40 border-b border-[var(--color-border)] bg-[color-mix(in_oklab,var(--color-page)_90%,transparent)] shadow-[0_10px_40px_rgb(15_23_42/6%)] backdrop-blur-xl">
        <div className="mx-auto flex max-w-[1440px] items-center gap-3 px-3 py-3 sm:px-5 lg:px-8">
          <NavLink to="/" className="shrink-0 rounded-2xl outline-none ring-[var(--color-primary-soft)] focus-visible:ring-4">
            <BrandLogo textClassName="hidden sm:block xl:hidden 2xl:block" />
          </NavLink>

          <nav className="mx-auto hidden min-w-0 items-center justify-center gap-1 rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] p-1.5 shadow-sm xl:flex">
            {mainNavigation.map((item) =>
              item.disabled ? (
                <span
                  key={item.to}
                  className="cursor-not-allowed whitespace-nowrap rounded-xl px-3 py-2.5 text-sm text-[var(--color-muted)] opacity-60 2xl:px-4"
                >
                  {t(item.labelKey)}
                </span>
              ) : (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.to === '/'}
                  className={({ isActive }) =>
                    [
                      'whitespace-nowrap rounded-xl px-3 py-2.5 text-sm font-semibold transition 2xl:px-4',
                      isActive
                        ? 'bg-[var(--color-primary)] text-white shadow-md shadow-[var(--color-primary-soft)]'
                        : 'text-[var(--color-muted)] hover:bg-[var(--color-surface-strong)] hover:text-[var(--color-text)]',
                    ].join(' ')
                  }
                >
                  {t(item.labelKey)}
                </NavLink>
              ),
            )}
          </nav>

          <div className="ms-auto flex shrink-0 items-center gap-1.5 sm:gap-2 xl:ms-0">
            <LanguageToggle />
            <ThemeToggle />
            <button
              type="button"
              onClick={() => setIsMenuOpen(true)}
              className="inline-flex h-11 w-11 items-center justify-center rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] text-[var(--color-text)] shadow-sm transition hover:border-[var(--color-primary)] xl:hidden"
              aria-label={t('shell.openMenu')}
              aria-expanded={isMenuOpen}
            >
              <Menu size={21} />
            </button>
          </div>
        </div>
      </header>

      <div
        className={[
          'fixed inset-0 z-50 bg-black/45 backdrop-blur-sm transition-opacity xl:hidden',
          isMenuOpen ? 'opacity-100' : 'pointer-events-none opacity-0',
        ].join(' ')}
        onClick={closeMenu}
      />

      <aside
        className={[
          'fixed bottom-0 top-0 z-50 flex w-[min(88vw,380px)] flex-col border-[var(--color-border)] bg-[var(--color-surface)] p-4 shadow-[var(--shadow-card)] transition-transform duration-300 xl:hidden',
          isRtl ? 'right-0 border-l' : 'left-0 border-r',
          isMenuOpen ? 'translate-x-0' : isRtl ? 'translate-x-full' : '-translate-x-full',
        ].join(' ')}
        aria-hidden={!isMenuOpen}
      >
        <div className="flex items-center justify-between gap-3 border-b border-[var(--color-border)] pb-4">
          <div className="flex items-center gap-3">
            <BrandLogo subtitle={t('shell.systemMenu')} alwaysShowSubtitle />
          </div>
          <button
            type="button"
            onClick={closeMenu}
            className="grid h-10 w-10 place-items-center rounded-2xl border border-[var(--color-border)] text-[var(--color-muted)]"
            aria-label={t('shell.closeMenu')}
          >
            <X size={19} />
          </button>
        </div>

        <nav className="mt-5 space-y-2">
          {mainNavigation.map((item) => {
            const Icon = item.icon;

            if (item.disabled) {
              return (
                <span
                  key={item.to}
                  className="flex cursor-not-allowed items-center gap-3 rounded-2xl px-4 py-3 text-sm font-semibold text-[var(--color-muted)] opacity-55"
                >
                  <Icon size={19} />
                  {t(item.labelKey)}
                </span>
              );
            }

            return (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.to === '/'}
                onClick={closeMenu}
                className={({ isActive }) =>
                  [
                    'flex items-center gap-3 rounded-2xl px-4 py-3 text-sm font-bold transition',
                    isActive
                      ? 'bg-[var(--color-primary)] text-white shadow-md shadow-[var(--color-primary-soft)]'
                      : 'text-[var(--color-text)] hover:bg-[var(--color-surface-strong)]',
                  ].join(' ')
                }
              >
                <Icon size={19} />
                {t(item.labelKey)}
              </NavLink>
            );
          })}
        </nav>

        <NavLink
          to="/chat"
          onClick={closeMenu}
          className="mt-auto flex items-center justify-center gap-2 rounded-2xl bg-[var(--color-primary)] px-4 py-3.5 text-sm font-bold text-white shadow-lg shadow-[var(--color-primary-soft)]"
        >
          <Bot size={18} />
          {t('nav.assistant')}
        </NavLink>
      </aside>

      <main className="relative mx-auto w-full max-w-[1440px] px-4 py-6 sm:px-5 sm:py-8 lg:px-8">
        <Outlet />
      </main>
    </div>
  );
}
