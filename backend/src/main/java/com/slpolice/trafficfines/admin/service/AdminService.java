package com.slpolice.trafficfines.admin.service;

import com.slpolice.trafficfines.admin.dto.CategoryReport;
import com.slpolice.trafficfines.admin.dto.DashboardSummary;
import com.slpolice.trafficfines.admin.dto.DistrictReport;
import com.slpolice.trafficfines.admin.dto.FineReport;
import com.slpolice.trafficfines.admin.dto.OfficerReport;
import com.slpolice.trafficfines.admin.dto.PaymentReport;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import com.slpolice.trafficfines.payment.entity.PaymentChannel;
import com.slpolice.trafficfines.payment.entity.PaymentMethod;

import java.util.List;

public interface AdminService {

    DashboardSummary getDashboardSummary();

    List<FineReport> getAllFines(FineStatus status, String district);

    List<PaymentReport> getAllPayments(PaymentMethod method, PaymentChannel channel);

    List<OfficerReport> getOfficerReports();

    List<DistrictReport> getDistrictReports();

    List<CategoryReport> getCategoryReports();
}
