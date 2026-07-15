export type ChatModelUsed = 'cache' | 'qa_pairs' | 'llm' | 'course_lookup';

export type ChatResponse = {
  success: boolean;
  answer: string | null;
  error: string | null;
  sessionId: string;
  traceId?: string;
  elapsedMs?: number;
  matchedQaId?: number | null;
  confidence?: number | null;
  modelUsed?: ChatModelUsed;
};

export type ChatMessageRecord = {
  role: 'user' | 'assistant';
  content: string;
  timestamp?: string;
};

export type ChatSession = {
  id: string;
  sessionId: string;
  userId: string;
  createdAt: string;
  updatedAt: string;
  messages: ChatMessageRecord[];
  lastTopic: string | null;
  lastEntityId: number | null;
};

export type ChatUiMessage = {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
  pending?: boolean;
  error?: boolean;
};
