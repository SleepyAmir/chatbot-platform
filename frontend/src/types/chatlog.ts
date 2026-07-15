export type ChatLog = {
  id: number;
  sessionId: string;
  question: string;
  answer: string;
  modelUsed?: string | null;
  traceId?: string | null;
  createdAt?: string | null;
};

export type FeedbackPayload = {
  logId: number;
  rating: 1 | -1;
  comment?: string;
};

export type Feedback = {
  id: number;
  logId: number;
  rating: number;
  comment?: string | null;
  createdAt?: string | null;
};
