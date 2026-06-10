package com.slpolice.trafficfines.payment.service;

import com.slpolice.trafficfines.payment.dto.PaymentDTO;
import com.slpolice.trafficfines.payment.dto.PaymentRequest;

/**
 * Service interface for the Payment module.
 * Interface-based design as per the implementation plan.
 *
 * Covers all endpoints from implementation plan Section 5.2:
 *   POST /api/payments               — Process a payment
 *   GET  /api/payments/{id}          — Get payment receipt
 *   GET  /api/payments/fine/{fineId} — Get payment for a fine
 */
public interface PaymentService {

    /**
     * Process a payment for a traffic fine.
     * Validates fine status, amount matching, then persists the payment
     * and updates the fine status to PAID.
     *
     * As per Section 5.4 Payment + SMS Flow.
     */
    PaymentDTO processPayment(PaymentRequest request);

    /**
     * Retrieve a payment by its ID (for receipt display).
     * Supports: GET /api/payments/{id}
     */
    PaymentDTO getPaymentById(Long id);

    /**
     * Retrieve the payment associated with a specific fine.
     * Supports: GET /api/payments/fine/{fineId}
     */
    PaymentDTO getPaymentByFineId(Long fineId);
}
