package com.gestionalmacen.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    List<String> details,
    Map<String, String> validationErrors,
    LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, List.of(), Map.of(), LocalDateTime.now());
    }

    public static ApiResponse<Void> failure(String message, List<String> details, Map<String, String> validationErrors) {
        return new ApiResponse<>(false, message, null, details, validationErrors, LocalDateTime.now());
    }
}
