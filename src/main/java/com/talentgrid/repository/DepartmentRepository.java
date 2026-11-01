package com.talentgrid.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talentgrid.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}