package com.m2.tur.service;

import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.model.dto.request.OtpCodeRequest;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EmailVerificationService {
    private final UserRepository userRepository;
    private final OtpService otpService;

    @Transactional
    public void verifyEmail(UUID userId, OtpCodeRequest request) {
        otpService.verifyCode(userId, request.otpCode());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setActive(true);
    }
}
