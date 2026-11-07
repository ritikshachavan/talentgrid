package com.talentgrid.dto;

import java.time.LocalDate;

public record EndingAllocationResponse(
        Long allocationId, String employeeName, String projectName,
        Integer allocationPercent, LocalDate endDate, long daysLeft) {}