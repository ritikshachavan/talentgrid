package com.talentgrid.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.talentgrid.dto.EndingAllocationResponse;
import com.talentgrid.service.AllocationAlertService;

@RestController
@RequestMapping("/api/analytics")
public class AlertController {

    private final AllocationAlertService service;

    public AlertController(AllocationAlertService service) {
        this.service = service;
    }

    @GetMapping("/expiring-allocations")
    public List<EndingAllocationResponse> expiring(@RequestParam(defaultValue = "7") int days) {
        return service.findEndingSoon(days);
    }
}