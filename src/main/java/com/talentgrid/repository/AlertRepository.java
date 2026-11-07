package com.talentgrid.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import com.talentgrid.entity.Allocation;
import com.talentgrid.entity.enums.AllocationStatus;

public interface AlertRepository extends Repository<Allocation, Long> {

    @Query("SELECT a FROM Allocation a "
         + "JOIN FETCH a.employee JOIN FETCH a.project "
         + "WHERE a.status = :status AND a.endDate IS NOT NULL "
         + "AND a.endDate BETWEEN :from AND :to "
         + "ORDER BY a.endDate")
    List<Allocation> findEndingBetween(@Param("status") AllocationStatus status,
                                       @Param("from") LocalDate from,
                                       @Param("to") LocalDate to);
}