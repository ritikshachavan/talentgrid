package com.talentgrid.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.talentgrid.entity.Allocation;
import com.talentgrid.entity.enums.AllocationStatus;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    @Override
    @EntityGraph(attributePaths = {"employee", "project"})
    List<Allocation> findAll();

    @EntityGraph(attributePaths = {"employee", "project"})
    Optional<Allocation> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"employee", "project"})
    List<Allocation> findByEmployeeId(Long employeeId);

    @EntityGraph(attributePaths = {"employee", "project"})
    List<Allocation> findByProjectId(Long projectId);

    // HQL: total % an employee is already committed to
    @Query("SELECT COALESCE(SUM(a.allocationPercent), 0L) FROM Allocation a "
         + "WHERE a.employee.id = :employeeId AND a.status = :status")
    Long sumPercent(@Param("employeeId") Long employeeId,
                    @Param("status") AllocationStatus status);

    // Same, but ignoring one allocation (used when editing it)
    @Query("SELECT COALESCE(SUM(a.allocationPercent), 0L) FROM Allocation a "
         + "WHERE a.employee.id = :employeeId AND a.status = :status AND a.id <> :excludeId")
    Long sumPercentExcluding(@Param("employeeId") Long employeeId,
                             @Param("status") AllocationStatus status,
                             @Param("excludeId") Long excludeId);
}