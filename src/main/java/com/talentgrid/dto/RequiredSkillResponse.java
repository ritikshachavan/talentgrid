package com.talentgrid.dto;

public record RequiredSkillResponse(
        Long projectId, Long skillId, String skillName,
        Integer minProficiency, Double minYears, Integer weight) {}