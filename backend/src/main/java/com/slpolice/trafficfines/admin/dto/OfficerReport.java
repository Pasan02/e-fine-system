package com.slpolice.trafficfines.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfficerReport {

    private Long id;
    private String fullName;
    private String district;
    private String phoneNumber;
    private long totalFinesIssued;
    private long totalFinesPaid;
}
