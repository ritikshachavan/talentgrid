package com.talentgrid.dto;

import java.time.LocalDate;
import com.talentgrid.entity.enums.ProjectStatus;

public record ProjectResponse(
        Long id, String name, String description,
        LocalDate startDate, LocalDate endDate, ProjectStatus status,
        Long departmentId, String departmentName) {}