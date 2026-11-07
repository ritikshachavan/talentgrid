package com.talentgrid.strategy;

import java.util.Comparator;
import org.springframework.stereotype.Component;
import com.talentgrid.dto.CandidateScore;

@Component
public class ExperienceFirstStrategy extends AbstractMatchingStrategy {

    @Override
    public String code() { return "EXPERIENCE_FIRST"; }

    // most experienced first, then best skill match
    @Override
    protected Comparator<CandidateScore> comparator() {
        return Comparator.comparingDouble(CandidateScore::experienceYears).reversed()
                .thenComparing(Comparator.comparingDouble(CandidateScore::matchPercent).reversed());
    }
}