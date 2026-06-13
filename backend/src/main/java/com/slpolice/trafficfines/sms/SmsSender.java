package com.slpolice.trafficfines.sms;

public interface SmsSender {

    void send(String toPhoneNumber, String message);
}
