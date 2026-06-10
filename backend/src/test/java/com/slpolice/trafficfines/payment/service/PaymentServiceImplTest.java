package com.slpolice.trafficfines.payment.service;

import com.slpolice.trafficfines.auth.entity.Role;
import com.slpolice.trafficfines.auth.entity.User;
import com.slpolice.trafficfines.fine.entity.Fine;
import com.slpolice.trafficfines.fine.entity.FineCategory;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import com.slpolice.trafficfines.fine.repository.FineRepository;
import com.slpolice.trafficfines.payment.dto.PaymentDTO;
import com.slpolice.trafficfines.payment.dto.PaymentRequest;
import com.slpolice.trafficfines.payment.entity.Payment;
import com.slpolice.trafficfines.payment.entity.PaymentChannel;
import com.slpolice.trafficfines.payment.entity.PaymentMethod;
import com.slpolice.trafficfines.payment.repository.PaymentRepository;
import com.slpolice.trafficfines.payment.service.impl.PaymentServiceImpl;
import com.slpolice.trafficfines.shared.exception.BadRequestException;
import com.slpolice.trafficfines.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentServiceImpl.
 * Covers all validation logic from Responsibility #5:
 *   - Fine status check (PENDING only)
 *   - Amount matching (must equal category fixed price)
 *   - Duplicate payment prevention
 *
 * As specified in the implementation plan:
 *   "Unit Tests: JUnit 5 + Mockito for service layer (Members 1 & 2)"
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentServiceImpl Unit Tests")
class PaymentServiceImplTest {

    // ── Mocks ────────────────────────────────────────────────────────────────
    @Mock private PaymentRepository paymentRepository;
    @Mock private FineRepository fineRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    // ── System Under Test ────────────────────────────────────────────────────
    @InjectMocks
    private PaymentServiceImpl paymentService;

    // ── Test Data ────────────────────────────────────────────────────────────
    private Fine pendingFine;
    private Payment payment;
    private PaymentRequest validRequest;

