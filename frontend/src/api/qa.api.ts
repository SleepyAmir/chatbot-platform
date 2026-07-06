import { apiRequest, toQueryString } from './http';
import type { Intent, QaPair, QaPairDetail } from '../types/qa';

export function getAllQaPairs() {
  return apiRequest<QaPair[]>('/api/qa/all');
}

export function searchQaPairs(keyword?: string) {
  return apiRequest<QaPair[]>(`/api/qa${toQueryString({ keyword })}`);
}

export function getQaPair(id: number) {
  return apiRequest<QaPair>(`/api/qa/${id}`);
}

export function getQaPairDetail(id: number) {
  return apiRequest<QaPairDetail>(`/api/qa/${id}/detail`);
}

export function getQaPairsByCourse(courseId: number) {
  return apiRequest<QaPair[]>(`/api/qa/by-course/${courseId}`);
}

export function getQaPairsByIntent(name: string) {
  return apiRequest<QaPair[]>(`/api/qa/by-intent${toQueryString({ name })}`);
}

export function getAllIntents() {
  return apiRequest<Intent[]>('/api/intents/all');
}
