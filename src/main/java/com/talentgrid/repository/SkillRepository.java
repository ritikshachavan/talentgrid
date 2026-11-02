package com.talentgrid.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.talentgrid.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Override
    @EntityGraph(attributePaths = "category")
    List<Skill> findAll();

    @EntityGraph(attributePaths = "category")
    List<Skill> findByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = "category")
    Optional<Skill> findWithCategoryById(Long id);
}