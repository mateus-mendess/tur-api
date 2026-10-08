package com.m2.tur.service;

import com.m2.tur.core.exception.NotFoundException;
import com.m2.tur.dto.request.VerifyCodeRequest;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EmailVerificationService {
    private final UserRepository userRepository;
    private final OtpCodeService otpService;
    private final EmailService emailService;

    @Transactional
    @CacheEvict(cacheNames = "stats-cache")
    public void verifyEmail(VerifyCodeRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("User not found"));

        otpService.verifyUserRegistrationCode(user.getId(), request.code());

        user.setActive(true);
    }

    public void sendVerificationCode(UUID userId, String email) {
        String code = otpService.generateUserRegistrationCode(userId);

        emailService.sendVerificationEmail(email, code);
    }

    public void resendVerificationCode(String email) {
        userRepository.findByEmail(email)
                .filter(user -> !Boolean.TRUE.equals(user.getActive()))
                .ifPresent(user -> sendVerificationCode(user.getId(), email));

    }
}
