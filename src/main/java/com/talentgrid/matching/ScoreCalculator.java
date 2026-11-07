package com.talentgrid.matching;

import java.util.ArrayList;
import java.util.List;
import com.talentgrid.dto.CandidateScore;

public final class ScoreCalculator {

    private ScoreCalculator() {}

    public static CandidateScore score(List<RequirementItem> requirements, CandidateProfile c) {
        double maxScore = 0;
        double gained = 0;
        List<String> missing = new ArrayList<>();
        List<String> weak = new ArrayList<>();

        for (RequirementItem r : requirements) {
            maxScore += r.weight();
            SkillLevel have = c.skills().get(r.skillId());

            if (have == null) {
                missing.add(r.skillName());
                continue;
            }

            double levelRatio = Math.min((double) have.proficiency() / r.minProficiency(), 1.0);
            double yearsRatio = (r.minYears() <= 0)
                    ? 1.0
                    : Math.min(have.years() / r.minYears(), 1.0);
            gained += r.weight() * levelRatio * yearsRatio;

            if (have.proficiency() < r.minProficiency() || have.years() < r.minYears()) {
                weak.add(r.skillName());
            }
        }

        double percent = (maxScore == 0) ? 0 : (gained / maxScore) * 100.0;
        percent = Math.round(percent * 10.0) / 10.0;

        return new CandidateScore(c.employeeId(), c.empCode(), c.fullName(),
                percent, c.availablePercent(), c.experienceYears(), missing, weak);
    }
}