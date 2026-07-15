import { apiRequest } from './http';

export type OcrUploadResponse = {
  id: string;
  extractedText: string;
};

export function uploadOcrImage(file: File) {
  const formData = new FormData();
  formData.set('file', file);

  return apiRequest<OcrUploadResponse>('/api/v1/ocr/upload', {
    method: 'POST',
    body: formData,
  });
}
