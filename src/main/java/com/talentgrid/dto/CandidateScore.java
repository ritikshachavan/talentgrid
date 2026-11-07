package com.talentgrid.dto;

import java.util.List;

public record CandidateScore(
        Long employeeId, String empCode, String fullName,
        double matchPercent, int availablePercent, double experienceYears,
        List<String> missingSkills, List<String> weakSkills) {}