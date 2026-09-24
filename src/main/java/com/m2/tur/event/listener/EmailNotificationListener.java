package com.m2.tur.event.listener;

import com.m2.tur.event.UserRegisteredEvent;
import com.m2.tur.service.EmailService;
import com.m2.tur.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailNotificationListener {
    private final EmailService emailService;
    private final OtpService otpService;

    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        String otpCode = otpService.generateOtpCode(event.userId());

        emailService.sendVerificationEmail(event.email(), otpCode);
    }
}
