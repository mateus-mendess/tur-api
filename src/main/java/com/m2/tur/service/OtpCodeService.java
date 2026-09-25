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
public class OtpCodeService {
    private final CacheManager cacheManager;

    @CachePut(cacheNames = "code", key = "#userId")
    public String generateOtpCode(UUID userId) {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }

     void verifyCode(UUID userId, String codeRequest) {
        Cache cache = cacheManager.getCache("code");
        String code = cache.get(userId, String.class);

        if (code == null) throw new OtpCodeExpiredException("Code expired");

        if (!codeRequest.equals(code)) throw new InvalidOtpCodeException("Invalid code");

        cache.evict(userId);
    }
}
