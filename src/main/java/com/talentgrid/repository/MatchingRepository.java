package com.talentgrid.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import com.talentgrid.dto.AllocatedPercent;
import com.talentgrid.entity.Employee;
import com.talentgrid.entity.EmployeeSkill;
import com.talentgrid.entity.enums.AllocationStatus;
import com.talentgrid.entity.enums.EmployeeStatus;

public interface MatchingRepository extends Repository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE e.status = :status ORDER BY e.id")
    List<Employee> findEmployeesByStatus(@Param("status") EmployeeStatus status);

    // one query for ALL employee skills (avoids one query per employee)
    @Query("SELECT es FROM EmployeeSkill es "
         + "JOIN FETCH es.employee e JOIN FETCH es.skill s "
         + "WHERE e.status = :status")
    List<EmployeeSkill> findSkillsByEmployeeStatus(@Param("status") EmployeeStatus status);

    // total ACTIVE allocation % per employee
    @Query("SELECT new com.talentgrid.dto.AllocatedPercent(a.employee.id, SUM(a.allocationPercent)) "
         + "FROM Allocation a WHERE a.status = :status GROUP BY a.employee.id")
    List<AllocatedPercent> sumAllocatedByEmployee(@Param("status") AllocationStatus status);
}