package com.slpolice.trafficfines.sms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SmsProperties Unit Tests")
class SmsPropertiesTest {

    @Test
    @DisplayName("Should hold Twilio configuration values")
    void shouldHoldTwilioConfig() {
        SmsProperties props = new SmsProperties();
        props.setAccountSid("AC123");
        props.setAuthToken("token");
        props.setFromNumber("+1234567890");
        props.setEnabled(true);

        assertThat(props.getAccountSid()).isEqualTo("AC123");
        assertThat(props.getAuthToken()).isEqualTo("token");
        assertThat(props.getFromNumber()).isEqualTo("+1234567890");
        assertThat(props.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should default enabled to true")
    void shouldDefaultEnabledToTrue() {
        SmsProperties props = new SmsProperties();
        assertThat(props.isEnabled()).isTrue();
    }
}
