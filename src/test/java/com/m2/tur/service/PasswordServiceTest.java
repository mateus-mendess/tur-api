package com.m2.tur.service;

import com.m2.tur.factory.UserFactory;
import com.m2.tur.infra.exception.InvalidCurrentPasswordException;
import com.m2.tur.infra.exception.InvalidOtpCodeException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.model.dto.request.ChangePasswordRequest;
import com.m2.tur.model.dto.request.ResetPasswordRequest;
import com.m2.tur.model.dto.request.VerifyCodeRequest;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OtpCodeService otpCodeService;

    @Mock
    private EmailService emailService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    Cache cache;

    @InjectMocks
    private PasswordService passwordService;

    private User user;
    private ChangePasswordRequest changePasswordRequest;
    private VerifyCodeRequest verifyCodeRequest;
    private ResetPasswordRequest resetPasswordRequest;

    @BeforeEach
    public void setUp() {
        user = UserFactory.createEntity();
        changePasswordRequest = new ChangePasswordRequest("12345", "123456", "123456");
        verifyCodeRequest = new VerifyCodeRequest(user.getEmail(), "123456");
        resetPasswordRequest = new ResetPasswordRequest(UUID.randomUUID().toString(), "123456", "123456");
    }

    @Nested
    class ChangePassword {
        @Test
        void should_successfully_change_the_password() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

            //Act & Assert
            passwordService.changePassword(changePasswordRequest);

            assertEquals("hashedPassword", user.getPassword());
        }

        @Test
        void should_throw_unauthorized_exception_when_user_not_authenticated() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(UnauthorizedException.class, () -> passwordService.changePassword(changePasswordRequest));

            verifyNoInteractions(passwordEncoder);
        }

        @Test
        void should_throw_invalid_current_password_exception_when_current_password_is_incorrect() {
            //Arrange
            when(authService.getAuthenticatedUser()).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            //Act & Assert
            assertThrows(InvalidCurrentPasswordException.class, () -> passwordService.changePassword(changePasswordRequest));

            verify(passwordEncoder, times(0)).encode(anyString());

            assertNotEquals(changePasswordRequest.newPassword(), user.getPassword());
        }

    }

    @Nested
    class RequestPasswordReset {
        @Test
        void should_successfully_request_a_password_reset() {
            //Arrange
            String email = user.getEmail();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(otpCodeService.generatePasswordResetCode(any(UUID.class))).thenReturn("123456");

            //Act & Assert
            passwordService.requestPasswordReset(email);

            verify(emailService).sendResetPasswordEmail(anyString(), anyString());
        }

        @Test
        void should_not_generate_code_when_the_user_is_not_found() {
            //Arrange
            String email = user.getEmail();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            //Act & Assert
            passwordService.requestPasswordReset(email);

            verify(otpCodeService, times(0)).generatePasswordResetCode(any(UUID.class));
            verify(emailService, times(0)).sendResetPasswordEmail(anyString(), anyString());
        }
    }

    @Nested
    class  VerifyResetCode {
        @Test
        void should_successfully_validate_the_code() {
            //Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(cacheManager.getCache(anyString())).thenReturn(cache);

            //Act & Assert
            var result = passwordService.verifyResetCode(verifyCodeRequest);

            verify(otpCodeService).verifyPasswordResetCode(any(UUID.class), anyString());
            verify(cache).put(anyString(), anyString());

            assertNotNull(result);
        }

        @Test
        void should_invalidate_code_when_email_is_not_found() {
            //Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(InvalidOtpCodeException.class, () -> passwordService.verifyResetCode(verifyCodeRequest));

            verify(otpCodeService, times(0)).verifyPasswordResetCode(any(UUID.class), anyString());
            verify(cache, times(0)).put(anyString(), anyString());
        }

        @Test
        void should_not_cache_token_when_otp_verification_fails() {
            //Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            doThrow(InvalidOtpCodeException.class).when(otpCodeService).verifyPasswordResetCode(any(UUID.class), anyString());

            //Act & Assert
            assertThrows(InvalidOtpCodeException.class, () -> passwordService.verifyResetCode(verifyCodeRequest));

            verify(otpCodeService).verifyPasswordResetCode(any(UUID.class), anyString());
            verify(cache, times(0)).put(any(UUID.class), anyString());
        }
    }

    @Nested
    class ResetPassword {
        @Test
        void should_successfully_reset_the_password() {
            //Arrange
            String email = user.getEmail();

            when(cacheManager.getCache(anyString())).thenReturn(cache);
            when(cache.get(anyString(), eq(String.class))).thenReturn(email);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

            //Act & Assert
            passwordService.resetPassword(resetPasswordRequest);

            verify(cache).evict(anyString());

            assertEquals("hashedPassword", user.getPassword());
        }

        @Test
        void should_throw_not_found_exception_when_user_not_found() {
            String email = user.getEmail();

            //Arrange
            when(cacheManager.getCache(anyString())).thenReturn(cache);
            when(cache.get(anyString(), eq(String.class))).thenReturn(email);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(NotFoundException.class, () -> passwordService.resetPassword(resetPasswordRequest));

            verify(passwordEncoder, times(0)).encode(anyString());
            verify(cache, times(0)).evict(anyString());
        }
    }
}
