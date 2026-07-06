import { Moon, Sun } from 'lucide-react';
import { useThemeStore } from '../state/themeStore';

export function ThemeToggle() {
  const { theme, toggleTheme } = useThemeStore();
  const isDark = theme === 'dark';

  return (
    <button
      type="button"
      onClick={toggleTheme}
      className="inline-flex h-11 w-11 items-center justify-center rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] text-[var(--color-text)] shadow-sm transition hover:-translate-y-0.5 hover:border-[var(--color-primary)]"
      aria-label={isDark ? 'فعال کردن حالت روشن' : 'فعال کردن حالت تاریک'}
    >
      {isDark ? <Sun size={18} /> : <Moon size={18} />}
    </button>
  );
}
