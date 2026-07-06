export type Course = {
  id: number;
  name: string;
  lessonUrl?: string | null;
};

export type CourseCareer = {
  courseId: number;
  courseName: string;
  careerId: number;
  careerTitle: string;
  relevance: number;
};
