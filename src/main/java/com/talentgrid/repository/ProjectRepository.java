package com.talentgrid.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.talentgrid.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Override
    @EntityGraph(attributePaths = "department")
    List<Project> findAll();

    @EntityGraph(attributePaths = "department")
    Optional<Project> findWithDepartmentById(Long id);
}