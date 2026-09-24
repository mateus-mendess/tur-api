package com.m2.tur.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OtpCodeRequest(
        @NotBlank
        @Size(min = 6, max = 6)
        String otpCode
) {}
