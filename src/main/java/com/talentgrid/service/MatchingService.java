package com.talentgrid.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.talentgrid.dto.AllocatedPercent;
import com.talentgrid.dto.CandidateScore;
import com.talentgrid.dto.RecommendationResponse;
import com.talentgrid.entity.Employee;
import com.talentgrid.entity.EmployeeSkill;
import com.talentgrid.entity.Project;
import com.talentgrid.entity.enums.AllocationStatus;
import com.talentgrid.entity.enums.EmployeeStatus;
import com.talentgrid.exception.ResourceNotFoundException;
import com.talentgrid.matching.CandidateProfile;
import com.talentgrid.matching.RequirementItem;
import com.talentgrid.matching.ScoreCalculator;
import com.talentgrid.matching.SkillLevel;
import com.talentgrid.repository.MatchingRepository;
import com.talentgrid.repository.ProjectRepository;
import com.talentgrid.repository.ProjectRequiredSkillRepository;
import com.talentgrid.strategy.MatchingStrategy;
import jakarta.annotation.PreDestroy;

@Service
public class MatchingService {

    private final ProjectRepository projectRepository;
    private final ProjectRequiredSkillRepository requiredSkillRepository;
    private final MatchingRepository matchingRepository;
    private final Map<String, MatchingStrategy> strategies;

    // a small thread pool used for parallel scoring
    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    // Spring injects EVERY MatchingStrategy bean into this list
    public MatchingService(ProjectRepository projectRepository,
                           ProjectRequiredSkillRepository requiredSkillRepository,
                           MatchingRepository matchingRepository,
                           List<MatchingStrategy> strategyList) {
        this.projectRepository = projectRepository;
        this.requiredSkillRepository = requiredSkillRepository;
        this.matchingRepository = matchingRepository;
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(MatchingStrategy::code, s -> s));
    }

    @Transactional(readOnly = true)
    public RecommendationResponse recommend(Long projectId, int top, String strategyCode) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        MatchingStrategy strategy = strategies.get(strategyCode.toUpperCase());
        if (strategy == null)
            throw new IllegalArgumentException(
                    "Unknown strategy '" + strategyCode + "'. Use one of: " + strategies.keySet());

        int limit = Math.max(1, Math.min(top, 50));

        // 1) requirements of the project
        List<RequirementItem> requirements = requiredSkillRepository.findAllForProject(projectId)
                .stream()
                .map(rs -> new RequirementItem(rs.getSkill().getId(), rs.getSkill().getName(),
                        rs.getMinProficiency(), rs.getMinYears(), rs.getWeight()))
                .toList();
        if (requirements.isEmpty())
            throw new IllegalArgumentException(
                    "Project has no required skills. Add some before asking for recommendations.");

        // 2) load everything with 3 queries, then copy into plain objects
        List<CandidateProfile> profiles = buildProfiles();

        // 3) score every candidate in parallel
        List<CompletableFuture<CandidateScore>> futures = profiles.stream()
                .map(p -> CompletableFuture.supplyAsync(
                        () -> ScoreCalculator.score(requirements, p), pool))
                .toList();

        List<CandidateScore> scored = futures.stream()
                .map(CompletableFuture::join)
                .filter(s -> s.matchPercent() > 0 && s.availablePercent() > 0)
                .toList();

        // 4) rank with the chosen strategy and take the top N
        List<CandidateScore> top5 = strategy.rank(scored).stream().limit(limit).toList();

        return new RecommendationResponse(project.getId(), project.getName(), strategy.code(),
                requirements.size(), profiles.size(), top5);
    }

    private List<CandidateProfile> buildProfiles() {
        List<Employee> employees = matchingRepository.findEmployeesByStatus(EmployeeStatus.ACTIVE);

        Map<Long, Map<Long, SkillLevel>> skillMap = new HashMap<>();
        for (EmployeeSkill es : matchingRepository.findSkillsByEmployeeStatus(EmployeeStatus.ACTIVE)) {
            skillMap.computeIfAbsent(es.getEmployee().getId(), k -> new HashMap<>())
                    .put(es.getSkill().getId(),
                         new SkillLevel(es.getProficiency(), es.getYearsOfExperience()));
        }

        Map<Long, Long> allocated = new HashMap<>();
        for (AllocatedPercent ap : matchingRepository.sumAllocatedByEmployee(AllocationStatus.ACTIVE)) {
            allocated.put(ap.employeeId(), ap.percent());
        }

        List<CandidateProfile> profiles = new ArrayList<>();
        for (Employee e : employees) {
            long used = allocated.getOrDefault(e.getId(), 0L);
            int available = (int) Math.max(0, 100 - used);
            profiles.add(new CandidateProfile(
                    e.getId(), e.getEmpCode(), e.getFullName(), e.getExperienceYears(),
                    available, skillMap.getOrDefault(e.getId(), Map.of())));
        }
        return profiles;
    }

    @PreDestroy
    public void shutdownPool() {
        pool.shutdown();
    }
}