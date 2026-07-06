export type QaPair = {
  id: number;
  question: string;
  answer: string;
  courseId?: number | null;
  courseName?: string | null;
};

export type Intent = {
  id: number;
  name: string;
  description?: string | null;
};

export type QaPairDetail = QaPair & {
  intents?: Intent[];
  hasEmbedding?: boolean;
};
