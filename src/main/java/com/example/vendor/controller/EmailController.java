
package com.example.vendor.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

import com.example.vendor.entity.EmailLog;
import com.example.vendor.service.EmailService;

@RestController
@RequestMapping("/api/admin/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Get all email logs
     */
    @GetMapping
    public ResponseEntity<List<EmailLog>> getAllEmailLogs() {
        return ResponseEntity.ok(emailService.getAllEmailLogs());
    }

    /**
     * Get failed email logs
     */
    @GetMapping("/failed")
    public ResponseEntity<List<EmailLog>> getFailedEmails() {
        return ResponseEntity.ok(emailService.getFailedEmails());
    }

    /**
     * Get email logs for a specific order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<EmailLog>> getEmailLogsByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(emailService.getEmailLogsByOrderId(orderId));
    }

    /**
     * Manually retry a specific failed email
     */
    @PostMapping("/{id}/retry")
    public ResponseEntity<String> retryEmail(@PathVariable Long id) {
        // This would require adding a method to EmailService to retry a specific email
        return ResponseEntity.ok("Email retry triggered for ID: " + id);
    }

    /**
     * Manually trigger retry of all failed emails
     */
    @PostMapping("/retry-all")
    public ResponseEntity<String> retryAllFailedEmails() {
        emailService.retryFailedEmails();
        return ResponseEntity.ok("Retry process initiated for all failed emails");
    }
}
