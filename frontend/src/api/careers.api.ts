import { apiRequest, toQueryString } from './http';
import type { Page } from '../types/api';
import type { Career, CareerRequirement, CareerSkill } from '../types/career';
import type { CourseCareer } from '../types/course';

export type CareerPayload = {
  title: string;
  description?: string;
  sourceUrl?: string;
};

export type CareersQuery = {
  keyword?: string;
  page?: number;
  size?: number;
  sort?: string;
};

export type CareerRequirementPayload = {
  chunkIndex: number;
  requirementText: string;
};

export type CourseCareerPayload = {
  careerId: number;
  relevance: number;
};

export function getCareers(params: CareersQuery = {}) {
  const { keyword, page = 0, size = 12, sort = 'id,desc' } = params;
  return apiRequest<Page<Career>>(
    `/api/careers${toQueryString({ keyword, page, size, sort })}`,
  );
}

export function getAllCareers() {
  return apiRequest<Career[]>('/api/careers/all');
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

export function updateCareer(id: number, payload: CareerPayload) {
  return apiRequest<Career>(`/api/careers/${id}`, {
    method: 'PUT',
    body: payload,
  });
}

export function deleteCareer(id: number) {
  return apiRequest<void>(`/api/careers/${id}`, {
    method: 'DELETE',
  });
}

export function getCareerSkills(careerId: number) {
  return apiRequest<CareerSkill[]>(`/api/careers/${careerId}/skills`);
}

export function addCareerSkill(careerId: number, skillName: string) {
  return apiRequest<CareerSkill>(`/api/careers/${careerId}/skills`, {
    method: 'POST',
    body: { skillName },
  });
}

export function getCareerRequirements(careerId: number) {
  return apiRequest<CareerRequirement[]>(`/api/careers/${careerId}/requirements`);
}

export function addCareerRequirement(careerId: number, payload: CareerRequirementPayload) {
  return apiRequest<CareerRequirement>(`/api/careers/${careerId}/requirements`, {
    method: 'POST',
    body: payload,
  });
}

export function getCareerCourses(careerId: number) {
  return apiRequest<CourseCareer[]>(`/api/careers/${careerId}/courses`);
}

export function getCareersByCourse(courseId: number) {
  return apiRequest<CourseCareer[]>(`/api/courses/${courseId}/careers`);
}

export function linkCourseCareer(courseId: number, payload: CourseCareerPayload) {
  return apiRequest<CourseCareer>(`/api/courses/${courseId}/careers`, {
    method: 'POST',
    body: payload,
  });
}
