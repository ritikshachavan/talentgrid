package com.talentgrid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.talentgrid.entity.SkillCategory;

public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}