    @BeforeEach
    void setUp() {
        User officer = User.builder()
                .id(1L)
                .username("officer1")
                .fullName("P. K. Silva")
                .phoneNumber("0771234567")
                .role(Role.OFFICER)
                .district("WP")
                .build();

        FineCategory category = FineCategory.builder()
                .id(1L)
                .categoryCode("SPD01")
                .description("Exceeding speed limit")
                .amount(new BigDecimal("1500.00"))
                .isActive(true)
                .build();

        pendingFine = Fine.builder()
                .id(1L)
                .referenceNumber("TF-2026-WP-00001")
                .officer(officer)
                .category(category)
                .driverLicenseNo("B1234567")
                .driverName("A. B. Perera")
                .vehicleNumber("CAR-1234")
                .district("WP")
                .status(FineStatus.PENDING)
                .build();

        payment = Payment.builder()
                .id(10L)
                .fine(pendingFine)
                .amountPaid(new BigDecimal("1500.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentChannel(PaymentChannel.WEB_PORTAL)
                .transactionRef("TXN-ABC-123")
                .paidAt(LocalDateTime.now())
                .build();

        validRequest = new PaymentRequest();
        validRequest.setReferenceNumber("TF-2026-WP-00001");
        validRequest.setCategoryCode("SPD01");
        validRequest.setAmount(new BigDecimal("1500.00"));
        validRequest.setPaymentMethod(PaymentMethod.CARD);
        validRequest.setPaymentChannel(PaymentChannel.WEB_PORTAL);
        validRequest.setTransactionRef("TXN-ABC-123");
    }

    // =========================================================================
    // processPayment — happy path
    // =========================================================================
    @Nested
    @DisplayName("processPayment() — success")
    class ProcessPaymentSuccess {

        @Test
        @DisplayName("Should process payment, save it and update fine to PAID")
        void shouldProcessPaymentSuccessfully() {
            when(fineRepository.findByReferenceNumberAndCategoryCategoryCode(
                    "TF-2026-WP-00001", "SPD01"))
                    .thenReturn(Optional.of(pendingFine));
            when(paymentRepository.existsByFineId(1L)).thenReturn(false);
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            PaymentDTO result = paymentService.processPayment(validRequest);

            assertThat(result).isNotNull();
            assertThat(result.getTransactionRef()).isEqualTo("TXN-ABC-123");
            assertThat(result.getAmountPaid()).isEqualByComparingTo("1500.00");

            // Fine status must be updated to PAID
            assertThat(pendingFine.getStatus()).isEqualTo(FineStatus.PAID);

            // Payment should be persisted
            verify(paymentRepository, times(1)).save(any(Payment.class));

            // Fine status update should be saved
            verify(fineRepository, times(1)).save(pendingFine);

            // SMS event should be published
            verify(eventPublisher, times(1)).publishEvent(any());
        }
    }

    // =========================================================================
    // processPayment — validation failures (Responsibility #5)
    // =========================================================================
    @Nested
    @DisplayName("processPayment() — validation failures")
    class ProcessPaymentValidation {

        @Test
        @DisplayName("Should throw ResourceNotFoundException when fine not found")
        void shouldThrowWhenFineNotFound() {
            when(fineRepository.findByReferenceNumberAndCategoryCategoryCode(
                    anyString(), anyString()))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> paymentService.processPayment(validRequest));

            verify(paymentRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should throw BadRequestException when fine is already PAID")
        void shouldThrowWhenFineAlreadyPaid() {
            pendingFine.setStatus(FineStatus.PAID);
            when(fineRepository.findByReferenceNumberAndCategoryCategoryCode(
                    "TF-2026-WP-00001", "SPD01"))
                    .thenReturn(Optional.of(pendingFine));

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> paymentService.processPayment(validRequest));

            assertThat(ex.getMessage()).containsIgnoringCase("already been paid");
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BadRequestException when fine is EXPIRED")
        void shouldThrowWhenFineExpired() {
            pendingFine.setStatus(FineStatus.EXPIRED);
            when(fineRepository.findByReferenceNumberAndCategoryCategoryCode(
                    "TF-2026-WP-00001", "SPD01"))
                    .thenReturn(Optional.of(pendingFine));

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> paymentService.processPayment(validRequest));

            assertThat(ex.getMessage()).containsIgnoringCase("expired");
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BadRequestException when amount does not match category amount")
        void shouldThrowWhenAmountMismatch() {
            when(fineRepository.findByReferenceNumberAndCategoryCategoryCode(
                    "TF-2026-WP-00001", "SPD01"))
                    .thenReturn(Optional.of(pendingFine));

            // Driver submits wrong amount
            validRequest.setAmount(new BigDecimal("500.00"));

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> paymentService.processPayment(validRequest));

            assertThat(ex.getMessage()).containsIgnoringCase("Incorrect payment amount");
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BadRequestException when payment already exists (duplicate)")
        void shouldThrowWhenDuplicatePayment() {
            when(fineRepository.findByReferenceNumberAndCategoryCategoryCode(
                    "TF-2026-WP-00001", "SPD01"))
                    .thenReturn(Optional.of(pendingFine));
            when(paymentRepository.existsByFineId(1L)).thenReturn(true);

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> paymentService.processPayment(validRequest));

            assertThat(ex.getMessage()).containsIgnoringCase("payment record already exists");
            verify(paymentRepository, never()).save(any());
        }
    }

    // =========================================================================
    // getPaymentById
    // =========================================================================
    @Nested
    @DisplayName("getPaymentById()")
    class GetPaymentById {

        @Test
        @DisplayName("Should return PaymentDTO when payment exists")
        void shouldReturnPaymentDtoWhenFound() {
            when(paymentRepository.findById(10L)).thenReturn(Optional.of(payment));

            PaymentDTO result = paymentService.getPaymentById(10L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getReferenceNumber()).isEqualTo("TF-2026-WP-00001");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when payment not found")
        void shouldThrowWhenPaymentNotFound() {
            when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> paymentService.getPaymentById(99L));
        }
    }

    // =========================================================================
    // getPaymentByFineId
    // =========================================================================
    @Nested
    @DisplayName("getPaymentByFineId()")
    class GetPaymentByFineId {

        @Test
        @DisplayName("Should return PaymentDTO for a paid fine")
        void shouldReturnPaymentDtoForFine() {
            when(fineRepository.existsById(1L)).thenReturn(true);
            when(paymentRepository.findByFineId(1L)).thenReturn(Optional.of(payment));

            PaymentDTO result = paymentService.getPaymentByFineId(1L);

            assertThat(result).isNotNull();
            assertThat(result.getFineId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when fine does not exist")
        void shouldThrowWhenFineNotFound() {
            when(fineRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> paymentService.getPaymentByFineId(99L));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when fine exists but has no payment yet")
        void shouldThrowWhenNoPaymentForFine() {
            when(fineRepository.existsById(1L)).thenReturn(true);
            when(paymentRepository.findByFineId(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> paymentService.getPaymentByFineId(1L));
        }
    }
}
