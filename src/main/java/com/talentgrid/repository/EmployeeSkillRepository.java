package com.talentgrid.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.talentgrid.entity.EmployeeSkill;
import com.talentgrid.entity.EmployeeSkillId;

public interface EmployeeSkillRepository extends JpaRepository<EmployeeSkill, EmployeeSkillId> {

    @EntityGraph(attributePaths = {"skill", "skill.category"})
    @Query("SELECT es FROM EmployeeSkill es WHERE es.employee.id = :employeeId ORDER BY es.skill.name")
    List<EmployeeSkill> findAllForEmployee(@Param("employeeId") Long employeeId);
}