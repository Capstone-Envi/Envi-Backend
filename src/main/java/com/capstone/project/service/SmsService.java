package com.capstone.project.service;

import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {
    private final String accountSid;
    private final String authToken;
    private final String twilioPhoneNumber;
    public SmsService(@Value("${twilio.accountSid}") String accountSid,
                      @Value("${twilio.authToken}") String authToken,
                      @Value("${twilio.phone}") String twilioPhoneNumber) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.twilioPhoneNumber = twilioPhoneNumber;
    }

    public void sendSMS(String toPhoneNumber, String message) {
        Twilio.init(accountSid, authToken);
        com.twilio.rest.api.v2010.account.Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(twilioPhoneNumber),  // Your Twilio phone number
                message
        ).create();
    }
}
