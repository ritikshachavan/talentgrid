package com.talentgrid.dto;

import java.time.LocalDate;
import com.talentgrid.entity.enums.EmployeeStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record EmployeeRequest(
        @NotBlank(message = "Employee code is required") @Size(max = 20) String empCode,
        @NotBlank(message = "First name is required") @Size(max = 50) String firstName,
        @NotBlank(message = "Last name is required") @Size(max = 50) String lastName,
        @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email,
        @NotNull(message = "Hire date is required") @PastOrPresent LocalDate hireDate,
        @NotNull(message = "Experience is required") @DecimalMin("0.0") Double experienceYears,
        EmployeeStatus status,
        @NotNull(message = "departmentId is required") Long departmentId,
        @NotNull(message = "designationId is required") Long designationId,
        Long managerId) {}