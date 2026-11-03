package com.talentgrid.dto;

public record EmployeeSkillResponse(
        Long employeeId, Long skillId, String skillName, String categoryName,
        Integer proficiency, Double yearsOfExperience) {}