import { ChangeEvent, useState } from 'react';
import { uploadOcrImage } from '../api/ocr.api';
import { StateBlock } from '../shared/ui';

export function OcrPage() {
  const [text, setText] = useState('');
  const [imageId, setImageId] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleFile = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) {
      return;
    }

    setLoading(true);
    uploadOcrImage(file)
      .then((result) => {
        setImageId(result.id);
        setText(result.extractedText);
        setError(null);
      })
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  };

  return (
    <div className="space-y-5">
      <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6 shadow-sm">
        <h3 className="text-xl font-bold">OCR تصویر</h3>
        <p className="mt-2 text-sm text-[var(--color-muted)]">ارسال تصویر به `/api/v1/ocr/upload` و نمایش متن استخراج‌شده</p>
        <input
          type="file"
          accept="image/*"
          onChange={handleFile}
          className="mt-5 block w-full rounded-2xl border border-dashed border-[var(--color-border)] bg-[var(--color-page)] p-4 text-sm text-[var(--color-muted)]"
        />
      </section>

      {loading ? <StateBlock title="در حال استخراج متن..." /> : null}
      {error ? <StateBlock title="خطا در OCR" description={error} /> : null}

      {text ? (
        <section className="rounded-3xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6 shadow-sm">
          <span className="text-xs font-bold text-[var(--color-primary)]">Document: {imageId}</span>
          <pre className="mt-4 whitespace-pre-wrap rounded-2xl bg-[var(--color-page)] p-4 text-sm leading-7 text-[var(--color-text)]">
            {text}
          </pre>
        </section>
      ) : null}
    </div>
  );
}
