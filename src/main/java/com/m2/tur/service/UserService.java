package com.m2.tur.service;

import com.m2.tur.event.UserRegisteredEvent;
import com.m2.tur.infra.exception.EmailAlreadyExistsException;
import com.m2.tur.infra.exception.InvalidCurrentPasswordException;
import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.mapper.UserMapper;
import com.m2.tur.model.dto.request.ChangePasswordRequest;
import com.m2.tur.model.dto.request.OtpCodeRequest;
import com.m2.tur.model.dto.request.UserRequest;
import com.m2.tur.model.dto.response.UserResponse;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    @Transactional
    public UserResponse save(UserRequest request) {
        validate(request);

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(user.getId(), user.getEmail()));

        return userMapper.toResponse(user);
    }
    private void validate(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists.", "email");
        }
    }

}
