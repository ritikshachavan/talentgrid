package com.talentgrid.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EmployeeSkillRequest(
        @NotNull(message = "skillId is required") Long skillId,
        @NotNull(message = "Proficiency is required") @Min(1) @Max(5) Integer proficiency,
        @NotNull(message = "Years of experience is required") @DecimalMin("0.0") Double yearsOfExperience) {}