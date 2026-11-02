package com.talentgrid.dto;

import java.time.LocalDate;
import com.talentgrid.entity.enums.EmployeeStatus;

public record EmployeeResponse(
        Long id, String empCode, String fullName, String email,
        LocalDate hireDate, Double experienceYears, EmployeeStatus status,
        Long departmentId, String departmentName,
        Long designationId, String designationTitle,
        Long managerId, String managerName) {}