package com.talentgrid.dto;

import java.time.LocalDate;
import com.talentgrid.entity.enums.AllocationStatus;

public record AllocationResponse(
        Long id, Long employeeId, String employeeName,
        Long projectId, String projectName, String roleName,
        Integer allocationPercent, LocalDate startDate, LocalDate endDate,
        AllocationStatus status, Long version) {}