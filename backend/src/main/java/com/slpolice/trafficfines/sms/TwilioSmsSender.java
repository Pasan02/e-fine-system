package com.slpolice.trafficfines.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "sms.twilio.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class TwilioSmsSender implements SmsSender {

    private final SmsProperties smsProperties;

    @PostConstruct
    public void init() {
        Twilio.init(smsProperties.getAccountSid(), smsProperties.getAuthToken());
        log.info("Twilio SMS client initialized");
    }

    @Override
    public void send(String toPhoneNumber, String message) {
        try {
            Message twilioMessage = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(smsProperties.getFromNumber()),
                    message
            ).create();

            log.info("SMS sent to {}: SID={}", toPhoneNumber, twilioMessage.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toPhoneNumber, e.getMessage());
        }
    }
}
