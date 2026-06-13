package com.slpolice.trafficfines.admin.controller;

import com.slpolice.trafficfines.admin.audit.Auditable;
import com.slpolice.trafficfines.admin.dto.CategoryReport;
import com.slpolice.trafficfines.admin.dto.DashboardSummary;
import com.slpolice.trafficfines.admin.dto.DistrictReport;
import com.slpolice.trafficfines.admin.dto.FineReport;
import com.slpolice.trafficfines.admin.dto.OfficerReport;
import com.slpolice.trafficfines.admin.dto.PaymentReport;
import com.slpolice.trafficfines.admin.service.AdminService;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import com.slpolice.trafficfines.payment.entity.PaymentChannel;
import com.slpolice.trafficfines.payment.entity.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @Auditable(action = "VIEW_DASHBOARD")
    public ResponseEntity<DashboardSummary> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardSummary());
    }

    @GetMapping("/fines")
    @Auditable(action = "VIEW_FINES")
    public ResponseEntity<List<FineReport>> getFines(
            @RequestParam(required = false) FineStatus status,
            @RequestParam(required = false) String district) {
        return ResponseEntity.ok(adminService.getAllFines(status, district));
    }

    @GetMapping("/payments")
    @Auditable(action = "VIEW_PAYMENTS")
    public ResponseEntity<List<PaymentReport>> getPayments(
            @RequestParam(required = false) PaymentMethod method,
            @RequestParam(required = false) PaymentChannel channel) {
        return ResponseEntity.ok(adminService.getAllPayments(method, channel));
    }

    @GetMapping("/officers")
    @Auditable(action = "VIEW_OFFICERS")
    public ResponseEntity<List<OfficerReport>> getOfficers() {
        return ResponseEntity.ok(adminService.getOfficerReports());
    }

    @GetMapping("/reports/districts")
    @Auditable(action = "VIEW_DISTRICT_REPORTS")
    public ResponseEntity<List<DistrictReport>> getDistrictReports() {
        return ResponseEntity.ok(adminService.getDistrictReports());
    }

    @GetMapping("/reports/categories")
    @Auditable(action = "VIEW_CATEGORY_REPORTS")
    public ResponseEntity<List<CategoryReport>> getCategoryReports() {
        return ResponseEntity.ok(adminService.getCategoryReports());
    }
}
