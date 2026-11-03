package com.talentgrid.dto;

import java.time.LocalDate;
import com.talentgrid.entity.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotBlank(message = "Name is required") @Size(max = 150) String name,
        @Size(max = 500) String description,
        LocalDate startDate,
        LocalDate endDate,
        ProjectStatus status,
        @NotNull(message = "departmentId is required") Long departmentId) {}