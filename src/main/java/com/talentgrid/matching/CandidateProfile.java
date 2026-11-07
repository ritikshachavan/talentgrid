package com.talentgrid.matching;

import java.util.Map;

public record CandidateProfile(
        Long employeeId, String empCode, String fullName,
        double experienceYears, int availablePercent,
        Map<Long, SkillLevel> skills) {}