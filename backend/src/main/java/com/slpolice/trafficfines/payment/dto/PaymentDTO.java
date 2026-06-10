package com.slpolice.trafficfines.payment.dto;

import com.slpolice.trafficfines.payment.entity.Payment;
import com.slpolice.trafficfines.payment.entity.PaymentChannel;
import com.slpolice.trafficfines.payment.entity.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for a Payment — used in all API responses.
 * As per the DTO Pattern: "Never expose JPA entities directly."
 *
 * This is the agreed API contract shape for the payment receipt,
 * shared with the frontend teams (Member 3, 4).
 */
@Data
@Builder
public class PaymentDTO {

    private Long id;

    // Associated fine info (for receipt display)
    private Long fineId;
    private String referenceNumber;
    private String driverName;
    private String vehicleNumber;
    private String categoryDescription;

    // Payment details
    private BigDecimal amountPaid;
    private PaymentMethod paymentMethod;
    private PaymentChannel paymentChannel;
    private String transactionRef;
    private LocalDateTime paidAt;

    /**
     * Static factory method to convert a Payment entity to a PaymentDTO.
     */
    public static PaymentDTO from(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .fineId(payment.getFine().getId())
                .referenceNumber(payment.getFine().getReferenceNumber())
                .driverName(payment.getFine().getDriverName())
                .vehicleNumber(payment.getFine().getVehicleNumber())
                .categoryDescription(payment.getFine().getCategory().getDescription())
                .amountPaid(payment.getAmountPaid())
                .paymentMethod(payment.getPaymentMethod())
                .paymentChannel(payment.getPaymentChannel())
                .transactionRef(payment.getTransactionRef())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
