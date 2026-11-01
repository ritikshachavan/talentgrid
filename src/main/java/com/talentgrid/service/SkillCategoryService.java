package com.talentgrid.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.talentgrid.dto.SkillCategoryRequest;
import com.talentgrid.dto.SkillCategoryResponse;
import com.talentgrid.entity.SkillCategory;
import com.talentgrid.exception.DuplicateResourceException;
import com.talentgrid.exception.ResourceNotFoundException;
import com.talentgrid.repository.SkillCategoryRepository;

@Service
public class SkillCategoryService {

    private final SkillCategoryRepository repository;

    public SkillCategoryService(SkillCategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SkillCategoryResponse create(SkillCategoryRequest r) {
        if (repository.existsByNameIgnoreCase(r.name()))
            throw new DuplicateResourceException("Skill category already exists: " + r.name());
        SkillCategory c = new SkillCategory();
        c.setName(r.name());
        return toResponse(repository.save(c));
    }

    @Transactional(readOnly = true)
    public List<SkillCategoryResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SkillCategoryResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public SkillCategoryResponse update(Long id, SkillCategoryRequest r) {
        SkillCategory c = getOrThrow(id);
        if (repository.existsByNameIgnoreCaseAndIdNot(r.name(), id))
            throw new DuplicateResourceException("Skill category already exists: " + r.name());
        c.setName(r.name());
        return toResponse(c);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private SkillCategory getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill category", id));
    }

    private SkillCategoryResponse toResponse(SkillCategory c) {
        return new SkillCategoryResponse(c.getId(), c.getName());
    }
}