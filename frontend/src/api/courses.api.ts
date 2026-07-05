import { apiRequest, toQueryString } from './http';
import type { Page } from '../types/api';
import type { Course, CourseCareer } from '../types/course';

export function getCourses(keyword?: string) {
  return apiRequest<Page<Course>>(
    `/api/courses${toQueryString({ keyword, size: 12, sort: 'id,desc' })}`,
  );
}

export function getAllCourses() {
  return apiRequest<Course[]>('/api/courses/all');
}

export function getCareersByCourse(courseId: number) {
  return apiRequest<CourseCareer[]>(`/api/courses/${courseId}/careers`);
}
