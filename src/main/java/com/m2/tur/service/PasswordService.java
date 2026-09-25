package com.m2.tur.service;

import com.m2.tur.infra.exception.InvalidCurrentPasswordException;
import com.m2.tur.infra.exception.InvalidOtpCodeException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.model.dto.request.ChangePasswordRequest;
import com.m2.tur.model.dto.request.ResetPasswordRequest;
import com.m2.tur.model.dto.request.VerifyCodeRequest;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PasswordService {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final OtpCodeService otpService;
    private final EmailService emailService;
    private final CacheManager cacheManager;

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User not logged in"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    String code = otpService.generateOtpCode(user.getId());
                    emailService.sendResetPasswordEmail(email, code);
                });
    }

    public String verifyResetCode(VerifyCodeRequest request) {
        User user = userRepository.findByEmail(request.email())
                        .orElseThrow(() -> new InvalidOtpCodeException("Invalid code"));

        otpService.verifyCode(user.getId(), request.code());

        String token = UUID.randomUUID().toString();

        cacheManager.getCache("password-reset-token").put(token, user.getEmail());

        return token;
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        Cache cache = cacheManager.getCache("password-reset-token");
        String email = cache.get(request.token(), String.class);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }
}
