export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
};

export type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};
