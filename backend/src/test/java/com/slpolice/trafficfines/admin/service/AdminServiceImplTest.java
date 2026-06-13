package com.slpolice.trafficfines.admin.service;

import com.slpolice.trafficfines.admin.dto.DashboardSummary;
import com.slpolice.trafficfines.admin.dto.FineReport;
import com.slpolice.trafficfines.admin.dto.OfficerReport;
import com.slpolice.trafficfines.admin.dto.PaymentReport;
import com.slpolice.trafficfines.admin.service.impl.AdminServiceImpl;
import com.slpolice.trafficfines.auth.entity.Role;
import com.slpolice.trafficfines.auth.entity.User;
import com.slpolice.trafficfines.auth.repository.UserRepository;
import com.slpolice.trafficfines.fine.entity.Fine;
import com.slpolice.trafficfines.fine.entity.FineCategory;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import com.slpolice.trafficfines.fine.repository.FineRepository;
import com.slpolice.trafficfines.payment.entity.Payment;
import com.slpolice.trafficfines.payment.entity.PaymentChannel;
import com.slpolice.trafficfines.payment.entity.PaymentMethod;
import com.slpolice.trafficfines.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminServiceImpl Unit Tests")
class AdminServiceImplTest {

    @Mock private FineRepository fineRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User officer;
    private FineCategory category;
    private Fine fine;
    private Payment payment;

