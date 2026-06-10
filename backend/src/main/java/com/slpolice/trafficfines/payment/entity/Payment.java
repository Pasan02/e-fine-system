package com.slpolice.trafficfines.payment.entity;

import com.slpolice.trafficfines.fine.entity.Fine;
import com.slpolice.trafficfines.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a payment made by a driver to settle a traffic fine.
 * Maps to the PAYMENTS table as defined in the ER diagram.
 *
 * A Fine can have at most one Payment (one-to-one relationship).
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The fine being paid (FK to FINES — one-to-one).
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fine_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Fine fine;

    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    /**
     * Payment method: CARD | MOBILE_WALLET
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    /**
     * External transaction reference from the payment gateway.
     */
    @Column(name = "transaction_ref", nullable = false, length = 100)
    private String transactionRef;

    /**
     * Channel used: MOBILE_APP | WEB_PORTAL
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_channel", nullable = false, length = 20)
    private PaymentChannel paymentChannel;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;
}
