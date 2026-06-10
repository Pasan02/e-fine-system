package com.slpolice.trafficfines.payment.dto;

import com.slpolice.trafficfines.payment.entity.PaymentChannel;
import com.slpolice.trafficfines.payment.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request body for POST /api/payments — submitted by a driver (mobile app or web portal).
 *
 * As shown in the payment flow (Section 5.4 of the implementation plan):
 *   POST /api/payments {referenceNo, categoryCode, paymentDetails}
 *
 * The dual-key (referenceNumber + categoryCode) ensures the driver is paying
 * the correct fine for the correct violation category.
 */
@Data
public class PaymentRequest {

    /**
     * Unique reference number from the physical fine sheet.
     */
    @NotBlank(message = "Reference number is required")
    private String referenceNumber;

    /**
     * Category code from the physical fine sheet (e.g., SPD01).
     * Used as a second verification factor alongside the reference number.
     */
    @NotBlank(message = "Category code is required")
    private String categoryCode;

    /**
     * Amount the driver is paying. Must match the category's fixed amount.
     * Validated in service layer against the stored category amount.
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    /**
     * Payment method chosen by the driver: CARD or MOBILE_WALLET.
     */
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    /**
     * Channel the payment is coming from: MOBILE_APP or WEB_PORTAL.
     */
    @NotNull(message = "Payment channel is required")
    private PaymentChannel paymentChannel;

    /**
     * Transaction reference returned by the (mocked/real) payment gateway.
     * In production this would come from PayHere or similar gateway callback.
     */
    @NotBlank(message = "Transaction reference is required")
    private String transactionRef;
}
