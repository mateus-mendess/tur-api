package com.m2.tur.controller;

import com.m2.tur.model.dto.request.ChangePasswordRequest;
import com.m2.tur.model.dto.request.ForgotPasswordRequest;
import com.m2.tur.model.dto.request.ResetPasswordRequest;
import com.m2.tur.model.dto.request.VerifyCodeRequest;
import com.m2.tur.service.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Password", description = "Endpoints for changing and recovering user passwords")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class PasswordController {
    private final PasswordService passwordService;

    @Operation(summary = "Change password", description = """
            Changes the password of the currently authenticated user.
            Requires the current password for confirmation.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "401", description = "Current password does not match or Unauthenticated user"),
            @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        passwordService.changePassword(request);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Request password reset", description = """
            Starts the password recovery flow by sending a verification code
            to the given email, if an account with that email exists.
            Always returns 200, regardless of whether the email is registered,
            to prevent user enumeration.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Request processed; a code was sent if the email is registered"),
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        passwordService.requestPasswordReset(request.email());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verify password reset code", description = """
            Validates the code sent to the user's email during the password
            reset flow. Returns a short-lived, single-use token required to
            complete the password reset.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Code verified; reset token returned"),
            @ApiResponse(responseCode = "400", description = "Invalid code or email"),
            @ApiResponse(responseCode = "410", description = "Code has expired")
    })
    @PostMapping("/forgot-password/verify")
    public ResponseEntity<String> verifyResetPassword(@RequestBody @Valid VerifyCodeRequest request) {
        return ResponseEntity.ok().body(passwordService.verifyResetCode(request));
    }

    @Operation(summary = "Reset password", description = """
            Sets a new password using the token obtained from the reset
            code verification step.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password reset successfully"),
            @ApiResponse(responseCode = "404", description = "Token not found, invalid or expired")
    })
    @PatchMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        passwordService.resetPassword(request);

        return ResponseEntity.noContent().build();
    }
}
