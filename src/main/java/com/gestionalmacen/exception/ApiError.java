package com.gestionalmacen.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ApiError(
	boolean success,
	String message,
	List<String> details,
	Map<String, String> validationErrors,
	LocalDateTime timestamp
) {}
