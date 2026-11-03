package com.talentgrid.dto;

import java.time.LocalDate;
import com.talentgrid.entity.enums.AllocationStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AllocationRequest(
        @NotNull(message = "employeeId is required") Long employeeId,
        @NotNull(message = "projectId is required") Long projectId,
        @Size(max = 80) String roleName,
        @NotNull(message = "allocationPercent is required") @Min(1) @Max(100) Integer allocationPercent,
        @NotNull(message = "startDate is required") LocalDate startDate,
        LocalDate endDate,
        AllocationStatus status,
        Long version) {}