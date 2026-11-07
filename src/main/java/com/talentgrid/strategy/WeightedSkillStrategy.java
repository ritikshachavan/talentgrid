package com.talentgrid.strategy;

import java.util.Comparator;
import org.springframework.stereotype.Component;
import com.talentgrid.dto.CandidateScore;

@Component
public class WeightedSkillStrategy extends AbstractMatchingStrategy {

    @Override
    public String code() { return "WEIGHTED"; }

    // best skill match first, then most free capacity
    @Override
    protected Comparator<CandidateScore> comparator() {
        return Comparator.comparingDouble(CandidateScore::matchPercent).reversed()
                .thenComparing(Comparator.comparingInt(CandidateScore::availablePercent).reversed());
    }
}