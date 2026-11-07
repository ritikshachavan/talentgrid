package com.talentgrid.scheduler;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.talentgrid.dto.EndingAllocationResponse;
import com.talentgrid.service.AllocationAlertService;

@Component
public class AllocationAlertScheduler {

    private static final Logger log = LoggerFactory.getLogger(AllocationAlertScheduler.class);

    private final AllocationAlertService service;

    public AllocationAlertScheduler(AllocationAlertService service) {
        this.service = service;
    }

    // default: every day at 09:00. Override with app.alerts.cron in application.properties
    @Scheduled(cron = "${app.alerts.cron:0 0 9 * * *}")
    public void alertEndingAllocations() {
        List<EndingAllocationResponse> ending = service.findEndingSoon(7);
        if (ending.isEmpty()) {
            log.info("Allocation check: nothing ends in the next 7 days");
            return;
        }
        for (EndingAllocationResponse a : ending) {
            log.warn("ALERT: {} on project '{}' ends on {} ({} days left)",
                    a.employeeName(), a.projectName(), a.endDate(), a.daysLeft());
        }
    }
}