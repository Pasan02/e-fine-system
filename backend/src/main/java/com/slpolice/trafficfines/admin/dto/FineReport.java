package com.slpolice.trafficfines.admin.dto;

import com.slpolice.trafficfines.fine.entity.Fine;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FineReport {

    private Long id;
    private String referenceNumber;
    private String officerName;
    private String officerDistrict;
    private String categoryCode;
    private String categoryDescription;
    private BigDecimal amount;
    private String driverLicenseNo;
    private String driverName;
    private String vehicleNumber;
    private String district;
    private String location;
    private FineStatus status;
    private LocalDateTime issuedAt;

    public static FineReport from(Fine fine) {
        return FineReport.builder()
                .id(fine.getId())
                .referenceNumber(fine.getReferenceNumber())
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
