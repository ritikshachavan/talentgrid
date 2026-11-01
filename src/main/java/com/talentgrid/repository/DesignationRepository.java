package com.talentgrid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.talentgrid.entity.Designation;

public interface DesignationRepository extends JpaRepository<Designation, Long> {
    boolean existsByTitleIgnoreCase(String title);
    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);
}