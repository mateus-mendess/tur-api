package com.m2.tur.controller;


import com.m2.tur.dto.request.ResendVerificationRequest;
import com.m2.tur.dto.request.VerifyCodeRequest;
import com.m2.tur.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Email Verification", description = "Endpoint for verifying a user's registered email")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @Operation(summary = "Verify email", description = """
            Confirms the user's email address using the verification code
            sent after registration.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid code"),
            @ApiResponse(responseCode = "410", description = "Code has expired"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/verify")
    public ResponseEntity<Void> verifyEmail(@RequestBody @Valid VerifyCodeRequest request) {
        emailVerificationService.verifyEmail(request);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Resend email verification code", description = """
            Resends a new verification code to the provided email address.
            Use this endpoint when a user did not receive the initial email or if the previous code has expired.
            For security purposes, this endpoint may return a successful response even if the email is not registered or
            is already verified, to prevent email enumeration attacks.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Request processed"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerificationCode(@RequestBody @Valid ResendVerificationRequest request) {
        emailVerificationService.resendVerificationCode(request.email());

        return ResponseEntity.noContent().build();
    }
}
