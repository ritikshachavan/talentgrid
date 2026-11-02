package com.talentgrid.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.talentgrid.dto.SkillRequest;
import com.talentgrid.dto.SkillResponse;
import com.talentgrid.entity.Skill;
import com.talentgrid.entity.SkillCategory;
import com.talentgrid.exception.DuplicateResourceException;
import com.talentgrid.exception.ResourceNotFoundException;
import com.talentgrid.repository.SkillCategoryRepository;
import com.talentgrid.repository.SkillRepository;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillCategoryRepository categoryRepository;

    public SkillService(SkillRepository skillRepository,
                        SkillCategoryRepository categoryRepository) {
        this.skillRepository = skillRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public SkillResponse create(SkillRequest r) {
        if (skillRepository.existsByNameIgnoreCase(r.name()))
            throw new DuplicateResourceException("Skill already exists: " + r.name());
        Skill s = new Skill();
        s.setName(r.name());
        s.setCategory(findCategory(r.categoryId()));
        return toResponse(skillRepository.save(s));
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> findAll(Long categoryId) {
        List<Skill> skills = (categoryId == null)
                ? skillRepository.findAll()
                : skillRepository.findByCategoryId(categoryId);
        return skills.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SkillResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public SkillResponse update(Long id, SkillRequest r) {
        Skill s = getOrThrow(id);
        if (skillRepository.existsByNameIgnoreCaseAndIdNot(r.name(), id))
            throw new DuplicateResourceException("Skill already exists: " + r.name());
        s.setName(r.name());
        s.setCategory(findCategory(r.categoryId()));
        return toResponse(s);
    }

    @Transactional
    public void delete(Long id) {
        skillRepository.delete(getOrThrow(id));
    }

    private Skill getOrThrow(Long id) {
        return skillRepository.findWithCategoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", id));
    }

    private SkillCategory findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill category", id));
    }

    private SkillResponse toResponse(Skill s) {
        return new SkillResponse(s.getId(), s.getName(),
                s.getCategory().getId(), s.getCategory().getName());
    }
}