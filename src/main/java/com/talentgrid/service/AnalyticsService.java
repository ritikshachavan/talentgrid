package com.talentgrid.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.talentgrid.dto.EmployeeResponse;
import com.talentgrid.dto.SkillCountResponse;
import com.talentgrid.dto.UtilizationResponse;
import com.talentgrid.entity.Employee;
import com.talentgrid.entity.enums.AllocationStatus;
import com.talentgrid.entity.enums.EmployeeStatus;
import com.talentgrid.repository.AnalyticsRepository;
import com.talentgrid.repository.EmployeeRepository;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final EmployeeRepository employeeRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository,
                            EmployeeRepository employeeRepository) {
        this.analyticsRepository = analyticsRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> bench() {
        return analyticsRepository
                .findBench(EmployeeStatus.ACTIVE, AllocationStatus.ACTIVE)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<UtilizationResponse> utilization() {
        return analyticsRepository.findUtilization(AllocationStatus.ACTIVE, EmployeeStatus.INACTIVE);
    }

    @Transactional(readOnly = true)
    public List<SkillCountResponse> skillDistribution() {
        return analyticsRepository.findSkillDistribution();
    }

    // Java Streams example: count employees per status with groupingBy
    @Transactional(readOnly = true)
    public Map<EmployeeStatus, Long> employeeSummary() {
        return employeeRepository.findAll().stream()
                .collect(Collectors.groupingBy(Employee::getStatus, Collectors.counting()));
    }

    private EmployeeResponse toResponse(Employee e) {
        Employee m = e.getManager();
        return new EmployeeResponse(
                e.getId(), e.getEmpCode(), e.getFullName(), e.getEmail(),
                e.getHireDate(), e.getExperienceYears(), e.getStatus(),
                e.getDepartment().getId(), e.getDepartment().getName(),
                e.getDesignation().getId(), e.getDesignation().getTitle(),
                m == null ? null : m.getId(),
                m == null ? null : m.getFullName());
    }
}