import { apiRequest, toQueryString } from './http';
import type { Page } from '../types/api';
import type { ChatLog, Feedback, FeedbackPayload } from '../types/chatlog';

export function getChatLogs(sessionId?: string, page = 0, size = 20) {
  return apiRequest<Page<ChatLog>>(
    `/api/chat-logs${toQueryString({ sessionId, page, size })}`,
  );
}

export function getChatLog(id: number) {
  return apiRequest<ChatLog>(`/api/chat-logs/${id}`);
}

export function getChatLogsBySession(sessionId: string) {
  return apiRequest<ChatLog[]>(`/api/chat-logs/session/${sessionId}`);
}

export function submitFeedback(payload: FeedbackPayload) {
  return apiRequest<Feedback>('/api/feedback', {
    method: 'POST',
    body: payload,
  });
}

export function getFeedbackByLog(logId: number) {
  return apiRequest<Feedback[]>(`/api/feedback/log/${logId}`);
}
