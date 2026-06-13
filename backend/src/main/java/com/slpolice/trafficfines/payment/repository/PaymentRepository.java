package com.slpolice.trafficfines.payment.repository;

import com.slpolice.trafficfines.payment.entity.Payment;
import com.slpolice.trafficfines.payment.entity.PaymentChannel;
import com.slpolice.trafficfines.payment.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * JPA Repository for the Payment entity.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find a payment by the associated fine's ID.
     * Used by GET /api/payments/fine/{fineId}
     */
    Optional<Payment> findByFineId(Long fineId);

    /**
     * Check whether a payment already exists for a given fine.
     * Used in payment validation to prevent duplicate payments.
     */
    boolean existsByFineId(Long fineId);

    // ── Admin reporting queries ─────────────────────────────────────────────

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p")
    BigDecimal sumAmountPaid();

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE p.paymentMethod = :method")
    BigDecimal sumAmountPaidByMethod(PaymentMethod method);

    long countByPaymentMethod(PaymentMethod paymentMethod);

    long countByPaymentChannel(PaymentChannel paymentChannel);
}
