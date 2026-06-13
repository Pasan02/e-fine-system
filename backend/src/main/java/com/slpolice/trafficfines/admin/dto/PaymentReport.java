package com.slpolice.trafficfines.admin.dto;

import com.slpolice.trafficfines.payment.entity.Payment;
import com.slpolice.trafficfines.payment.entity.PaymentChannel;
import com.slpolice.trafficfines.payment.entity.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentReport {

    private Long id;
    private String referenceNumber;
    private String driverName;
    private String vehicleNumber;
    private String categoryDescription;
    private BigDecimal amountPaid;
    private PaymentMethod paymentMethod;
    private PaymentChannel paymentChannel;
    private String transactionRef;
    private LocalDateTime paidAt;

    public static PaymentReport from(Payment payment) {
        return PaymentReport.builder()
                .id(payment.getId())
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
