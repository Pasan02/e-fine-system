package com.slpolice.trafficfines.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response object for the API.
 * Uses JsonInclude.Include.NON_NULL to omit fields that are not applicable (e.g., validationErrors)
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    
    // Map to hold field-specific validation errors (e.g., "username" -> "cannot be blank")
    private Map<String, String> validationErrors;
}
