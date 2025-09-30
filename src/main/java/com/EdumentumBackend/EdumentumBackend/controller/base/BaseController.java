package com.EdumentumBackend.EdumentumBackend.controller.base;

import com.EdumentumBackend.EdumentumBackend.dtos.common.ApiResponse;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    /**
     * Extract user ID from the security context
     */
    protected abstract Long getCurrentUserId();

    /**
     * Helper method to create a success response
     */
    protected <T> ResponseEntity<ApiResponse<T>> successResponse(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    /**
     * Helper method to create an error response
     */
    protected <T> ResponseEntity<ApiResponse<T>> errorResponse(String message, int code) {
        return ResponseEntity.status(code).body(ApiResponse.error(message, code));
    }
}