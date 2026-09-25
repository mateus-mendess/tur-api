package com.m2.tur.controller;


import com.m2.tur.model.dto.request.OtpCodeRequest;
import com.m2.tur.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @PatchMapping("/{id}/verify")
    public ResponseEntity<Void> verifyEmail(@PathVariable UUID id, @RequestBody @Valid OtpCodeRequest request) {
        emailVerificationService.verifyEmail(id, request);

        return ResponseEntity.noContent().build();
    }
}
