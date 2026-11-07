package com.talentgrid.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.talentgrid.dto.EndingAllocationResponse;
import com.talentgrid.entity.enums.AllocationStatus;
import com.talentgrid.repository.AlertRepository;

@Service
public class AllocationAlertService {

    private final AlertRepository alertRepository;

    public AllocationAlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Transactional(readOnly = true)
    public List<EndingAllocationResponse> findEndingSoon(int days) {
        if (days < 1 || days > 365)
            throw new IllegalArgumentException("days must be between 1 and 365");

        LocalDate today = LocalDate.now();
        return alertRepository
                .findEndingBetween(AllocationStatus.ACTIVE, today, today.plusDays(days))
                .stream()
                .map(a -> new EndingAllocationResponse(
                        a.getId(), a.getEmployee().getFullName(), a.getProject().getName(),
                        a.getAllocationPercent(), a.getEndDate(),
                        ChronoUnit.DAYS.between(today, a.getEndDate())))
                .toList();
    }
}