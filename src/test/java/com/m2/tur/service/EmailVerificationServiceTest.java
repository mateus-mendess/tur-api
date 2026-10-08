package com.m2.tur.service;

import com.m2.tur.factory.UserFactory;
import com.m2.tur.core.exception.InvalidOtpCodeException;
import com.m2.tur.core.exception.NotFoundException;
import com.m2.tur.dto.request.VerifyCodeRequest;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpCodeService otpCodeService;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    private User user;
    private VerifyCodeRequest codeRequest;

    @BeforeEach
    void setUp() {
        user = UserFactory.createEntity();
        user.setActive(false);
        codeRequest = new VerifyCodeRequest(user.getEmail(), "123456");
    }

    @Nested
    class VerifyEmail {
        @Test
        void should_successfully_validate_email() {
            //Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

            //Act & Assert
            emailVerificationService.verifyEmail(codeRequest);

            verify(otpCodeService).verifyUserRegistrationCode(any(UUID.class), anyString());

            assertTrue(user.getActive());
        }

        @Test
        void should_throw_not_found_exception_when_user_does_not_found() {
            //Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            //Act & Assert
            assertThrows(NotFoundException.class, () -> emailVerificationService.verifyEmail(codeRequest));
            verify(otpCodeService, times(0)).verifyUserRegistrationCode(any(UUID.class), anyString());
        }

        @Test
        void should_throw_invalid_code_exception_when_code_is_invalid() {
            //Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
            doThrow(InvalidOtpCodeException.class).when(otpCodeService).verifyUserRegistrationCode(any(UUID.class), anyString());

            //Act & Assert
            assertThrows(InvalidOtpCodeException.class, () -> emailVerificationService.verifyEmail(codeRequest));

            assertFalse(user.getActive());
        }
    }
}
