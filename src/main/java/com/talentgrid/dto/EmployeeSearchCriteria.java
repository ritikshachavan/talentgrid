package com.talentgrid.dto;

import com.talentgrid.entity.enums.EmployeeStatus;

public record EmployeeSearchCriteria(
        Long departmentId,
        Long skillId,
        Integer minProficiency,
        EmployeeStatus status,
        String name,
        Double minExperience) {}