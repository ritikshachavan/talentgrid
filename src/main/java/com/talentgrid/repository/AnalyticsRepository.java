package com.talentgrid.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import com.talentgrid.dto.SkillCountResponse;
import com.talentgrid.dto.UtilizationResponse;
import com.talentgrid.entity.Employee;
import com.talentgrid.entity.enums.AllocationStatus;
import com.talentgrid.entity.enums.EmployeeStatus;

public interface AnalyticsRepository extends Repository<Employee, Long> {

    // HQL 1: bench = active employees with NO active allocation
    @Query("SELECT e FROM Employee e "
         + "JOIN FETCH e.department JOIN FETCH e.designation LEFT JOIN FETCH e.manager "
         + "WHERE e.status = :empStatus "
         + "AND NOT EXISTS (SELECT a.id FROM Allocation a "
         + "                WHERE a.employee = e AND a.status = :allocStatus) "
         + "ORDER BY e.firstName")
    List<Employee> findBench(@Param("empStatus") EmployeeStatus empStatus,
                             @Param("allocStatus") AllocationStatus allocStatus);

    // HQL 2: utilization = LEFT JOIN + GROUP BY + SUM
    @Query("SELECT new com.talentgrid.dto.UtilizationResponse("
         + "e.id, e.empCode, CONCAT(e.firstName, ' ', e.lastName), COALESCE(SUM(a.allocationPercent), 0L)) "
         + "FROM Employee e LEFT JOIN Allocation a ON a.employee = e AND a.status = :allocStatus "
         + "WHERE e.status <> :inactive "
         + "GROUP BY e.id, e.empCode, e.firstName, e.lastName "
         + "ORDER BY e.firstName")
    List<UtilizationResponse> findUtilization(@Param("allocStatus") AllocationStatus allocStatus,
                                              @Param("inactive") EmployeeStatus inactive);

    // HQL 3: how many employees have each skill, and their average level
    @Query("SELECT new com.talentgrid.dto.SkillCountResponse("
         + "s.name, c.name, COUNT(es), AVG(es.proficiency)) "
         + "FROM EmployeeSkill es JOIN es.skill s JOIN s.category c "
         + "GROUP BY s.name, c.name "
         + "ORDER BY COUNT(es) DESC")
    List<SkillCountResponse> findSkillDistribution();
}