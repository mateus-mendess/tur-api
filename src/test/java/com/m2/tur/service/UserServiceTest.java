package com.m2.tur.service;

import com.m2.tur.event.UserRegisteredEvent;
import com.m2.tur.factory.UserFactory;
import com.m2.tur.infra.exception.EmailAlreadyExistsException;
import com.m2.tur.mapper.UserMapper;
import com.m2.tur.dto.request.UserRequest;
import com.m2.tur.dto.response.UserResponse;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> captor;

    private UserRequest request;
    private User user;
    private UserResponse response;

    @BeforeEach
    void setUp() {
        request = UserFactory.createRequest();
        user = UserFactory.createEntity();
        response = UserFactory.createResponse();
    }

    @Nested
    class SaveEntity {
        @Test
        void should_save_entity_with_success() {
            //Arrange
            when(userMapper.toEntity(request)).thenReturn(user);
            when(passwordEncoder.encode(request.password())).thenReturn(request.password());
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toResponse(any(User.class))).thenReturn(response);

            //Act & Assert
            var result = userService.save(request);

            verify(userRepository).save(any(User.class));
            verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));

            assertSame(response, result);
        }

        @Test
        void should_throw_email_already_exists_exception_when_email_already_exists() {
            //Assert
            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            //Act & Assert
            assertThrows(EmailAlreadyExistsException.class, () -> userService.save(request));

            verify(userRepository, times(0)).save(any(User.class));
            verify(eventPublisher, times(0)).publishEvent(any(UserRegisteredEvent.class));

        }
    }
}
