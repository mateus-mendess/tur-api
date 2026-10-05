package com.m2.tur.service;

import com.m2.tur.infra.exception.EmailException;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @Nested
    class SendEmail {
        @Test
        void should_successfully_send_email() {
            //Arrange
            MimeMessage message = mock(MimeMessage.class);

            when(templateEngine.process(anyString(), any(Context.class))).thenReturn("htmlBody");
            when(mailSender.createMimeMessage()).thenReturn(message);

            //Act & Assert
            emailService.sendVerificationEmail("teste@email.com", "123456");

            verify(mailSender).send(any(MimeMessage.class));
        }

        @Test
        void should_throw_email_exception_when_sending_email_fails() throws MessagingException {
            //Arrange
            MimeMessage message = mock(MimeMessage.class);

            when(templateEngine.process(anyString(), any(Context.class))).thenReturn("htmlBody");
            when(mailSender.createMimeMessage()).thenReturn(message);
            doThrow(MessagingException.class).when(message).setFrom(any(Address.class));

            //Act & Assert
            assertThrows(EmailException.class, () -> emailService.sendVerificationEmail("teste@email.com", "123456"));

            verify(mailSender, times(0)).send(any(MimeMessage.class));
        }
    }
}
