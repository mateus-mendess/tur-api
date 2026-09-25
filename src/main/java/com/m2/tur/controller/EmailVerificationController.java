package com.m2.tur.controller;


import com.m2.tur.model.dto.request.VerifyCodeRequest;
import com.m2.tur.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @PatchMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestBody @Valid VerifyCodeRequest request) {
        emailVerificationService.verifyEmail(request);

        return ResponseEntity.noContent().build();
    }
}
