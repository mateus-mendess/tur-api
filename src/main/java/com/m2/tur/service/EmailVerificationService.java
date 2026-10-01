package com.m2.tur.service;

import com.m2.tur.infra.exception.NotFoundException;
import com.m2.tur.model.dto.request.VerifyCodeRequest;
import com.m2.tur.model.entity.User;
import com.m2.tur.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class EmailVerificationService {
    private final UserRepository userRepository;
    private final OtpCodeService otpService;

    @Transactional
    public void verifyEmail(VerifyCodeRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("User not found"));

        otpService.verifyUserRegistrationCode(user.getId(), request.code());

        user.setActive(true);
    }
}
