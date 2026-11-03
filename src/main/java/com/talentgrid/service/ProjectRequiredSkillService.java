package com.talentgrid.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.talentgrid.dto.RequiredSkillRequest;
import com.talentgrid.dto.RequiredSkillResponse;
import com.talentgrid.dto.RequiredSkillUpdateRequest;
import com.talentgrid.entity.Project;
import com.talentgrid.entity.ProjectRequiredSkill;
import com.talentgrid.entity.ProjectRequiredSkillId;
import com.talentgrid.entity.Skill;
import com.talentgrid.exception.DuplicateResourceException;
import com.talentgrid.exception.ResourceNotFoundException;
import com.talentgrid.repository.ProjectRepository;
import com.talentgrid.repository.ProjectRequiredSkillRepository;
import com.talentgrid.repository.SkillRepository;

@Service
public class ProjectRequiredSkillService {

    private final ProjectRequiredSkillRepository repository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;

    public ProjectRequiredSkillService(ProjectRequiredSkillRepository repository,
                                       ProjectRepository projectRepository,
                                       SkillRepository skillRepository) {
        this.repository = repository;
        this.projectRepository = projectRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional
    public RequiredSkillResponse add(Long projectId, RequiredSkillRequest r) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
        Skill skill = skillRepository.findById(r.skillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill", r.skillId()));

        ProjectRequiredSkillId id = new ProjectRequiredSkillId(projectId, r.skillId());
        if (repository.existsById(id))
            throw new DuplicateResourceException("Project already requires skill: " + skill.getName());

        ProjectRequiredSkill rs = new ProjectRequiredSkill();
        rs.setId(id);
        rs.setProject(project);
        rs.setSkill(skill);
        rs.setMinProficiency(r.minProficiency());
        rs.setMinYears(r.minYears());
        rs.setWeight(r.weight());
        return toResponse(repository.save(rs));
    }

    @Transactional(readOnly = true)
    public List<RequiredSkillResponse> findByProject(Long projectId) {
        if (!projectRepository.existsById(projectId))
            throw new ResourceNotFoundException("Project", projectId);
        return repository.findAllForProject(projectId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public RequiredSkillResponse update(Long projectId, Long skillId, RequiredSkillUpdateRequest r) {
        ProjectRequiredSkill rs = getOrThrow(projectId, skillId);
        rs.setMinProficiency(r.minProficiency());
        rs.setMinYears(r.minYears());
        rs.setWeight(r.weight());
        return toResponse(rs);
    }

    @Transactional
    public void remove(Long projectId, Long skillId) {
        repository.delete(getOrThrow(projectId, skillId));
    }

    private ProjectRequiredSkill getOrThrow(Long projectId, Long skillId) {
        return repository.findById(new ProjectRequiredSkillId(projectId, skillId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Required skill", projectId + "/" + skillId));
    }

    private RequiredSkillResponse toResponse(ProjectRequiredSkill rs) {
        return new RequiredSkillResponse(
                rs.getProject().getId(), rs.getSkill().getId(), rs.getSkill().getName(),
                rs.getMinProficiency(), rs.getMinYears(), rs.getWeight());
    }
}