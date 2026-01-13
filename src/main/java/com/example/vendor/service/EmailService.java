
package com.example.vendor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.example.vendor.entity.EmailLog;
import com.example.vendor.entity.EmailLog.EmailStatus;
import com.example.vendor.entity.ProcurementOrder;
import com.example.vendor.repository.EmailLogRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailLogRepository emailLogRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${email.notification.approvers}")
    private String approverEmails;

    @Value("${email.retry.max-attempts}")
    private int maxRetryAttempts;

    @Value("${email.retry.delay-ms}")
    private long retryDelayMs;

    private static final String TEMPLATE_PATH = "src/main/resources/templates/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Send email notification when order is submitted for approval
     */
    @Async("emailTaskExecutor")
    public void sendOrderSubmittedEmail(ProcurementOrder order) {
        try {
            String subject = "New Procurement Order Awaiting Approval - " + order.getOrderNumber();
            
            Map<String, String> variables = new HashMap<>();
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("vendorName", order.getVendor().getName());
            variables.put("requestedBy", order.getRequestedBy());
            variables.put("totalAmount", String.format("%.2f", order.getTotalAmount()));
            variables.put("submittedAt", LocalDateTime.now().format(DATE_FORMATTER));
            variables.put("notes", order.getNotes() != null ? order.getNotes() : "No additional notes");

            String body = buildEmailBody("order-submitted-template.html", variables);

            // Send to all approvers
            String[] recipients = approverEmails.split(",");
            for (String recipient : recipients) {
                EmailLog emailLog = createEmailLog(recipient.trim(), subject, body, order.getId());
                sendEmailWithRetry(emailLog);
            }
        } catch (Exception e) {
            System.err.println("Error preparing order submitted email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send email notification when order is approved
     */
    @Async("emailTaskExecutor")
    public void sendOrderApprovedEmail(ProcurementOrder order) {
        try {
            String subject = "Procurement Order Approved - " + order.getOrderNumber();
            
            Map<String, String> variables = new HashMap<>();
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("vendorName", order.getVendor().getName());
            variables.put("requestedBy", order.getRequestedBy());
            variables.put("totalAmount", String.format("%.2f", order.getTotalAmount()));
            variables.put("approvedBy", order.getApprovedBy());
            variables.put("approvedAt", order.getApprovedAt().format(DATE_FORMATTER));

            String body = buildEmailBody("order-approved-template.html", variables);

            // Send to the person who requested the order
            EmailLog emailLog = createEmailLog(order.getRequestedBy(), subject, body, order.getId());
            sendEmailWithRetry(emailLog);
        } catch (Exception e) {
            System.err.println("Error preparing order approved email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send email with retry logic
     */
    @Transactional
    public void sendEmailWithRetry(EmailLog emailLog) {
        int attempt = 0;
        boolean sent = false;

        while (attempt < maxRetryAttempts && !sent) {
            attempt++;
            emailLog.setRetryCount(attempt);

            try {
                sendEmail(emailLog.getRecipient(), emailLog.getSubject(), emailLog.getBody());
                
                // Mark as sent
                emailLog.setStatus(EmailStatus.SENT);
                emailLog.setSentAt(LocalDateTime.now());
                emailLog.setErrorMessage(null);
                sent = true;
                
                System.out.println("✅ Email sent successfully to: " + emailLog.getRecipient());
                
            } catch (Exception e) {
                emailLog.setStatus(EmailStatus.FAILED);
                emailLog.setErrorMessage(e.getMessage());
                
                System.err.println("❌ Failed to send email (attempt " + attempt + "/" + maxRetryAttempts + "): " + e.getMessage());

                if (attempt < maxRetryAttempts) {
                    try {
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } finally {
                emailLogRepository.save(emailLog);
            }
        }
    }

    /**
     * Core method to send email using JavaMailSender
     */
    private void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // true = HTML content

        mailSender.send(message);
    }

    /**
     * Build email body from HTML template with variable substitution
     */
    private String buildEmailBody(String templateName, Map<String, String> variables) {
        try {
            String templatePath = TEMPLATE_PATH + templateName;
            String template = new String(Files.readAllBytes(Paths.get(templatePath)));

            // Simple variable substitution
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            return template;
        } catch (IOException e) {
            System.err.println("Error loading email template: " + e.getMessage());
            return "<html><body><h2>Notification</h2><p>Unable to load email template.</p></body></html>";
        }
    }

    /**
     * Create EmailLog entry
     */
    private EmailLog createEmailLog(String recipient, String subject, String body, Long orderId) {
        EmailLog emailLog = new EmailLog();
        emailLog.setRecipient(recipient);
        emailLog.setSubject(subject);
        emailLog.setBody(body);
        emailLog.setRelatedOrderId(orderId);
        emailLog.setStatus(EmailStatus.PENDING);
        return emailLogRepository.save(emailLog);
    }

    /**
     * Retry all failed emails (called by scheduler)
     */
    @Transactional
    public void retryFailedEmails() {
        List<EmailLog> failedEmails = emailLogRepository.findByStatusAndRetryCountLessThan(
            EmailStatus.FAILED, maxRetryAttempts
        );

        System.out.println("🔄 Retrying " + failedEmails.size() + " failed emails...");

        for (EmailLog emailLog : failedEmails) {
            sendEmailWithRetry(emailLog);
        }
    }

    /**
     * Get all email logs
     */
    public List<EmailLog> getAllEmailLogs() {
        return emailLogRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get email logs by order ID
     */
    public List<EmailLog> getEmailLogsByOrderId(Long orderId) {
        return emailLogRepository.findByRelatedOrderId(orderId);
    }

    /**
     * Get failed email logs
     */
    public List<EmailLog> getFailedEmails() {
        return emailLogRepository.findByStatus(EmailStatus.FAILED);
    }
}
