package com.example.platform.common.response;

/**
 * Standard response envelope for every REST endpoint in the project.
 * Use the static factory methods below instead of the raw constructor
 * so every module (course, qa, career, ...) produces the same shape.
 */
public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * For endpoints with no payload (e.g. delete operations) — data is null.
     */
    public static ApiResponse<Void> ok(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}