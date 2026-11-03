package com.talentgrid.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.talentgrid.dto.EmployeeSkillRequest;
import com.talentgrid.dto.EmployeeSkillResponse;
import com.talentgrid.dto.EmployeeSkillUpdateRequest;
import com.talentgrid.entity.Employee;
import com.talentgrid.entity.EmployeeSkill;
import com.talentgrid.entity.EmployeeSkillId;
import com.talentgrid.entity.Skill;
import com.talentgrid.exception.DuplicateResourceException;
import com.talentgrid.exception.ResourceNotFoundException;
import com.talentgrid.repository.EmployeeRepository;
import com.talentgrid.repository.EmployeeSkillRepository;
import com.talentgrid.repository.SkillRepository;

@Service
public class EmployeeSkillService {

    private final EmployeeSkillRepository repository;
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;

    public EmployeeSkillService(EmployeeSkillRepository repository,
                                EmployeeRepository employeeRepository,
                                SkillRepository skillRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional
    public EmployeeSkillResponse add(Long employeeId, EmployeeSkillRequest r) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId));
        Skill skill = skillRepository.findById(r.skillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill", r.skillId()));

        EmployeeSkillId id = new EmployeeSkillId(employeeId, r.skillId());
        if (repository.existsById(id))
            throw new DuplicateResourceException("Employee already has skill: " + skill.getName());

        EmployeeSkill es = new EmployeeSkill();
        es.setId(id);
        es.setEmployee(employee);
        es.setSkill(skill);
        es.setProficiency(r.proficiency());
        es.setYearsOfExperience(r.yearsOfExperience());
        return toResponse(repository.save(es));
    }

    @Transactional(readOnly = true)
    public List<EmployeeSkillResponse> findByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId))
            throw new ResourceNotFoundException("Employee", employeeId);
        return repository.findAllForEmployee(employeeId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public EmployeeSkillResponse update(Long employeeId, Long skillId, EmployeeSkillUpdateRequest r) {
        EmployeeSkill es = getOrThrow(employeeId, skillId);
        es.setProficiency(r.proficiency());
        es.setYearsOfExperience(r.yearsOfExperience());
        return toResponse(es);
    }

    @Transactional
    public void remove(Long employeeId, Long skillId) {
        repository.delete(getOrThrow(employeeId, skillId));
    }

    private EmployeeSkill getOrThrow(Long employeeId, Long skillId) {
        return repository.findById(new EmployeeSkillId(employeeId, skillId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee skill", employeeId + "/" + skillId));
    }

    private EmployeeSkillResponse toResponse(EmployeeSkill es) {
        return new EmployeeSkillResponse(
                es.getEmployee().getId(), es.getSkill().getId(), es.getSkill().getName(),
                es.getSkill().getCategory().getName(),
                es.getProficiency(), es.getYearsOfExperience());
    }
}