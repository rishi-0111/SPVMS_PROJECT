
package com.example.vendor.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.vendor.service.VendorPerformanceService;

@Component
public class VendorScoreScheduler {

    private final VendorPerformanceService service;

    public VendorScoreScheduler(VendorPerformanceService service) {
        this.service = service;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateScores() {
        service.updateScores();
    }
}
