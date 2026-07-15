type StateBlockProps = {
  title: string;
  description?: string;
};

export function StateBlock({ title, description }: StateBlockProps) {
  return (
    <div className="rounded-[2rem] border border-dashed border-[var(--color-border)] bg-[color-mix(in_oklab,var(--color-surface)_82%,transparent)] p-8 text-center shadow-sm">
      <div className="mx-auto grid h-12 w-12 place-items-center rounded-2xl bg-[var(--color-surface-strong)] text-[var(--color-primary)]">
        <Inbox size={22} />
      </div>
      <h3 className="mt-4 font-bold text-[var(--color-text)]">{title}</h3>
      {description ? <p className="mx-auto mt-2 max-w-xl text-sm leading-7 text-[var(--color-muted)]">{description}</p> : null}
    </div>
  );
}
import { Inbox } from 'lucide-react';
