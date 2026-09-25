package com.m2.tur.controller;

import com.m2.tur.model.dto.request.ChangePasswordRequest;
import com.m2.tur.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class PasswordController {
    private final PasswordService passwordService;

    @PatchMapping
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        passwordService.changePassword(request);

        return ResponseEntity.noContent().build();
    }
}
