package com.slpolice.trafficfines.fine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for POST /api/fines — issued by a traffic police officer.
 * Validated using Bean Validation (spring-boot-starter-validation).
 */
@Data
public class CreateFineRequest {

    @NotBlank(message = "Category code is required")
    @Size(max = 20, message = "Category code must be at most 20 characters")
    private String categoryCode;

    @NotBlank(message = "Driver license number is required")
    @Size(max = 20, message = "Driver license number must be at most 20 characters")
    private String driverLicenseNo;

    @NotBlank(message = "Driver name is required")
    private String driverName;

    @NotBlank(message = "Vehicle number is required")
    @Pattern(regexp = "^[A-Z0-9\\-]{2,15}$", message = "Invalid vehicle number format")
    private String vehicleNumber;

    @NotBlank(message = "District is required")
    @Size(max = 50, message = "District must be at most 50 characters")
    private String district;

    private String location;
}
