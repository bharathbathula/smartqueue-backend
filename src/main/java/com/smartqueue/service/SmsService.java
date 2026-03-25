package com.smartqueue.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class SmsService {

    @Value("${twilio.account_sid:PLACEHOLDER}")
    private String accountSid;

    @Value("${twilio.auth_token:PLACEHOLDER}")
    private String authToken;

    @Value("${twilio.phone_number:PLACEHOLDER}")
    private String fromNumber;

    private boolean isConfigured = false;

    @PostConstruct
    public void init() {
        if (!"PLACEHOLDER".equals(accountSid) && !"PLACEHOLDER".equals(authToken)) {
            Twilio.init(accountSid, authToken);
            isConfigured = true;
        }
    }

    public void sendSms(String to, String body) {
        if (!isConfigured) {
            System.out.println("SMS service not configured. Would have sent to " + to + ": " + body);
            return;
        }
        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromNumber),
                body)
            .create();
    }
}
