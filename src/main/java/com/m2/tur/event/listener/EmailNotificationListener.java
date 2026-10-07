package com.m2.tur.event.listener;

import com.m2.tur.event.UserRegisteredEvent;
import com.m2.tur.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class EmailNotificationListener {
    private final EmailVerificationService emailVerificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegisteredEvent event) {
        emailVerificationService.sendVerificationCode(event.userId(), event.email());
    }
}
