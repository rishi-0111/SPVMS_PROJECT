# Email Notification System Documentation

## Overview

The Vendor Management System includes an automated email notification system that sends professional HTML emails to stakeholders when procurement orders are submitted for approval or approved. Built with Spring Boot's JavaMailSender, the system features retry logic, comprehensive logging, and asynchronous processing.

---

## Architecture

### Components

The email notification system consists of several integrated components:

**Core Service Layer:**
- `EmailService` - Handles email composition, sending, and retry logic
- `ProcurementService` - Triggers email notifications during workflow transitions

**Data Layer:**
- `EmailLog` entity - Stores email delivery history
- `EmailLogRepository` - Provides database access for email logs

**Infrastructure:**
- `AsyncConfig` - Configures thread pool for non-blocking email operations
- `EmailRetryScheduler` - Automatically retries failed emails every 10 minutes

**Presentation:**
- `EmailController` - REST API for email log management
- HTML Templates - Professional email designs

---

## How It Works

### Workflow Integration

Emails are automatically triggered at key points in the procurement workflow:

**1. Order Submission**
```
User submits order → ProcurementService.submitForApproval()
  → EmailService.sendOrderSubmittedEmail()
  → Email sent to approvers
```

**2. Order Approval**
```
Manager approves order → ProcurementService.approveOrder()
  → EmailService.sendOrderApprovedEmail()
  → Email sent to requester
```

### Email Sending Process

When an email is triggered:

1. **Create EmailLog**: A database record is created with status `PENDING`
2. **Async Send**: Email is sent asynchronously (non-blocking)
3. **Retry Logic**: Up to 3 attempts with 5-second delays
4. **Update Status**: On success, status changes to `SENT`; on failure, status is `FAILED`
5. **Scheduled Retry**: Failed emails are retried every 10 minutes

---

## Configuration

### SMTP Settings

Located in `src/main/resources/application.properties`:

```properties
# Email Server
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@example.com
spring.mail.password=your-app-password

# Security
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Retry Configuration
email.retry.max-attempts=3
email.retry.delay-ms=5000

# Recipients
email.notification.approvers=manager@example.com
```

### Gmail Setup

For Gmail accounts:
1. Enable 2-Factor Authentication
2. Generate App Password (Security → App passwords)
3. Use App Password (not regular password) in configuration
4. Remove spaces from the 16-character password

### Alternative Providers

The system works with any SMTP provider:

**SendGrid:**
```properties
spring.mail.host=smtp.sendgrid.net
spring.mail.username=apikey
spring.mail.password=YOUR_SENDGRID_API_KEY
```

**AWS SES:**
```properties
spring.mail.host=email-smtp.us-east-1.amazonaws.com
spring.mail.username=YOUR_AWS_SES_USERNAME
spring.mail.password=YOUR_AWS_SES_PASSWORD
```

---

## Email Templates

### Template Structure

Templates are HTML files with variable placeholders:

**Location:** `src/main/resources/templates/`

**Available Templates:**
- `order-submitted-template.html` - Green theme for submission notifications
- `order-approved-template.html` - Blue theme for approval notifications

### Template Variables

Variables are replaced at runtime using `{{variableName}}` syntax:

**Submission Template:**
- `{{orderNumber}}` - Procurement order number
- `{{vendorName}}` - Vendor company name
- `{{requestedBy}}` - Person who created the order
- `{{totalAmount}}` - Order total in USD
- `{{submittedAt}}` - Submission timestamp
- `{{notes}}` - Additional notes

**Approval Template:**
- `{{orderNumber}}` - Procurement order number
- `{{vendorName}}` - Vendor company name
- `{{requestedBy}}` - Original requester
- `{{totalAmount}}` - Order total in USD
- `{{approvedBy}}` - Person who approved
- `{{approvedAt}}` - Approval timestamp

### Customizing Templates

Edit the HTML files directly:

```html
<h2>{{orderNumber}}</h2>
<p>Total: ${{totalAmount}}</p>
```

Add your company logo:
```html
<img src="https://your-domain.com/logo.png" alt="Company Logo">
```

---

## Database Schema

### EmailLog Table

Tracks all email delivery attempts:

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| recipient | VARCHAR | Email address |
| subject | VARCHAR | Email subject line |
| body | TEXT | Full HTML content |
| status | VARCHAR | PENDING, SENT, or FAILED |
| retry_count | INT | Number of send attempts |
| error_message | TEXT | Error details (if failed) |
| related_order_id | BIGINT | Foreign key to procurement_order |
| sent_at | TIMESTAMP | Successful delivery time |
| created_at | TIMESTAMP | Record creation time |

### Querying Email Logs

