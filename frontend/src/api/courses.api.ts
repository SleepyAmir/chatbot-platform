import { apiRequest, toQueryString } from './http';
import type { Page } from '../types/api';
import type { Course, CourseDetail } from '../types/course';

export type CoursesQuery = {
  keyword?: string;
  page?: number;
  size?: number;
  sort?: string;
};

export function getCourses(params: CoursesQuery = {}) {
  const { keyword, page = 0, size = 12, sort = 'id,desc' } = params;
  return apiRequest<Page<Course>>(
    `/api/courses${toQueryString({ keyword, page, size, sort })}`,
  );
}

export function getAllCourses() {
  return apiRequest<Course[]>('/api/courses/all');
}

export function getCourse(id: number) {
  return apiRequest<Course>(`/api/courses/${id}`);
}

export function getCourseByName(name: string) {
  return apiRequest<Course>(`/api/courses/by-name${toQueryString({ name })}`);
}

export function getCourseDetails(id: number) {
  return apiRequest<CourseDetail>(`/api/courses/${id}/details`);
}

export function searchCourseDetails(keyword?: string, page = 0, size = 12) {
  return apiRequest<Page<CourseDetail>>(
    `/api/courses/details/search${toQueryString({ keyword, page, size })}`,
  );
}
