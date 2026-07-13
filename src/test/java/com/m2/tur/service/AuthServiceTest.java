package com.m2.tur.service;

import com.m2.tur.infra.security.jwt.JwtService;
import com.m2.tur.model.dto.request.AuthenticationRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Nested
    class Authenticate {
        @Test
        void should_return_token_when_credentials_are_valid() {
            //Arrange
            AuthenticationRequest request = new AuthenticationRequest("test@email.com", "Password123@");
            Authentication authentication = mock(Authentication.class);

            when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password())))
                    .thenReturn(authentication);
            when(jwtService.generateToken(authentication)).thenReturn("token");

            //Act & Assert
            assertDoesNotThrow(() -> authService.authenticate(request));
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtService).generateToken(any(Authentication.class));
        }

        @Test
        void should_throw_authentication_exception_when_credentials_are_invalid() {
            //Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("invalid Credential"));

            //Act & Assert
            assertThrows(BadCredentialsException.class, () -> authService.authenticate(new AuthenticationRequest("test@gmail.com", "Password123@")));
            verify(jwtService, times(0)).generateToken(any(Authentication.class));
        }
    }

    @Nested
    class GetAuthenticatedUser {
        @Test
        void should_return_user_when_token_are_valid() {
            //Arrange
            SecurityContextHolder securityContext = mock(SecurityContextHolder.class);


            //Act & Assert
        }
    }
}
