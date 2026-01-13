# Vendor Management System

A Spring Boot REST API for managing vendors and procurement workflows with automated email notifications, performance tracking, and comprehensive analytics.

## What Does It Do?

This system helps you manage vendor relationships and procurement orders from start to finish. It automatically calculates vendor performance scores, enforces proper approval workflows, and sends email notifications at key milestones.

**Key Features:**
- Complete vendor CRUD operations with performance scoring
- State-machine driven procurement workflow (Draft → Approval → Delivery)
- Automated email notifications for order submissions and approvals
- Analytics and reporting on vendor performance
- Full audit trail of all API interactions
- CSV export with security protections

## Technology Stack

- **Java 17** with **Spring Boot 3.2.0**
- **Spring Data JPA** for database operations
- **H2 Database** (in-memory, easily switchable to PostgreSQL/MySQL)
- **JavaMailSender** for email notifications
- **Maven** for build management
- **Bean Validation** for input validation

## Getting Started

### Prerequisites

You'll need:
- Java 17 or higher
- Maven 3.6+ (or use your IDE's built-in Maven)
- A Gmail account (for email notifications)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd vendor-management
   ```

2. **Configure email settings** (optional but recommended)
   
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-gmail-app-password
   email.notification.approvers=manager@example.com
   ```
   
   See [Email Setup Guide](docs/EMAIL_NOTIFICATION_SYSTEM.md) for Gmail configuration.

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080`

### Quick Test

Check if everything's running:
```bash
curl http://localhost:8080/health
```

You should see: `UP`

## Usage Examples

### Working with Vendors

**Create a vendor:**
```bash
curl -X POST http://localhost:8080/api/vendors \
  -H "Content-Type: application/json" \
  -H "userId: admin" \
  -d '{
    "name": "Acme Supplies",
    "deliveryRate": 98.5,
    "qualityRating": 4.7,
    "priceScore": 85.0
  }'
```

The system automatically calculates a performance score based on:
- 40% delivery reliability
- 40% quality rating (scaled to 100)
- 20% pricing competitiveness

**Find high-performing vendors:**
```bash
curl "http://localhost:8080/api/vendors/performance?minScore=70"
```

### Managing Procurement Orders

**Create an order:**
```bash
curl -X POST http://localhost:8080/api/procurement/orders \
  -H "Content-Type: application/json" \
  -H "userId: john.doe" \
  -d '{
    "vendorId": 1,
    "requestedBy": "john.doe",
    "items": [
      {
        "itemName": "Office Supplies",
        "quantity": 100,
        "unitPrice": 5.50
      }
    ],
    "notes": "Urgent order"
  }'
```

**Submit for approval** (triggers email notification):
```bash
curl -X POST http://localhost:8080/api/procurement/orders/1/submit
```

**Approve the order** (triggers another email):
```bash
curl -X POST "http://localhost:8080/api/procurement/orders/1/approve?approver=manager"
```

### Order Workflow States

Orders follow a strict state machine:

```
DRAFT → PENDING_APPROVAL → APPROVED → IN_PROGRESS → DELIVERED
  ↓            ↓              ↓            ↓
              CANCELLED (allowed from most states)
