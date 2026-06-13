package com.slpolice.trafficfines.sms;

import com.slpolice.trafficfines.auth.entity.User;
import com.slpolice.trafficfines.fine.entity.Fine;
import com.slpolice.trafficfines.payment.entity.Payment;
import com.slpolice.trafficfines.payment.service.impl.PaymentCompletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SmsNotificationListener Unit Tests")
class SmsNotificationListenerTest {

    @Mock private SmsSender smsSender;
    @Mock private PaymentCompletedEvent event;
    @Mock private Payment payment;
    @Mock private Fine fine;
    @Mock private User officer;

    @Captor
    private ArgumentCaptor<String> phoneCaptor;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    private SmsNotificationListener listener;

    @BeforeEach
    void setUp() {
        listener = new SmsNotificationListener(smsSender);
    }

    @Nested
    @DisplayName("onPaymentCompleted()")
    class OnPaymentCompleted {

        @Test
        @DisplayName("Should send SMS to officer with payment details")
        void shouldSendSmsToOfficer() {
            when(event.getPayment()).thenReturn(payment);
            when(payment.getFine()).thenReturn(fine);
            when(fine.getOfficer()).thenReturn(officer);
            when(officer.getPhoneNumber()).thenReturn("+94771234567");
            when(officer.getFullName()).thenReturn("P. K. Silva");
            when(fine.getReferenceNumber()).thenReturn("TF-2026-WP-00001");
            when(fine.getDriverName()).thenReturn("A. B. Perera");
            when(fine.getVehicleNumber()).thenReturn("CAR-1234");
            when(payment.getAmountPaid()).thenReturn(new BigDecimal("1500.00"));
            when(payment.getTransactionRef()).thenReturn("TXN-ABC-123");

            listener.onPaymentCompleted(event);

            verify(smsSender).send(phoneCaptor.capture(), messageCaptor.capture());

            assertThat(phoneCaptor.getValue()).isEqualTo("+94771234567");
            assertThat(messageCaptor.getValue())
                    .contains("P. K. Silva")
                    .contains("TF-2026-WP-00001")
                    .contains("A. B. Perera")
                    .contains("CAR-1234")
                    .contains("1500.00")
                    .contains("TXN-ABC-123");
        }

        @Test
        @DisplayName("Should handle null officer phone gracefully")
        void shouldHandleNullOfficerPhone() {
            when(event.getPayment()).thenReturn(payment);
            when(payment.getFine()).thenReturn(fine);
            when(fine.getOfficer()).thenReturn(officer);
            when(officer.getPhoneNumber()).thenReturn(null);
            when(officer.getFullName()).thenReturn("P. K. Silva");
            when(fine.getReferenceNumber()).thenReturn("TF-2026-WP-00001");
            when(fine.getDriverName()).thenReturn("A. B. Perera");
            when(fine.getVehicleNumber()).thenReturn("CAR-1234");
            when(payment.getAmountPaid()).thenReturn(new BigDecimal("500.00"));
            when(payment.getTransactionRef()).thenReturn("TXN-456");

            listener.onPaymentCompleted(event);

            verify(smsSender).send(phoneCaptor.capture(), messageCaptor.capture());
            assertThat(phoneCaptor.getValue()).isNull();
        }
    }
}
