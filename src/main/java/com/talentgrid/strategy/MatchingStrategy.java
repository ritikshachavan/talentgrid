package com.talentgrid.strategy;

import java.util.List;
import com.talentgrid.dto.CandidateScore;

public interface MatchingStrategy {
    String code();
    List<CandidateScore> rank(List<CandidateScore> candidates);
}