package com.talentgrid.matching;

public record RequirementItem(
        Long skillId, String skillName, int minProficiency, double minYears, int weight) {}