```

The API prevents invalid state transitions and returns clear error messages.

### Analytics

**Get comprehensive analytics:**
```bash
curl http://localhost:8080/api/analytics/summary
```

**Find top vendors:**
```bash
curl "http://localhost:8080/api/analytics/vendors/top?limit=10"
```

**Search by name:**
```bash
curl "http://localhost:8080/api/analytics/vendors/search?name=acme"
```

## Email Notifications

The system automatically sends professional HTML emails when:
- Orders are submitted for approval (notifies approvers)
- Orders are approved (notifies the requester)

Features:
- Automatic retry (up to 3 attempts) for failed deliveries
- Full email history logged in database
- Scheduled retry every 10 minutes for failures
- Async processing (doesn't slow down API responses)

For complete email system documentation, see [Email Notification System](docs/EMAIL_NOTIFICATION_SYSTEM.md).

## Performance Scoring

Vendor scores are calculated using this formula:

```
Score = (deliveryRate × 0.4) + (qualityRating × 20 × 0.4) + (priceScore × 0.2)
```

**Example:**
- Delivery Rate: 95%
- Quality Rating: 4.5/5
- Price Score: 80/100

```
Score = (95 × 0.4) + (4.5 × 20 × 0.4) + (80 × 0.2) = 90.0
```

Scores update automatically at midnight every day.

## Database Access

The in-memory H2 database console is available at:
```
http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (leave blank)
```

**Note:** Data is lost when you restart the application. For persistent storage, update `application.properties`:
```properties
spring.datasource.url=jdbc:h2:file:./data/vendordb
```

## Testing

### Using Postman

Import the included collection for instant access to all endpoints:
1. Open Postman
2. Click **Import**
3 Select `Vendor-Management-API.postman_collection.json`

The collection includes 28 pre-configured requests with sample data.

### Using PowerShell Scripts

Run the comprehensive test suite:
```powershell
cd scripts
.\comprehensive_test.ps1
```

This tests the complete workflow including:
- Vendor CRUD operations
- Full procurement lifecycle
- Analytics endpoints
- Email notifications
- Audit logging

## API Documentation

| Endpoint Group | Description |
|----------------|-------------|
| `/api/vendors` | Vendor management and performance tracking |
| `/api/procurement` | Procurement order workflow |
| `/api/analytics` | Vendor analytics and reporting |
| `/api/admin/audit` | Audit trail and CSV export |
| `/api/admin/emails` | Email notification logs |

Complete API documentation: [API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)

## Project Structure

```
src/main/java/com/example/vendor/
├── VendorApplication.java      # Application entry point
├── config/                     # Spring configuration
├── controller/                 # REST API endpoints
├── dto/                        # Data transfer objects
├── entity/                     # Database models
├── exception/                  # Error handling
├── interceptor/                # Request interceptors
├── mapper/                     # DTO ↔ Entity conversion
├── repository/                 # Database access
├── scheduler/                  # Scheduled tasks
├── service/                    # Business logic
└── util/                       # Utilities

src/main/resources/
├── application.properties      # Configuration
└── templates/                  # Email templates
```

## Audit Logging

Every API call (except `/health`) is automatically logged with:
- User ID (from `userId` header)
- HTTP method and path
- Timestamp

Access audit logs:
```bash
curl http://localhost:8080/api/admin/audit
```

Export to CSV:
```bash
curl http://localhost:8080/api/admin/audit/export
```

CSV exports include protection against formula injection attacks.

## Configuration

### Changing the Port

Edit `application.properties`:
```properties
server.port=8081
```

### Email Settings

```properties
# SMTP server
spring.mail.host=smtp.gmail.com
spring.mail.port=587

# Credentials
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# Retry behavior
email.retry.max-attempts=3
email.retry.delay-ms=5000

# Recipients
email.notification.approvers=manager@example.com
```

For other email providers (SendGrid, AWS SES), see the email documentation.

## Scheduled Jobs

**Vendor Score Recalculation**
- Runs daily at midnight
- Automatically updates all vendor performance scores
- Cron expression: `0 0 0 * * ?`

**Email Retry**
- Runs every 10 minutes
- Retries failed email deliveries
- Cron expression: `0 */10 * * * ?`

## Troubleshooting

**Port 8080 already in use**
- Change the port in `application.properties`
- Or stop the other process using that port

**Emails not sending**
- Verify Gmail App Password is correct
- Check spam folder
- Review logs for error messages: `❌ Failed to send email...`

**Data disappeared after restart**
- This is normal for in-memory H2
- Switch to file-based storage (see Database Access section)

**Maven not found**
- Download from https://maven.apache.org/download.cgi
- Or use your IDE's built-in Maven support

## Documentation

- [Email Notification System](docs/EMAIL_NOTIFICATION_SYSTEM.md) - Complete email setup and features
- [API Documentation](docs/API_DOCUMENTATION.md) - All endpoints with examples
- [Implementation Summary](docs/IMPLEMENTATION_SUMMARY.md) - Technical architecture

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - feel free to use it in your own projects.

## Acknowledgments

Thanks to the Spring Boot team for making Java development enjoyable again!

---

**Version:** 1.1.0 (with Email Notifications)  
**Last Updated:** January 5, 2026
