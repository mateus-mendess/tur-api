package com.m2.tur.service;

import com.m2.tur.infra.exception.InvalidOtpCodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OtpCodeService {
    private static final String USER_REGISTRATION_CACHE = "user-registration-cache";
    private static final String PASSWORD_RESET_CACHE = "password-reset-cache";
    private static final int MAX_ATTEMPTS = 3;

    private final CacheManager cacheManager;

    public String generateUserRegistrationCode(UUID userId) {
        String code = generateOtpCode();

        OtpData otpData = new OtpData(code);
        cacheManager.getCache(USER_REGISTRATION_CACHE).put(userId, otpData);

        return code;
    }

    public String generatePasswordResetCode(UUID userId) {
        String code = generateOtpCode();

        OtpData otpData = new OtpData(code);
        cacheManager.getCache(PASSWORD_RESET_CACHE).put(userId, otpData);

        return code;
    }

    public void verifyUserRegistrationCode(UUID userId, String code) {
        verifyCode(userId, code, USER_REGISTRATION_CACHE);
    }

    public void verifyPasswordResetCode(UUID userId, String code) {
        verifyCode(userId, code, PASSWORD_RESET_CACHE);
    }

     private void verifyCode(UUID userId, String inputCode, String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        OtpData otp = cache.get(userId, OtpData.class);

        if (otp == null || !otp.matches(inputCode)) throw new InvalidOtpCodeException("Code expired or invalid");

        if (!otp.matches(inputCode)) {
            if (otp.registerFailure() >= MAX_ATTEMPTS) {
                cache.evict(userId);
            }

            throw new InvalidOtpCodeException("Code expired or invalid");
        }

        if (!cache.evictIfPresent(userId)) {
            throw new InvalidOtpCodeException("Code expired or invalid");
        }
    }

    private String generateOtpCode() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }
}
