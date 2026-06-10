package com.slpolice.trafficfines.payment.controller;

import com.slpolice.trafficfines.payment.dto.PaymentDTO;
import com.slpolice.trafficfines.payment.dto.PaymentRequest;
import com.slpolice.trafficfines.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Payment endpoints.
 * All endpoints are as defined in the implementation plan Section 5.2:
 *
 *   POST /api/payments               — Process a payment (Public)
 *   GET  /api/payments/{id}          — Get payment receipt (Authenticated)
 *   GET  /api/payments/fine/{fineId} — Get payment for a fine (Authenticated)
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * POST /api/payments
     * Public — a driver (via mobile app or web portal) submits payment.
     * No login required; the dual-key (referenceNumber + categoryCode) acts as authentication.
     *
     * As per Section 5.4 payment flow.
     */
    @PostMapping
    public ResponseEntity<PaymentDTO> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentDTO receipt = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
    }

    /**
     * GET /api/payments/{id}
     * Authenticated — retrieves a payment receipt by payment ID.
     * Used by drivers or officers to view the confirmation.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    /**
     * GET /api/payments/fine/{fineId}
     * Authenticated — retrieves the payment associated with a specific fine.
     * Used by officers to confirm a fine was paid before releasing a licence.
     */
    @GetMapping("/fine/{fineId}")
    public ResponseEntity<PaymentDTO> getPaymentByFineId(@PathVariable Long fineId) {
        return ResponseEntity.ok(paymentService.getPaymentByFineId(fineId));
    }
}
