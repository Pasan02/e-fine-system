package com.slpolice.trafficfines.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CategoryReport {

    private String categoryCode;
    private String description;
    private BigDecimal amount;
    private long totalFinesIssued;
    private long totalFinesPaid;
    private BigDecimal totalRevenue;
}
