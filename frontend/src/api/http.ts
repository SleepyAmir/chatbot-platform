import axios, { AxiosError, type AxiosRequestConfig } from 'axios';
import type { ApiResponse } from '../types/api';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

export const httpClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    Accept: 'application/json',
  },
});

export async function apiRequest<T>(
  path: string,
  options: AxiosRequestConfig & { body?: unknown } = {},
): Promise<T> {
  try {
    const { body, data, ...config } = options;
    const response = await httpClient.request<ApiResponse<T>>({
      url: path,
      data: data ?? body,
      ...config,
    });

    if (!response.data.success) {
      throw new Error(response.data.message || 'Request failed');
    }

    return response.data.data;
  } catch (error) {
    if (error instanceof AxiosError) {
      const payload = error.response?.data as Partial<ApiResponse<unknown>> | undefined;
      throw new Error(payload?.message || error.message || 'Request failed');
    }

    throw error;
  }
}

/** For endpoints that return raw JSON without ApiResponse wrapper (e.g. /api/chat-sessions). */
export async function rawRequest<T>(
  path: string,
  options: AxiosRequestConfig & { body?: unknown } = {},
): Promise<T> {
  try {
    const { body, data, ...config } = options;
    const response = await httpClient.request<T>({
      url: path,
      data: data ?? body,
      ...config,
    });

    return response.data;
  } catch (error) {
    if (error instanceof AxiosError) {
      const payload = error.response?.data as { message?: string } | undefined;
      throw new Error(payload?.message || error.message || 'Request failed');
    }

    throw error;
  }
}

export function toQueryString(params: Record<string, string | number | undefined | null>) {
  const searchParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      searchParams.set(key, String(value));
    }
  });

  const queryString = searchParams.toString();
  return queryString ? `?${queryString}` : '';
}
