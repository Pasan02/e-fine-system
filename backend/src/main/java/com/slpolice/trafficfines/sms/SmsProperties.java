package com.slpolice.trafficfines.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sms.twilio")
public class SmsProperties {

    private String accountSid;
    private String authToken;
    private String fromNumber;
    private boolean enabled = true;
}
