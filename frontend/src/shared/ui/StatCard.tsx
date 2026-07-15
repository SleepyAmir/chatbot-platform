type StatCardProps = {
  label: string;
  value: string | number;
  hint: string;
};

export function StatCard({ label, value, hint }: StatCardProps) {
  return (
    <div className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-5 shadow-sm">
      <p className="text-sm text-[var(--color-muted)]">{label}</p>
      <strong className="mt-2 block text-3xl font-bold text-[var(--color-text)]">{value}</strong>
      <span className="mt-2 block text-xs text-[var(--color-muted)]">{hint}</span>
    </div>
  );
}
