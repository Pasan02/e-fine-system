package com.slpolice.trafficfines.sms;

import com.slpolice.trafficfines.config.SmsCircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TwilioSmsSender Unit Tests")
class TwilioSmsSenderTest {

    @Mock private SmsProperties smsProperties;
    @Mock private CircuitBreaker smsCircuitBreaker;

    private TwilioSmsSender twilioSmsSender;

    @BeforeEach
    void setUp() {
        twilioSmsSender = new TwilioSmsSender(smsProperties, smsCircuitBreaker);
    }

    @Nested
    @DisplayName("send()")
    class Send {

        @Test
        @DisplayName("Should delegate to circuit breaker executeRunnable")
        void shouldDelegateToCircuitBreaker() {
            doAnswer(invocation -> {
                ((Runnable) invocation.getArgument(0)).run();
                return null;
            }).when(smsCircuitBreaker).executeRunnable(any(Runnable.class));

            assertThatCode(() -> twilioSmsSender.send("+94771234567", "Test message"))
                    .doesNotThrowAnyException();

            verify(smsCircuitBreaker).executeRunnable(any(Runnable.class));
        }

        @Test
        @DisplayName("Should handle CallNotPermittedException gracefully")
        void shouldHandleCircuitBreakerOpen() {
            doThrow(CallNotPermittedException.class)
                    .when(smsCircuitBreaker).executeRunnable(any(Runnable.class));

            assertThatCode(() -> twilioSmsSender.send("+94771234567", "Test"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should handle runtime exception from Twilio gracefully")
        void shouldHandleTwilioException() {
            doAnswer(invocation -> {
                ((Runnable) invocation.getArgument(0)).run();
                return null;
            }).when(smsCircuitBreaker).executeRunnable(any(Runnable.class));

            assertThatCode(() -> twilioSmsSender.send("+94771234567", "Test"))
                    .doesNotThrowAnyException();
        }
    }
}
