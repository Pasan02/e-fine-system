package com.slpolice.trafficfines.config;

import com.slpolice.trafficfines.sms.SmsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@EnableConfigurationProperties(SmsProperties.class)
public class AsyncConfig {
}
