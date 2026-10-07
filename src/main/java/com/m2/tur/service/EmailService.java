package com.m2.tur.service;

import com.m2.tur.infra.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendVerificationEmail(String email, String code){
        Context context = new Context();
        context.setVariable("code", code);

        String htmlBody = templateEngine.process("verification-email", context);

        send(email, "Confirm your registration - tur.", htmlBody);
    }

    @Async
    public void sendResetPasswordEmail(String email, String code){
        Context context = new Context();
        context.setVariable("code", code);

        String htmlBody = templateEngine.process("password-reset-email", context);

        send(email, "Reset your password - tur.", htmlBody);
    }

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@tur.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new EmailException("Critical failure when attempting to send the verification email");
        }
    }
}
