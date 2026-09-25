package com.m2.tur.controller;

import com.m2.tur.model.dto.request.ChangePasswordRequest;
import com.m2.tur.model.dto.request.ForgotPasswordRequest;
import com.m2.tur.model.dto.request.ResetPasswordRequest;
import com.m2.tur.model.dto.request.VerifyCodeRequest;
import com.m2.tur.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Validated
public class PasswordController {
    private final PasswordService passwordService;

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        passwordService.changePassword(request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        passwordService.requestPasswordReset(request.email());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password/verify")
    public ResponseEntity<String> verifyResetPassword(@RequestBody @Valid VerifyCodeRequest request) {
        return ResponseEntity.ok().body(passwordService.verifyResetCode(request));
    }
    @PatchMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        passwordService.resetPassword(request);

        return ResponseEntity.noContent().build();
    }
}
