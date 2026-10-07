package com.m2.tur.service;

import com.m2.tur.config.CookieUtil;
import com.m2.tur.factory.UserFactory;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.infra.security.jwt.JwtService;
import com.m2.tur.model.dto.request.AuthenticationRequest;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CookieUtil cookieUtil;

    @InjectMocks
    private AuthService authService;

    private AuthenticationRequest authenticationRequest;
    private Authentication authentication;
    private HttpServletResponse httpServletResponse;
    private User user;

    @BeforeEach
    public void setUp() {
        authenticationRequest = new AuthenticationRequest("teste@gmail.com", "Password123@");
        authentication = new UsernamePasswordAuthenticationToken(authenticationRequest.email(), authenticationRequest.password());
        httpServletResponse = mock(HttpServletResponse.class);
        user = UserFactory.createEntity();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class Authenticate {
        @Test
        void should_return_token_when_credentials_are_valid() {
            //Arrange
            when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
            when(jwtService.generateToken(authentication)).thenReturn("token");

            //Act & Assert
            authService.authenticate(authenticationRequest, httpServletResponse);

            verify(jwtService).generateToken(any(Authentication.class));
            verify(cookieUtil).createCookie(any(HttpServletResponse.class), any(String.class));
        }

        @Test
        void should_throw_authentication_exception_when_credentials_are_invalid() {
            //Arrange
            when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException("invalid Credential"));

            //Act & Assert
            assertThrows(BadCredentialsException.class, () -> authService.authenticate(authenticationRequest, httpServletResponse));

            verify(jwtService, times(0)).generateToken(any(Authentication.class));
            verify(cookieUtil, times(0)).createCookie(any(HttpServletResponse.class), any(String.class));
        }
    }

    @Nested
    class GetAuthenticatedUser {
        @Test
        void should_return_user_when_token_are_valid() {
            //Arrange
            Jwt jwt = Jwt.withTokenValue("token")
                    .header("alg", "RS256")
                    .subject(user.getId().toString())
                    .build();

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new JwtAuthenticationToken(jwt));
            SecurityContextHolder.setContext(context);

            when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

            //Act & Assert
            var result = authService.getAuthenticatedUser();

            assertTrue(result.isPresent());
            assertEquals(user, result.get());
        }

        @Test
        void should_return_empty_when_token_are_invalid() {
            //Arrange
            authentication = new UsernamePasswordAuthenticationToken("teste", null, List.of());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            //Act & Assert
            var result = authService.getAuthenticatedUser();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class GetMe {
        @Test
        void should_return_user_id_when_authenticated() {
            //Arrange
            AuthService spyService = spy(authService);

            doReturn(Optional.of(user)).when(spyService).getAuthenticatedUser();

            //Act & Assert
            var result = spyService.getMe();

            assertEquals(user.getId(), result);


        }

        @Test
        void should_return_empty_when_user_not_authenticated() {
            //Arrange
            AuthService spyService = spy(authService);

            doReturn(Optional.empty()).when(spyService).getAuthenticatedUser();

            //Act & Assert
            assertThrows(UnauthorizedException.class, spyService::getMe);
        }
    }
}
