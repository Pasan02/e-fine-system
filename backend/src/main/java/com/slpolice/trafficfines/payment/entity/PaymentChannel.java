package com.slpolice.trafficfines.payment.entity;

/**
 * Channel through which the payment was made.
 * Maps to PAYMENTS.payment_channel: MOBILE_APP | WEB_PORTAL
 */
public enum PaymentChannel {
    MOBILE_APP,
    WEB_PORTAL
}
