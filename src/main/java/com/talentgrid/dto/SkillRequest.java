package com.talentgrid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SkillRequest(
        @NotBlank(message = "Name is required") @Size(max = 100) String name,
        @NotNull(message = "categoryId is required") Long categoryId) {}