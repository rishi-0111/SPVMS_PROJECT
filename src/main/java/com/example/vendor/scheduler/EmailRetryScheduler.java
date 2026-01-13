
package com.example.vendor.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.vendor.service.EmailService;

@Component
public class EmailRetryScheduler {

    private final EmailService emailService;

    public EmailRetryScheduler(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Retry failed emails every 10 minutes
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void retryFailedEmails() {
        System.out.println("🔄 [EmailRetryScheduler] Running scheduled retry for failed emails...");
        emailService.retryFailedEmails();
    }
}
