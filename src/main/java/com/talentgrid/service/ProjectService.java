package com.talentgrid.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.talentgrid.dto.ProjectRequest;
import com.talentgrid.dto.ProjectResponse;
import com.talentgrid.entity.Project;
import com.talentgrid.exception.ResourceNotFoundException;
import com.talentgrid.repository.DepartmentRepository;
import com.talentgrid.repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;

    public ProjectService(ProjectRepository projectRepository,
                          DepartmentRepository departmentRepository) {
        this.projectRepository = projectRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public ProjectResponse create(ProjectRequest r) {
        Project p = new Project();
        apply(p, r);
        return toResponse(projectRepository.save(p));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest r) {
        Project p = getOrThrow(id);
        apply(p, r);
        return toResponse(p);
    }

    @Transactional
    public void delete(Long id) {
        projectRepository.delete(getOrThrow(id));
    }

    private void apply(Project p, ProjectRequest r) {
        if (r.startDate() != null && r.endDate() != null && r.endDate().isBefore(r.startDate()))
            throw new IllegalArgumentException("End date cannot be before start date");

        p.setName(r.name());
        p.setDescription(r.description());
        p.setStartDate(r.startDate());
        p.setEndDate(r.endDate());
        if (r.status() != null) p.setStatus(r.status());
        p.setDepartment(departmentRepository.findById(r.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", r.departmentId())));
    }

    private Project getOrThrow(Long id) {
        return projectRepository.findWithDepartmentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    private ProjectResponse toResponse(Project p) {
        return new ProjectResponse(p.getId(), p.getName(), p.getDescription(),
                p.getStartDate(), p.getEndDate(), p.getStatus(),
                p.getDepartment().getId(), p.getDepartment().getName());
    }
}