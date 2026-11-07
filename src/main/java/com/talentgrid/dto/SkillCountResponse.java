package com.talentgrid.dto;

public record SkillCountResponse(
        String skillName, String categoryName, Long employeeCount, Double averageProficiency) {}