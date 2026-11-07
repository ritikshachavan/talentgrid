package com.talentgrid.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.talentgrid.dto.CandidateScore;
import com.talentgrid.strategy.AvailabilityFirstStrategy;
import com.talentgrid.strategy.ExperienceFirstStrategy;
import com.talentgrid.strategy.WeightedSkillStrategy;

class ScoreCalculatorTest {

    // Java: min level 4, min 2 years, weight 10.  MySQL: min level 3, min 1 year, weight 5.
    private final List<RequirementItem> requirements = List.of(
            new RequirementItem(1L, "Java", 4, 2.0, 10),
            new RequirementItem(2L, "MySQL", 3, 1.0, 5));

    private CandidateProfile profile(Map<Long, SkillLevel> skills) {
        return new CandidateProfile(1L, "E1", "Test User", 5.0, 100, skills);
    }

    @Test
    void perfectMatchScores100() {
        CandidateScore s = ScoreCalculator.score(requirements, profile(Map.of(
                1L, new SkillLevel(5, 3.0),
                2L, new SkillLevel(4, 2.0))));
        assertEquals(100.0, s.matchPercent(), 0.01);
        assertTrue(s.missingSkills().isEmpty());
        assertTrue(s.weakSkills().isEmpty());
    }

    @Test
    void missingSkillScoresZeroForThatSkill() {
        CandidateScore s = ScoreCalculator.score(requirements, profile(Map.of(
                1L, new SkillLevel(5, 3.0))));
        assertEquals(66.7, s.matchPercent(), 0.01);       // 10 of 15 points
        assertEquals(List.of("MySQL"), s.missingSkills());
    }

    @Test
    void lowLevelGivesPartialScoreAndIsMarkedWeak() {
        // Java level 2 of 4 needed => half of 10 points = 5, MySQL perfect = 5 => 10 of 15
        CandidateScore s = ScoreCalculator.score(requirements, profile(Map.of(
                1L, new SkillLevel(2, 3.0),
                2L, new SkillLevel(3, 1.0))));
        assertEquals(66.7, s.matchPercent(), 0.01);
        assertEquals(List.of("Java"), s.weakSkills());
    }

    @Test
    void noSkillsScoresZero() {
        CandidateScore s = ScoreCalculator.score(requirements, profile(Map.of()));
        assertEquals(0.0, s.matchPercent(), 0.01);
        assertEquals(2, s.missingSkills().size());
    }

    @Test
    void strategiesRankDifferently() {
        CandidateScore a = new CandidateScore(1L, "A", "Ann", 90.0, 20, 3.0, List.of(), List.of());
        CandidateScore b = new CandidateScore(2L, "B", "Bob", 80.0, 100, 9.0, List.of(), List.of());
        List<CandidateScore> input = List.of(b, a);

        assertEquals("A", new WeightedSkillStrategy().rank(input).get(0).empCode());
        assertEquals("B", new AvailabilityFirstStrategy().rank(input).get(0).empCode());
        assertEquals("B", new ExperienceFirstStrategy().rank(input).get(0).empCode());
    }
}