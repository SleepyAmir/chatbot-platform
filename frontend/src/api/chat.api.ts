import { apiRequest, rawRequest } from './http';
import type { ChatMessageRecord, ChatResponse, ChatSession } from '../types/chat';

export type SendChatParams = {
  question: string;
  sessionId?: string;
  userId?: string;
};

export function sendChatMessage(params: SendChatParams) {
  return apiRequest<ChatResponse>('/api/chat', {
    method: 'POST',
    body: params,
  });
}

export function sendChatWithFile(params: SendChatParams & { file: File }) {
  const formData = new FormData();
  formData.set('question', params.question);
  if (params.sessionId) {
    formData.set('sessionId', params.sessionId);
  }
  if (params.userId) {
    formData.set('userId', params.userId);
  }
  formData.set('file', params.file);

  return apiRequest<ChatResponse>('/api/chat', {
    method: 'POST',
    body: formData,
  });
}

export function createChatSession(userId?: string) {
  return rawRequest<ChatSession>('/api/chat-sessions', {
    method: 'POST',
    body: userId ? { userId } : {},
  });
}

export function appendChatSessionMessage(sessionId: string, role: 'user' | 'assistant', content: string) {
  return rawRequest<ChatSession>(`/api/chat-sessions/${sessionId}/messages`, {
    method: 'POST',
    body: { role, content },
  });
}

export function getChatSessionHistory(sessionId: string) {
  return rawRequest<ChatMessageRecord[]>(`/api/chat-sessions/${sessionId}/history`);
}
