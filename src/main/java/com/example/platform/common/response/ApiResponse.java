package com.example.platform.common.response;


public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {
}