package com.slpolice.trafficfines.payment.service.impl;

import com.slpolice.trafficfines.fine.entity.Fine;
import com.slpolice.trafficfines.fine.entity.FineStatus;
import com.slpolice.trafficfines.fine.repository.FineRepository;
import com.slpolice.trafficfines.payment.dto.PaymentDTO;
import com.slpolice.trafficfines.payment.dto.PaymentRequest;
import com.slpolice.trafficfines.payment.entity.Payment;
import com.slpolice.trafficfines.payment.repository.PaymentRepository;
import com.slpolice.trafficfines.payment.service.PaymentService;
import com.slpolice.trafficfines.shared.exception.BadRequestException;
import com.slpolice.trafficfines.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Implementation of PaymentService.
 *
 * Responsibilities (as per implementation plan Member 1):
 *   - Payment validation logic: check fine status (PENDING), amount matching
 *   - Save payment, update fine status to PAID
 *   - Publish a PaymentCompletedEvent for Member 2's SMS service (Observer/Event pattern)
 *
 * Design patterns applied (as per implementation plan Section 7):
 *   - Strategy Pattern: PaymentMethod is a strategy enum (CARD / MOBILE_WALLET)
 *   - Observer/Event: Payment → SMS notification via Spring ApplicationEventPublisher
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final FineRepository fineRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Process a payment — core flow from implementation plan Section 5.4.
     *
     * Validation steps (Responsibility #5):
     *   1. Look up fine by referenceNumber AND categoryCode (dual-key)
     *   2. Check fine status is PENDING (reject PAID/EXPIRED)
     *   3. Check amount paid matches the category's fixed amount
     *   4. Check no duplicate payment exists
     *
     * On success:
     *   - Persist Payment record
     *   - Update Fine status to PAID
     *   - Publish PaymentCompletedEvent (SMS dispatch — Member 2's responsibility)
     */
    @Override
    @Transactional
    public PaymentDTO processPayment(PaymentRequest request) {
        log.info("Processing payment for reference: {}, category: {}",
                request.getReferenceNumber(), request.getCategoryCode());

        // ── Step 1: Locate the fine using dual-key lookup ──────────────────────
        Fine fine = fineRepository
                .findByReferenceNumberAndCategoryCategoryCode(
                        request.getReferenceNumber(), request.getCategoryCode())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No fine found matching the provided reference number and category code."));

        // ── Step 2: Validate fine status (Responsibility #5) ───────────────────
        if (fine.getStatus() == FineStatus.PAID) {
            throw new BadRequestException("This fine has already been paid.");
        }
        if (fine.getStatus() == FineStatus.EXPIRED) {
            throw new BadRequestException("This fine has expired and can no longer be paid online.");
        }

        // ── Step 3: Validate amount matches category fixed amount ───────────────
        BigDecimal expectedAmount = fine.getCategory().getAmount();
        if (request.getAmount().compareTo(expectedAmount) != 0) {
            throw new BadRequestException(String.format(
                    "Incorrect payment amount. Expected: %.2f LKR, received: %.2f LKR.",
                    expectedAmount, request.getAmount()));
        }

        // ── Step 4: Guard against duplicate payment ─────────────────────────────
        if (paymentRepository.existsByFineId(fine.getId())) {
            throw new BadRequestException("A payment record already exists for this fine.");
        }

        // ── Persist the payment ─────────────────────────────────────────────────
        Payment payment = Payment.builder()
                .fine(fine)
                .amountPaid(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentChannel(request.getPaymentChannel())
                .transactionRef(request.getTransactionRef())
                .paidAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // ── Update fine status to PAID ──────────────────────────────────────────
        fine.setStatus(FineStatus.PAID);
        fineRepository.save(fine);

        log.info("Payment successful. Fine {} is now PAID. Transaction: {}",
                fine.getReferenceNumber(), savedPayment.getTransactionRef());

        // ── Publish event for SMS notification (Observer/Event pattern) ─────────
        // Member 2 will implement SmsNotificationListener to handle this event.
        // Using Spring's ApplicationEventPublisher keeps this module decoupled from SMS.
        eventPublisher.publishEvent(new PaymentCompletedEvent(this, savedPayment));

        return PaymentDTO.from(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return PaymentDTO.from(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByFineId(Long fineId) {
        // Validate the fine exists first
        if (!fineRepository.existsById(fineId)) {
            throw new ResourceNotFoundException("Fine not found with id: " + fineId);
        }
        Payment payment = paymentRepository.findByFineId(fineId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No payment found for fine id: " + fineId));
        return PaymentDTO.from(payment);
    }
}
