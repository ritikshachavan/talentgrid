package com.talentgrid.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.talentgrid.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByEmpCodeIgnoreCase(String empCode);
    boolean existsByEmpCodeIgnoreCaseAndIdNot(String empCode, Long id);

    @Override
    @EntityGraph(attributePaths = {"department", "designation", "manager"})
    Page<Employee> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"department", "designation", "manager"})
    Optional<Employee> findWithDetailsById(Long id);
}