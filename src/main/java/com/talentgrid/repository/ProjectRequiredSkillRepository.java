package com.talentgrid.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.talentgrid.entity.ProjectRequiredSkill;
import com.talentgrid.entity.ProjectRequiredSkillId;

public interface ProjectRequiredSkillRepository
        extends JpaRepository<ProjectRequiredSkill, ProjectRequiredSkillId> {

    @EntityGraph(attributePaths = "skill")
    @Query("SELECT rs FROM ProjectRequiredSkill rs WHERE rs.project.id = :projectId ORDER BY rs.weight DESC")
    List<ProjectRequiredSkill> findAllForProject(@Param("projectId") Long projectId);
}