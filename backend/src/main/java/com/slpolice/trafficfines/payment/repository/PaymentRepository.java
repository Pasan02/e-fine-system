package com.slpolice.trafficfines.payment.repository;

import com.slpolice.trafficfines.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
