package com.talentgrid.strategy;

import java.util.Comparator;
import java.util.List;
import com.talentgrid.dto.CandidateScore;

public abstract class AbstractMatchingStrategy implements MatchingStrategy {

    // each strategy only decides HOW to compare two candidates
    protected abstract Comparator<CandidateScore> comparator();

    @Override
    public List<CandidateScore> rank(List<CandidateScore> candidates) {
        return candidates.stream().sorted(comparator()).toList();
    }
}