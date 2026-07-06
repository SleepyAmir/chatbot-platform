export type Course = {
  id: number;
  name: string;
  lessonUrl?: string | null;
};

export type CourseDetail = {
  id: number;
  courseId: number;
  price: string;
  teacher: string;
  duration: string;
  branch: string;
};

export type CourseCareer = {
  courseId: number;
  courseName: string;
  careerId: number;
  careerTitle: string;
  relevance: number;
};