Access via H2 console (http://localhost:8080/h2-console):

```sql
-- View all emails
SELECT * FROM EMAIL_LOG ORDER BY created_at DESC;

-- Find failed emails
SELECT * FROM EMAIL_LOG WHERE status = 'FAILED';

-- Emails for specific order
SELECT * FROM EMAIL_LOG WHERE related_order_id = 1;

-- Delivery success rate
SELECT status, COUNT(*) as count 
FROM EMAIL_LOG 
GROUP BY status;
```

---

## REST API

### Email Management Endpoints

**Base Path:** `/api/admin/emails`

#### Get All Email Logs
```http
GET /api/admin/emails
```

**Response:**
```json
[
  {
    "id": 1,
    "recipient": "user@example.com",
    "subject": "New Procurement Order...",
    "status": "SENT",
    "retryCount": 1,
    "sentAt": "2026-01-05T12:09:50",
    "createdAt": "2026-01-05T12:09:13"
  }
]
```

#### Get Failed Emails
```http
GET /api/admin/emails/failed
```

#### Get Emails by Order
```http
GET /api/admin/emails/order/{orderId}
```

**Example:**
```bash
curl http://localhost:8080/api/admin/emails/order/1
```

#### Retry All Failed Emails
```http
POST /api/admin/emails/retry-all
```

**Response:**
```
Retry process initiated for all failed emails
```

---

## Monitoring & Troubleshooting

### Application Logs

Look for these log messages:

**Success:**
```
✅ Email sent successfully to: user@example.com
```

**Failure:**
```
❌ Failed to send email (attempt 1/3): Authentication failed
```

**Retry:**
```
🔄 [EmailRetryScheduler] Running scheduled retry for failed emails...
🔄 Retrying 2 failed emails...
```

### Common Issues

**Authentication Failed**
- Verify App Password is correct (not regular Gmail password)
- Check 2FA is enabled on Gmail account
- Ensure no extra spaces in password

**Connection Timeout**
- Check SMTP host and port are correct
- Verify firewall isn't blocking port 587
- Ensure internet connection is stable

**Emails Not Arriving**
- Check spam/junk folder
- Verify recipient email address is correct
- Check Gmail's "Sent Mail" folder to confirm delivery

**Status Stuck on PENDING**
- Check application logs for errors
- Verify async configuration is working
- Manually trigger retry: `POST /api/admin/emails/retry-all`

---

## Performance Considerations

### Async Processing

Emails are sent asynchronously to avoid blocking API responses:

```java
@Async("emailTaskExecutor")
public void sendOrderSubmittedEmail(ProcurementOrder order) {
    // Non-blocking - returns immediately
}
```

**Thread Pool Configuration:**
- Core threads: 2
- Max threads: 5
- Queue capacity: 100

### Retry Strategy

Helps handle temporary failures:
- **Max attempts:** 3 (configurable)
- **Delay:** 5 seconds between attempts (configurable)
- **Scheduled retry:** Every 10 minutes for failed emails

### Email Rate Limiting

To avoid being flagged as spam:
- Gmail: 500 emails/day (for regular accounts)
- SendGrid: Based on plan (starts at 100/day free)
- AWS SES: Large volumes, pay per email

---

## Security

### Best Practices

1. **Never commit credentials** - Use environment variables
2. **Use App Passwords** - Not regular account passwords
3. **Enable TLS** - Encrypt email transmission
4. **Sanitize inputs** - Prevent injection attacks in email content
5. **Log selectively** - Don't log full email bodies in production

### Environment Variables

Instead of hardcoding in `application.properties`:

```properties
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}
```

Set in environment:
```powershell
$env:EMAIL_USERNAME="your-email@gmail.com"
$env:EMAIL_PASSWORD="your-app-password"
```

---

## Testing

### Manual Testing

1. **Create a test order:**
```bash
curl -X POST http://localhost:8080/api/procurement/orders \
  -H "Content-Type: application/json" \
  -d '{
    "vendorId": 1,
    "requestedBy": "test@example.com",
    "items": [...],
    "notes": "Test order"
  }'
```

2. **Submit for approval:**
```bash
curl -X POST http://localhost:8080/api/procurement/orders/1/submit
```

3. **Check email logs:**
```bash
curl http://localhost:8080/api/admin/emails
```

### Automated Testing

Run the comprehensive test script:
```powershell
cd scripts
.\comprehensive_test.ps1
```

---

## Future Enhancements

Potential improvements for the email system:

1. **Template Engine** - Use Thymeleaf for advanced templating
2. **Email Attachments** - Include PDF invoices
3. **User Preferences** - Allow users to opt-in/out of notifications
4. **Delivery Receipts** - Track when emails are opened
5. **SMS Integration** - Add Twilio for critical alerts
6. **Batch Digests** - Daily summary instead of individual emails
7. **Email Webhooks** - Real-time delivery status updates

---

## Support

For issues or questions:
1. Check application logs for error messages
2. Query `EMAIL_LOG` table for delivery status
3. Verify SMTP configuration in `application.properties`
4. Test connectivity to SMTP server
5. Review this documentation for troubleshooting steps

---

**Last Updated:** January 5, 2026  
**Version:** 1.0.0
