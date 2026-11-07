package com.talentgrid.controller;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.talentgrid.dto.EmployeeResponse;
import com.talentgrid.dto.SkillCountResponse;
import com.talentgrid.dto.UtilizationResponse;
import com.talentgrid.entity.enums.EmployeeStatus;
import com.talentgrid.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/bench")
    public List<EmployeeResponse> bench() { return service.bench(); }

    @GetMapping("/utilization")
    public List<UtilizationResponse> utilization() { return service.utilization(); }

    @GetMapping("/skill-distribution")
    public List<SkillCountResponse> skillDistribution() { return service.skillDistribution(); }

    @GetMapping("/summary")
    public Map<EmployeeStatus, Long> summary() { return service.employeeSummary(); }
}