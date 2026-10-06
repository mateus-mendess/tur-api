package com.m2.tur.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @Schema(description = "Category name. Only letters and spaces allowed.", example = "Beaches")
        @Size(min = 5, max = 30)
        @NotBlank(message = "name required")
        @Pattern(regexp = "[A-Za-zÀ-ÖØ-öø-ÿ ]$",
        message = "Invalid name")
        String name
) {}
