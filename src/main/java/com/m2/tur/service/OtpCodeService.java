package com.m2.tur.service;

import com.m2.tur.infra.exception.InvalidOtpCodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OtpCodeService {
    private static final String USER_REGISTRATION_CACHE = "user-registration-cache";
    private static final String PASSWORD_RESET_CACHE = "password-reset-cache";

    private final CacheManager cacheManager;

    @CachePut(cacheNames = USER_REGISTRATION_CACHE, key = "#userId")
    public String generateUserRegistrationCode(UUID userId) {
        return generateOtpCode();
    }

    @CachePut(cacheNames = PASSWORD_RESET_CACHE, key = "#userId")
    public String generatePasswordResetCode(UUID userId) {
        return generateOtpCode();
    }

    public void verifyUserRegistrationCode(UUID userId, String code) {
        verifyCode(userId, code,  USER_REGISTRATION_CACHE);
    }

    public void verifyPasswordResetCode(UUID userId, String code) {
        verifyCode(userId, code,  PASSWORD_RESET_CACHE);
    }

     private void verifyCode(UUID userId, String codeRequest, String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        String code = cache.get(userId, String.class);

        if (code == null || !code.equals(codeRequest)) throw new InvalidOtpCodeException("Code expired or invalid");

        cache.evict(userId);
    }

    private String generateOtpCode() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }
}
