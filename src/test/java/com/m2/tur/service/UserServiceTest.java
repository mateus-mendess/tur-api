package com.m2.tur.service;

import com.m2.tur.factory.UserFactory;
import com.m2.tur.infra.exception.EmailAlreadyExistsException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.mapper.UserMapper;
import com.m2.tur.model.dto.request.UserRequest;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> captor;

    @Nested
    class FindEntityById {
        @Test
        void should_return_entity_with_success() {
            //Arrange
            User user = UserFactory.createEntity();

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

            //Act & Assert
            var result = assertDoesNotThrow(() -> userService.findEntityById(user.getId()));
            assertEquals(user.getEmail(), result.getEmail());
        }

        @Test
        void should_return_NotFound_when_entity_not_found() {
            //Arrange
            UUID id = UUID.randomUUID();

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            //Act e Assert
            assertThrows(NotFoundException.class, () -> userService.findEntityById(id));
        }
    }

    @Nested
    class SaveEntity {
        @Test
        void should_save_entity_with_success() {
            //Arrange
            UserRequest request = UserFactory.createRequest();
            User user = UserFactory.createEntity();

            when(userMapper.toEntity(request)).thenReturn(user);
            when(passwordEncoder.encode(request.password())).thenReturn(request.password());
            when(userRepository.save(user)).thenReturn(user);

            //Act & Assert
            assertDoesNotThrow(() -> userService.save(request));

            verify(userMapper).toEntity(request);
            verify(passwordEncoder).encode(request.password());
            verify(userRepository).save(captor.capture());

            var captured = captor.getValue();
            assertEquals(user.getId(), captured.getId());
        }

        @Test
        void should_throw_EmailAlreadyExists_when_email_already_exists() {
            //Assert
            UserRequest request = UserFactory.createRequest();

            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            //Act & Assert
            assertThrows(EmailAlreadyExistsException.class, () -> userService.save(request));

            verify(userRepository, times(0)).save(any(User.class));

        }
    }
}
