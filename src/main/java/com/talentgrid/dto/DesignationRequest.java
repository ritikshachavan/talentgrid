package com.talentgrid.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DesignationRequest(
        @NotBlank(message = "Title is required") @Size(max = 100) String title,
        @NotNull(message = "Level is required") @Min(1) @Max(10) Integer level) {}