package com.slpolice.trafficfines.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DistrictReport {

    private String district;
    private long totalFines;
    private long totalPaid;
    private long totalPending;
    private long totalExpired;
    private BigDecimal totalRevenue;
}
