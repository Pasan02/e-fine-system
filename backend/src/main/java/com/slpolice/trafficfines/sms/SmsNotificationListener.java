package com.slpolice.trafficfines.sms;

import com.slpolice.trafficfines.fine.entity.Fine;
import com.slpolice.trafficfines.payment.entity.Payment;
import com.slpolice.trafficfines.payment.service.impl.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmsNotificationListener {

    private final SmsSender smsSender;

    @Async
    @EventListener
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        Payment payment = event.getPayment();
        Fine fine = payment.getFine();

        String officerPhone = fine.getOfficer().getPhoneNumber();
        String officerName = fine.getOfficer().getFullName();
        String referenceNumber = fine.getReferenceNumber();
        String driverName = fine.getDriverName();
        String vehicleNumber = fine.getVehicleNumber();
        String amount = payment.getAmountPaid().toString();

        String message = String.format(
                "Dear %s, Fine %s issued to %s (%s) has been paid. Amount: %s LKR. Transaction: %s",
                officerName, referenceNumber, driverName, vehicleNumber,
                amount, payment.getTransactionRef()
        );

        log.info("Dispatching SMS notification for fine {} to officer {} at {}",
                referenceNumber, officerName, officerPhone);

        smsSender.send(officerPhone, message);
    }
}
