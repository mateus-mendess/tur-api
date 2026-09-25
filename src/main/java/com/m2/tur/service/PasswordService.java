package com.m2.tur.service;

import com.m2.tur.infra.exception.InvalidCurrentPasswordException;
import com.m2.tur.infra.exception.UnauthorizedException;
import com.m2.tur.model.dto.request.ChangePasswordRequest;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PasswordService {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = authService.getAuthenticatedUser()
                .orElseThrow(() -> new UnauthorizedException("User not logged in."));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user != null) {
            otpService.generateResetPasswordCode(user.getEmail());
        }
    }
}
