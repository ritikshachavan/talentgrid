package com.talentgrid.strategy;

import java.util.Comparator;
import org.springframework.stereotype.Component;
import com.talentgrid.dto.CandidateScore;

@Component
public class AvailabilityFirstStrategy extends AbstractMatchingStrategy {

    @Override
    public String code() { return "AVAILABILITY_FIRST"; }

    // most free capacity first, then best skill match
    @Override
    protected Comparator<CandidateScore> comparator() {
        return Comparator.comparingInt(CandidateScore::availablePercent).reversed()
                .thenComparing(Comparator.comparingDouble(CandidateScore::matchPercent).reversed());
    }
}