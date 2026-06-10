package com.slpolice.trafficfines.payment.service.impl;

import com.slpolice.trafficfines.payment.entity.Payment;
import org.springframework.context.ApplicationEvent;

/**
 * Spring ApplicationEvent published after a payment is successfully processed.
 *
 * Design Pattern: Observer/Event (Section 7 of the implementation plan):
 *   "Payment → SMS notification | Async SMS dispatch after payment events"
 *
 * This event decouples the Payment module from the SMS module.
 * Member 2 should create a @EventListener (SmsNotificationListener) in the
 * sms/ package that listens for this event and dispatches the SMS to the officer.
 *
 * The listener should be annotated with @Async so SMS failures do not roll back
 * the payment transaction (Circuit Breaker pattern for SMS — ADR-005).
 */
public class PaymentCompletedEvent extends ApplicationEvent {

    private final Payment payment;

    public PaymentCompletedEvent(Object source, Payment payment) {
        super(source);
        this.payment = payment;
    }

    /**
     * The completed payment — contains fine reference, officer phone, driver info.
     */
    public Payment getPayment() {
        return payment;
    }
}
