package com.slpolice.trafficfines.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardSummary {

    private long totalFines;
    private long totalPaid;
    private long totalPending;
    private long totalExpired;
    private BigDecimal totalRevenue;

    private long totalOfficers;
    private long totalAdmins;
    private long totalDrivers;

    private Map<String, Long> finesByDistrict;
    private Map<String, Long> paidFinesByDistrict;

    private RevenueBreakdown revenueByMethod;
    private long mobileAppPayments;
    private long webPortalPayments;

    @Data
    @Builder
    public static class RevenueBreakdown {
        private BigDecimal card;
        private BigDecimal mobileWallet;
    }
}
