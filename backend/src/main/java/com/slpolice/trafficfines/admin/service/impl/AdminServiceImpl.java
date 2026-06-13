package com.slpolice.trafficfines.admin.service.impl;

import com.slpolice.trafficfines.admin.dto.CategoryReport;
import com.slpolice.trafficfines.admin.dto.DashboardSummary;
import com.slpolice.trafficfines.admin.dto.DistrictReport;
import com.slpolice.trafficfines.admin.dto.FineReport;
import com.slpolice.trafficfines.admin.dto.OfficerReport;
import com.slpolice.trafficfines.admin.dto.PaymentReport;
import com.slpolice.trafficfines.admin.service.AdminService;
import com.slpolice.trafficfines.auth.entity.Role;
import com.slpolice.trafficfines.auth.repository.UserRepository;
import com.slpolice.trafficfines.fine.entity.Fine;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import com.slpolice.trafficfines.fine.repository.FineRepository;
import com.slpolice.trafficfines.payment.entity.PaymentChannel;
import com.slpolice.trafficfines.payment.entity.PaymentMethod;
import com.slpolice.trafficfines.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final FineRepository fineRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummary getDashboardSummary() {
        long totalFines = fineRepository.count();
        long totalPaid = fineRepository.countByStatus(FineStatus.PAID);
        long totalPending = fineRepository.countByStatus(FineStatus.PENDING);
        long totalExpired = fineRepository.countByStatus(FineStatus.EXPIRED);

        DashboardSummary.DashboardSummaryBuilder builder = DashboardSummary.builder()
                .totalFines(totalFines)
                .totalPaid(totalPaid)
                .totalPending(totalPending)
                .totalExpired(totalExpired)
                .totalRevenue(paymentRepository.sumAmountPaid())
                .totalOfficers(userRepository.countByRole(Role.OFFICER))
                .totalAdmins(userRepository.countByRole(Role.ADMIN))
                .totalDrivers(userRepository.countByRole(Role.DRIVER))
                .finesByDistrict(toMap(fineRepository.countByDistrictGrouped()))
                .paidFinesByDistrict(toMap(fineRepository.countPaidByDistrictGrouped()))
                .revenueByMethod(DashboardSummary.RevenueBreakdown.builder()
                        .card(paymentRepository.sumAmountPaidByMethod(PaymentMethod.CARD))
                        .mobileWallet(paymentRepository.sumAmountPaidByMethod(PaymentMethod.MOBILE_WALLET))
                        .build())
                .mobileAppPayments(paymentRepository.countByPaymentChannel(PaymentChannel.MOBILE_APP))
                .webPortalPayments(paymentRepository.countByPaymentChannel(PaymentChannel.WEB_PORTAL));

        return builder.build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FineReport> getAllFines(FineStatus status, String district) {
        List<Fine> fines;

        if (status != null && district != null) {
            fines = fineRepository.findAll().stream()
                    .filter(f -> f.getStatus() == status && district.equals(f.getDistrict()))
                    .collect(Collectors.toList());
        } else if (status != null) {
            fines = fineRepository.findAll().stream()
                    .filter(f -> f.getStatus() == status)
                    .collect(Collectors.toList());
        } else if (district != null) {
            fines = fineRepository.findAll().stream()
                    .filter(f -> district.equals(f.getDistrict()))
                    .collect(Collectors.toList());
        } else {
            fines = fineRepository.findAll();
        }

        return fines.stream()
                .map(FineReport::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentReport> getAllPayments(PaymentMethod method, PaymentChannel channel) {
        return paymentRepository.findAll().stream()
                .filter(p -> method == null || p.getPaymentMethod() == method)
                .filter(p -> channel == null || p.getPaymentChannel() == channel)
                .map(PaymentReport::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfficerReport> getOfficerReports() {
        List<Object[]> officerRows = userRepository.findAllOfficerSummaries();
        Map<Long, Long> finesIssued = new HashMap<>();
        Map<Long, Long> finesPaid = new HashMap<>();

        List<Fine> allFines = fineRepository.findAll();
        for (Fine fine : allFines) {
            Long oid = fine.getOfficer().getId();
            finesIssued.merge(oid, 1L, Long::sum);
            if (fine.getStatus() == FineStatus.PAID) {
                finesPaid.merge(oid, 1L, Long::sum);
            }
        }

        return officerRows.stream().map(row -> OfficerReport.builder()
                .id((Long) row[0])
                .fullName((String) row[1])
                .district((String) row[2])
                .phoneNumber((String) row[3])
                .totalFinesIssued(finesIssued.getOrDefault((Long) row[0], 0L))
                .totalFinesPaid(finesPaid.getOrDefault((Long) row[0], 0L))
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictReport> getDistrictReports() {
        Map<String, Long> totalFines = toMap(fineRepository.countByDistrictGrouped());
        Map<String, Long> totalPaid = toMap(fineRepository.countPaidByDistrictGrouped());
        Map<String, Long> totalPending = toMap(fineRepository.countPendingByDistrictGrouped());
        Map<String, Long> totalExpired = toMap(fineRepository.countExpiredByDistrictGrouped());
        Map<String, BigDecimal> revenue = toBigDecimalMap(paymentRepository.sumAmountPaidByDistrictGrouped());

        return totalFines.keySet().stream().map(district -> DistrictReport.builder()
                .district(district)
                .totalFines(totalFines.getOrDefault(district, 0L))
                .totalPaid(totalPaid.getOrDefault(district, 0L))
                .totalPending(totalPending.getOrDefault(district, 0L))
                .totalExpired(totalExpired.getOrDefault(district, 0L))
                .totalRevenue(revenue.getOrDefault(district, BigDecimal.ZERO))
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryReport> getCategoryReports() {
        Map<String, Long> totalFines = new HashMap<>();
        Map<String, Long> totalPaid = new HashMap<>();
        Map<String, BigDecimal> revenue = new HashMap<>();
        Map<String, String> descriptions = new HashMap<>();
        Map<String, BigDecimal> amounts = new HashMap<>();

        for (Object[] row : fineRepository.countByCategoryGrouped()) {
            String code = (String) row[0];
            descriptions.put(code, (String) row[1]);
            totalFines.put(code, (Long) row[2]);
        }
        for (Object[] row : fineRepository.countPaidByCategoryGrouped()) {
            String code = (String) row[0];
            totalPaid.put(code, (Long) row[2]);
        }
        for (Object[] row : paymentRepository.sumAmountPaidByCategoryGrouped()) {
            String code = (String) row[0];
            revenue.put(code, (BigDecimal) row[2]);
        }

        return fineRepository.findAll().stream()
                .map(f -> f.getCategory())
                .distinct()
                .map(cat -> CategoryReport.builder()
                        .categoryCode(cat.getCategoryCode())
                        .description(cat.getDescription())
                        .amount(cat.getAmount())
                        .totalFinesIssued(totalFines.getOrDefault(cat.getCategoryCode(), 0L))
                        .totalFinesPaid(totalPaid.getOrDefault(cat.getCategoryCode(), 0L))
                        .totalRevenue(revenue.getOrDefault(cat.getCategoryCode(), BigDecimal.ZERO))
                        .build()
                ).collect(Collectors.toList());
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        Map<String, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put((String) row[0], (Long) row[1]);
        }
        return map;
    }

    private Map<String, BigDecimal> toBigDecimalMap(List<Object[]> rows) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put((String) row[0], (BigDecimal) row[1]);
        }
        return map;
    }
}
