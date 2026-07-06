import { apiRequest, toQueryString } from './http';
import type { Page } from '../types/api';
import type { Career, CareerRequirement, CareerSearchResult, CareerSkill } from '../types/career';

export type CareerPayload = {
  title: string;
  description?: string;
  sourceUrl?: string;
};

export function getCareers(keyword?: string) {
  return apiRequest<Page<Career>>(
    `/api/careers${toQueryString({ keyword, size: 12, sort: 'id,desc' })}`,
  );
}

export function getCareer(id: number) {
  return apiRequest<Career>(`/api/careers/${id}`);
}

export function createCareer(payload: CareerPayload) {
  return apiRequest<Career>('/api/careers', {
    method: 'POST',
    body: payload,
  });
}

export function getCareerSkills(careerId: number) {
  return apiRequest<CareerSkill[]>(`/api/careers/${careerId}/skills`);
}

export function getCareerRequirements(careerId: number) {
  return apiRequest<CareerRequirement[]>(`/api/careers/${careerId}/requirements`);
}

export function searchCareerRequirements(embedding: number[], topK = 5, minSimilarity = 0) {
  return apiRequest<CareerSearchResult[]>('/api/careers/search', {
    method: 'POST',
    body: {
      embedding,
      topK,
      minSimilarity,
    },
  });
}