    @BeforeEach
    void setUp() {
        officer = User.builder()
                .id(1L)
                .username("officer1")
                .fullName("P. K. Silva")
                .phoneNumber("0771234567")
                .role(Role.OFFICER)
                .district("WP")
                .build();

        category = FineCategory.builder()
                .id(1L)
                .categoryCode("SPD01")
                .description("Exceeding speed limit")
                .amount(new BigDecimal("1500.00"))
                .isActive(true)
                .build();

        fine = Fine.builder()
                .id(1L)
                .referenceNumber("TF-2026-WP-00001")
                .officer(officer)
                .category(category)
                .driverLicenseNo("B1234567")
                .driverName("A. B. Perera")
                .vehicleNumber("CAR-1234")
                .district("WP")
                .location("Colombo 03")
                .status(FineStatus.PAID)
                .build();

        payment = Payment.builder()
                .id(10L)
                .fine(fine)
                .amountPaid(new BigDecimal("1500.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentChannel(PaymentChannel.WEB_PORTAL)
                .transactionRef("TXN-ABC-123")
                .paidAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("getDashboardSummary()")
    class GetDashboardSummary {

        @Test
        @DisplayName("Should return dashboard with correct aggregated values")
        void shouldReturnDashboardSummary() {
            when(fineRepository.count()).thenReturn(100L);
            when(fineRepository.countByStatus(FineStatus.PAID)).thenReturn(60L);
            when(fineRepository.countByStatus(FineStatus.PENDING)).thenReturn(35L);
            when(fineRepository.countByStatus(FineStatus.EXPIRED)).thenReturn(5L);
            when(paymentRepository.sumAmountPaid()).thenReturn(new BigDecimal("90000.00"));
            when(userRepository.countByRole(Role.OFFICER)).thenReturn(10L);
            when(userRepository.countByRole(Role.ADMIN)).thenReturn(2L);
            when(userRepository.countByRole(Role.DRIVER)).thenReturn(50L);
            when(fineRepository.countByDistrictGrouped()).thenReturn(List.of(
                    new Object[]{"WP", 60L}, new Object[]{"CP", 40L}
            ));
            when(fineRepository.countPaidByDistrictGrouped()).thenReturn(List.of(
                    new Object[]{"WP", 40L}, new Object[]{"CP", 20L}
            ));
            when(paymentRepository.sumAmountPaidByMethod(PaymentMethod.CARD))
                    .thenReturn(new BigDecimal("50000.00"));
            when(paymentRepository.sumAmountPaidByMethod(PaymentMethod.MOBILE_WALLET))
                    .thenReturn(new BigDecimal("40000.00"));
            when(paymentRepository.countByPaymentChannel(PaymentChannel.MOBILE_APP)).thenReturn(25L);
            when(paymentRepository.countByPaymentChannel(PaymentChannel.WEB_PORTAL)).thenReturn(35L);

            DashboardSummary summary = adminService.getDashboardSummary();

            assertThat(summary.getTotalFines()).isEqualTo(100L);
            assertThat(summary.getTotalPaid()).isEqualTo(60L);
            assertThat(summary.getTotalPending()).isEqualTo(35L);
            assertThat(summary.getTotalExpired()).isEqualTo(5L);
            assertThat(summary.getTotalRevenue()).isEqualByComparingTo("90000.00");
            assertThat(summary.getTotalOfficers()).isEqualTo(10L);
            assertThat(summary.getTotalAdmins()).isEqualTo(2L);
            assertThat(summary.getTotalDrivers()).isEqualTo(50L);
            assertThat(summary.getFinesByDistrict()).containsEntry("WP", 60L);
            assertThat(summary.getPaidFinesByDistrict()).containsEntry("CP", 20L);
            assertThat(summary.getRevenueByMethod().getCard()).isEqualByComparingTo("50000.00");
            assertThat(summary.getRevenueByMethod().getMobileWallet()).isEqualByComparingTo("40000.00");
            assertThat(summary.getMobileAppPayments()).isEqualTo(25L);
            assertThat(summary.getWebPortalPayments()).isEqualTo(35L);
        }
    }

    @Nested
    @DisplayName("getAllFines()")
    class GetAllFines {

        @Test
        @DisplayName("Should return all fines with no filters")
        void shouldReturnAllFines() {
            when(fineRepository.findAll()).thenReturn(List.of(fine));

            List<FineReport> results = adminService.getAllFines(null, null);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getReferenceNumber()).isEqualTo("TF-2026-WP-00001");
        }

        @Test
        @DisplayName("Should filter fines by status")
        void shouldFilterByStatus() {
            when(fineRepository.findAll()).thenReturn(List.of(fine));

            List<FineReport> results = adminService.getAllFines(FineStatus.PAID, null);

            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("Should filter fines by district")
        void shouldFilterByDistrict() {
            when(fineRepository.findAll()).thenReturn(List.of(fine));

            List<FineReport> results = adminService.getAllFines(null, "CP");

            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("getAllPayments()")
    class GetAllPayments {

        @Test
        @DisplayName("Should return all payments with no filters")
        void shouldReturnAllPayments() {
            when(paymentRepository.findAll()).thenReturn(List.of(payment));

            List<PaymentReport> results = adminService.getAllPayments(null, null);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getTransactionRef()).isEqualTo("TXN-ABC-123");
        }

        @Test
        @DisplayName("Should filter payments by method")
        void shouldFilterByMethod() {
            when(paymentRepository.findAll()).thenReturn(List.of(payment));

            List<PaymentReport> results = adminService.getAllPayments(PaymentMethod.CARD, null);

            assertThat(results).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getOfficerReports()")
    class GetOfficerReports {

        @Test
        @DisplayName("Should return officer reports with fine counts")
        void shouldReturnOfficerReports() {
            when(userRepository.findAllOfficerSummaries()).thenReturn(List.<Object[]>of(
                    new Object[]{1L, "P. K. Silva", "WP", "0771234567"}
            ));
            when(fineRepository.findAll()).thenReturn(List.of(fine));

            List<OfficerReport> results = adminService.getOfficerReports();

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getId()).isEqualTo(1L);
            assertThat(results.get(0).getFullName()).isEqualTo("P. K. Silva");
            assertThat(results.get(0).getTotalFinesIssued()).isEqualTo(1L);
            assertThat(results.get(0).getTotalFinesPaid()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should return empty list when no officers exist")
        void shouldReturnEmptyWhenNoOfficers() {
            when(userRepository.findAllOfficerSummaries()).thenReturn(List.of());
            when(fineRepository.findAll()).thenReturn(List.of());

            List<OfficerReport> results = adminService.getOfficerReports();

            assertThat(results).isEmpty();
        }
    }
}
