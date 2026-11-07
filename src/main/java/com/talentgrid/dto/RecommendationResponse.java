package com.talentgrid.dto;

import java.util.List;

public record RecommendationResponse(
        Long projectId, String projectName, String strategy,
        int requiredSkillCount, int candidatesConsidered,
        List<CandidateScore> candidates) {}