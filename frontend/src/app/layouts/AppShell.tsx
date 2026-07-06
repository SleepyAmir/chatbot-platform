import { Menu, Search, UserRound, X } from 'lucide-react';
import { useEffect, useState } from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import { mainNavigation } from '../../shared/lib/navigation';
import { BrandLogo, ThemeToggle } from '../../shared/ui';

export function AppShell() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
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
      <header className="sticky top-0 z-40 border-b border-[var(--color-border)] bg-[color-mix(in_oklab,var(--color-page)_88%,transparent)] backdrop-blur-xl">
        <div className="mx-auto flex max-w-7xl items-center justify-between gap-3 px-4 py-3 sm:gap-6 sm:py-4 lg:px-8">
          <NavLink to="/" className="flex items-center gap-3">
            <BrandLogo />
          </NavLink>

          <nav className="hidden items-center gap-1 rounded-full border border-[var(--color-border)] bg-[var(--color-surface)] p-1 shadow-sm lg:flex">
            {mainNavigation.map((item) =>
              item.disabled ? (
                <span
                  key={item.to}
                  className="cursor-not-allowed rounded-full px-4 py-2 text-sm text-[var(--color-muted)] opacity-60"
                >
                  {item.label}
                </span>
              ) : (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.to === '/'}
                  className={({ isActive }) =>
                    [
                      'rounded-full px-4 py-2 text-sm font-semibold transition',
                      isActive
                        ? 'bg-[var(--color-primary)] text-white shadow-md shadow-[var(--color-primary-soft)]'
                        : 'text-[var(--color-muted)] hover:bg-[var(--color-surface-strong)] hover:text-[var(--color-text)]',
                    ].join(' ')
                  }
                >
                  {item.label}
                </NavLink>
              ),
            )}
          </nav>

          <div className="flex items-center gap-2">
            <button className="hidden h-11 w-11 place-items-center rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] text-[var(--color-muted)] transition hover:text-[var(--color-primary)] md:grid">
              <Search size={18} />
            </button>
            <ThemeToggle />
            <button className="hidden items-center gap-2 rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-3 py-2 text-sm font-semibold text-[var(--color-text)] md:inline-flex">
              <UserRound size={17} />
              کاربر لاگین‌شده
            </button>
            <button
              type="button"
              onClick={() => setIsMenuOpen(true)}
              className="inline-flex h-11 w-11 items-center justify-center rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] text-[var(--color-text)] shadow-sm transition hover:border-[var(--color-primary)] lg:hidden"
              aria-label="باز کردن منوی اصلی"
              aria-expanded={isMenuOpen}
            >
              <Menu size={21} />
            </button>
          </div>
        </div>
      </header>

      <div
        className={[
          'fixed inset-0 z-50 bg-black/45 backdrop-blur-sm transition-opacity lg:hidden',
          isMenuOpen ? 'opacity-100' : 'pointer-events-none opacity-0',
        ].join(' ')}
        onClick={closeMenu}
      />

      <aside
        className={[
          'fixed bottom-0 right-0 top-0 z-50 flex w-[min(88vw,380px)] flex-col border-l border-[var(--color-border)] bg-[var(--color-surface)] p-4 shadow-[var(--shadow-card)] transition-transform duration-300 lg:hidden',
          isMenuOpen ? 'translate-x-0' : 'translate-x-full',
        ].join(' ')}
        aria-hidden={!isMenuOpen}
      >
        <div className="flex items-center justify-between gap-3 border-b border-[var(--color-border)] pb-4">
          <div className="flex items-center gap-3">
            <BrandLogo subtitle="منوی سامانه" alwaysShowSubtitle />
          </div>
          <button
            type="button"
            onClick={closeMenu}
            className="grid h-10 w-10 place-items-center rounded-2xl border border-[var(--color-border)] text-[var(--color-muted)]"
            aria-label="بستن منو"
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
                  {item.label}
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
                {item.label}
              </NavLink>
            );
          })}
        </nav>

        <div className="mt-auto rounded-3xl border border-[var(--color-border)] bg-[var(--color-page)] p-4">
          <div className="flex items-center gap-3 text-sm font-bold">
            <UserRound size={18} />
            کاربر لاگین‌شده
          </div>
          <p className="mt-2 text-xs leading-6 text-[var(--color-muted)]">
            در نسخه بعدی، اطلاعات کاربر از سرویس احراز هویت خوانده می‌شود.
          </p>
        </div>
      </aside>

      <main className="relative mx-auto max-w-7xl px-4 py-6 sm:py-8 lg:px-8">
        <Outlet />
      </main>
    </div>
  );
}
