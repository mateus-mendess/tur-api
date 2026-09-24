package com.m2.tur.service;

import com.m2.tur.infra.exception.InvalidOtpCodeException;
import com.m2.tur.infra.exception.OtpCodeExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OtpService {
    private final CacheManager cacheManager;

    @CachePut(cacheNames = "otpCode", key = "#userId")
    public String generateOtpCode(UUID userId) {
        SecureRandom random = new SecureRandom();

        return String.format("%06d", random.nextInt(10000));
    }

    public void verifyCode(UUID userId, String codeRequest) {
        Cache cache = cacheManager.getCache("otpCode");
        String code = cache.get(userId, String.class);

        if (code == null) throw new OtpCodeExpiredException("Code expired");

        if (!codeRequest.equals(code)) throw new InvalidOtpCodeException("Invalid code");

        cache.evict(userId);
    }
}
