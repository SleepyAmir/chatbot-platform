type StateBlockProps = {
  title: string;
  description?: string;
};

export function StateBlock({ title, description }: StateBlockProps) {
  return (
    <div className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6 text-center shadow-sm">
      <h3 className="font-semibold text-[var(--color-text)]">{title}</h3>
      {description ? <p className="mt-2 text-sm text-[var(--color-muted)]">{description}</p> : null}
    </div>
  );
}
