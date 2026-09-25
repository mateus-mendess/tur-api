package com.m2.tur.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendVerificationEmail(String email, String code){
        Context context = new Context();
        context.setVariable("code", code);

        String htmlBody = templateEngine.process("verification-email", context);

        send(email, "Confirm your registration - tur.", htmlBody);
    }

    public void sendResetPasswordEmail(String email, String code){
        Context context = new Context();
        context.setVariable("code", code);

        String htmlBody = templateEngine.process("password-reset-email", context);

        send(email, "Reset your password - tur.", htmlBody);
    }

    private void send(String to, String subject, String text) {
        var message = new SimpleMailMessage();
        message.setFrom("noreply@tur.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}
