package com.talentgrid.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RequiredSkillRequest(
        @NotNull(message = "skillId is required") Long skillId,
        @NotNull(message = "minProficiency is required") @Min(1) @Max(5) Integer minProficiency,
        @NotNull(message = "minYears is required") @DecimalMin("0.0") Double minYears,
        @NotNull(message = "weight is required") @Min(1) @Max(10) Integer weight) {}