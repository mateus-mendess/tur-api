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
    private final AuthService authService;
    private final ApplicationEventPublisher eventPublisher;
    private final OtpService otpService;

    @Transactional
    public UserResponse save(UserRequest request) {
        validate(request);

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(user.getId(), user.getEmail()));

        return userMapper.toResponse(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User not logged in."));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    @Transactional
    public void verifyEmail(UUID userId, OtpCodeRequest request) {
        otpService.verifyCode(userId, request.otpCode());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setActive(true);
    }

    private void validate(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists.", "email");
        }
    }

}
