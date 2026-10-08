package com.m2.tur.service;

import com.m2.tur.core.exception.InvalidOtpCodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OtpServiceTest {
    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private OtpCodeService otpCodeService;

    private UUID userId;
    private String code;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        code = "654321";
    }

    @Nested
    class VerifyCode {
        @Test
        void should_successfully_validate_the_code() {
            //Arrange
            when(cacheManager.getCache(anyString())).thenReturn(cache);
            when(cache.get(any(UUID.class), eq(String.class))).thenReturn(code);

            //Act & Assert
            otpCodeService.verifyUserRegistrationCode(userId, code);

            verify(cache).evict(any(UUID.class));
        }

        @Test
        void should_throw_invalid_otp_code_exception_when_code_is_not_in_cache() {
            //Arrange
            when(cacheManager.getCache(anyString())).thenReturn(cache);
            when(cache.get(any(UUID.class), eq(String.class))).thenReturn(null);

            //Act & Assert
            assertThrows(InvalidOtpCodeException.class, () -> otpCodeService.verifyUserRegistrationCode(userId, code));

            verify(cache, times(0)).evict(any(UUID.class));
        }

        @Test
        void should_throw_invalid_otp_code_exception_when_code_does_not_match() {
            //Arrange
            String codeRequest = "123456";

            when(cacheManager.getCache(anyString())).thenReturn(cache);
            when(cache.get(any(UUID.class), eq(String.class))).thenReturn(code);

            //Act & Assert
            assertThrows(InvalidOtpCodeException.class, () -> otpCodeService.verifyUserRegistrationCode(userId, codeRequest));

            verify(cache, times(0)).evict(any(UUID.class));
        }
    }

}
