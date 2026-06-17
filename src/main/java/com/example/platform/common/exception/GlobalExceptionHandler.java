package com.example.platform.common.exception;

import com.example.platform.common.response.ApiResponse;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centralizes exception -> HTTP response mapping for the whole API, so every
 * module (course, qa, career, chat, ...) returns the same ApiResponse shape
 * on error instead of a raw stack trace / default Spring error page.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Entity not found (e.g. GET /api/courses/{id} with a non-existing id).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Bad pagination/sort input, e.g. ?sort=string (a value that isn't a real
     * entity property) coming from Swagger's default placeholder or a typo.
     * Spring Data wraps this case in InvalidDataAccessApiUsageException, and
     * an unresolvable nested sort path can also surface as PropertyReferenceException.
     */
    @ExceptionHandler({InvalidDataAccessApiUsageException.class, PropertyReferenceException.class})
    public ResponseEntity<ApiResponse<Void>> handleInvalidQueryUsage(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid request parameters: " + ex.getMessage()));
    }

    /**
     * Wrong type for a path/query parameter, e.g. /api/courses/abc where {id} expects an Integer.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid value for parameter '" + ex.getName() + "': " + ex.getValue();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    /**
     * @Valid failures on @RequestBody DTOs (e.g. CourseRequest.name blank).
     * Returns a field -> message map inside ApiResponse.data so the client
     * knows exactly which field failed and why.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Validation failed", fieldErrors));
    }

    /**
     * Service-level "this already exists" / bad-argument cases
     * (e.g. duplicate course name on create/update in phase 2).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Catch-all safety net: anything not handled above still returns a clean
     * ApiResponse with 500 instead of a raw stack trace / default error page.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Unexpected error: " + ex.getMessage()));
    }
}