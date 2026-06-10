package com.slpolice.trafficfines.fine.dto;

import com.slpolice.trafficfines.fine.entity.Fine;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for a Fine — used in all API responses.
 * As per the DTO Pattern: "Never expose JPA entities directly."
 * This DTO is the agreed API contract for the fine payload shared with
 * the frontend teams (Member 3, 4) and documented in the parallel_development_guide.
 */
@Data
@Builder
public class FineDTO {

    private Long id;
    private String referenceNumber;

    // Officer info
    private Long officerId;
    private String officerName;
    private String officerDistrict;

    // Category info
    private String categoryCode;
    private String categoryDescription;
    private BigDecimal amount;

    // Driver info
    private String driverLicenseNo;
    private String driverName;
    private String vehicleNumber;
    private String district;
    private String location;

    private FineStatus status;
    private LocalDateTime issuedAt;  // maps to createdAt

    /**
     * Static factory method to convert a Fine entity to a FineDTO.
     * Keeps mapping logic in one place (avoid scattering it across service/controller).
     */
    public static FineDTO from(Fine fine) {
        return FineDTO.builder()
                .id(fine.getId())
                .referenceNumber(fine.getReferenceNumber())
                .officerId(fine.getOfficer().getId())
                .officerName(fine.getOfficer().getFullName())
                .officerDistrict(fine.getOfficer().getDistrict())
                .categoryCode(fine.getCategory().getCategoryCode())
                .categoryDescription(fine.getCategory().getDescription())
                .amount(fine.getCategory().getAmount())
                .driverLicenseNo(fine.getDriverLicenseNo())
                .driverName(fine.getDriverName())
                .vehicleNumber(fine.getVehicleNumber())
                .district(fine.getDistrict())
                .location(fine.getLocation())
                .status(fine.getStatus())
                .issuedAt(fine.getCreatedAt())
                .build();
    }
}
