package com.m2.tur.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String otpCode) {
        var message = new SimpleMailMessage();
        message.setFrom("noreply@tur.com");
        message.setTo(to);
        message.setSubject("Confirme seu cadastro - tur.");
        message.setText("""
                Olá!
                
                Use o código abaixo para confirmar seu cadastro na plataforma tur.:
                
                %s
                
                Esse código expira em 10 minutos.
                """.formatted(otpCode));

        mailSender.send(message);
    }

}
