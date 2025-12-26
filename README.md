# 🏢 Vendor Management System

> A modern, production-ready Spring Boot application that takes the headache out of vendor management. Track performance, automate procurement workflows, and get actionable insights—all in one place.

---

## What's This About?

Ever struggled with managing multiple vendors, tracking their performance, and handling procurement orders? Yeah, we've been there too. This system gives you a clean REST API to handle all that complexity, plus some neat analytics to help you make data-driven decisions.

## ✨ What Can It Do?

**Vendor Management**  
Create, update, and track vendors with real-time performance scores. No more spreadsheet nightmares.

**Smart Procurement Workflows**  
From draft to delivery, manage your entire procurement lifecycle with proper state management and validation. Because "it worked on my machine" shouldn't apply to procurement.

**Analytics That Actually Help**  
JPQL-powered queries give you insights into vendor performance, spending patterns, and delivery metrics. Make decisions based on data, not gut feelings.

**Auto-Audit Everything**  
Every API call gets logged automatically. Your compliance team will love you.

**Scheduled Score Updates**  
Performance scores recalculate daily at midnight. Set it and forget it.

**CSV Export with Injection Protection**  
Export your audit logs safely. We've handled the boring security stuff so you don't have to worry about CSV injection attacks.

### Built With

```
☕ Java 17
🚀 Spring Boot 3.2.0  
💾 Spring Data JPA
🗄️ H2 Database (in-memory, but easily switchable)
🔨 Maven
✅ Bean Validation API
```

---

## 🚦 Getting Started

### What You'll Need

Make sure you have these installed before diving in:
- Java 17 or newer
- Maven 3.6+ (or we'll install it for you!)
- PowerShell (if you're on Windows and want to run the test scripts)

### Quick Setup

**1. Grab the code**
```bash
git clone <repository-url>
cd vendor-management
```

**2. Build it**
```bash
mvn clean install
```
*First build might take a minute as Maven downloads dependencies. Perfect time for a coffee ☕*

**3. Fire it up**
```bash
mvn spring-boot:run
```

That's it! Your API is now live at **http://localhost:8080**

---

## 📖 API Documentation

Full documentation with request/response examples lives here: [API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)

### Quick API Reference

| Endpoint | What It Does |
|----------|--------------|
| `GET /api/vendors` | List all your vendors |
| `POST /api/vendors` | Add a new vendor |
| `PUT /api/vendors/{id}` | Update vendor details |
| `DELETE /api/vendors/{id}` | Remove a vendor |
| `GET /api/vendors/performance?minScore=70` | Find high performers |
| `POST /api/procurement/orders` | Create a procurement order |
| `POST /api/procurement/orders/{id}/submit` | Send order for approval |
| `POST /api/procurement/orders/{id}/approve` | Approve an order |
| `GET /api/analytics/summary` | Get comprehensive stats |
| `GET /api/analytics/vendors/top?limit=10` | See your top vendors |
| `GET /api/admin/audit/export` | Download audit logs as CSV |

*Pro tip: Check out the Postman collection below for ready-to-use requests!*

---

## 🧪 Testing Made Easy

### Option 1: Import into Postman (Seriously, Do This First!)

No need to manually type out endpoints and JSON bodies. We've got you covered with a complete Postman collection.

**Import in 3 Steps:**
1. Open Postman
2. Hit that **Import** button (top left corner)
3. Select `Vendor-Management-API.postman_collection.json` from the project root

**What You Get:**
- 🎯 All 28 endpoints pre-configured
- 📝 Sample request bodies already filled in
- 🔧 Environment variables set up (`{{baseUrl}}`)
- 👤 User headers configured
- 🔄 Complete procurement workflow examples

Just import, click send, and watch the magic happen!

### Option 2: PowerShell Scripts (Old School, But Works)

If you prefer command line testing:

```powershell
# Quick test of main endpoints
cd scripts
.\test_endpoints.ps1

# Full integration test suite
.\comprehensive_test.ps1
```

The comprehensive test runs through everything:
- Health checks ✓
- Full vendor CRUD cycle ✓
- Complete procurement workflow (draft → delivered) ✓
- Analytics endpoints ✓
- Audit logging & CSV export ✓
- Validation & error handling ✓

---

## 🎯 How Performance Scores Work

We calculate vendor performance using a weighted formula that considers three key metrics:

```
Score = (Delivery Rate × 40%) + (Quality Rating × 20 × 40%) + (Price Score × 20%)
```

**Real Example:**
```
Vendor: "Acme Supplies"
├─ Delivery Rate: 95%
├─ Quality Rating: 4.5 out of 5
└─ Price Score: 80/100

Calculation:
(95 × 0.4) + (4.5 × 20 × 0.4) + (80 × 0.2) = 90.0

Result: 90/100 (Excellent! 🌟)
```

This runs automatically every night at midnight, so you always have up-to-date scores.

---

## 🔄 Understanding Procurement States

Orders move through a strictly enforced state machine. Here's how it works:

```
DRAFT → PENDING_APPROVAL → APPROVED → IN_PROGRESS → DELIVERED
  ↓            ↓              ↓            ↓
             CANCELLED ←─────────────────────
```

**What Each State Means:**
- **DRAFT**: Order being created, can still edit
- **PENDING_APPROVAL**: Submitted, waiting for manager review
- **APPROVED**: Manager said yes, ready to process
- **IN_PROGRESS**: Warehouse is working on it
- **DELIVERED**: All done! 🎉
- **CANCELLED**: Nope, not happening (can cancel from most states)

Try skipping a state and the API will politely tell you no. 😊

---

## 🗄️ Database Info

**Default Setup:**
- Type: H2 In-Memory Database
- URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: *(leave blank)*

**Access the Console:**  
Visit http://localhost:8080/h2-console while the app is running

*Note: Data disappears when you restart. Great for testing, not great for production!*

**Want Persistent Storage?**  
Update `application.properties`:
```properties
spring.datasource.url=jdbc:h2:file:./data/vendordb
```
Now your data survives restarts.

---

## 📂 Project Structure

Clean, organized, and easy to navigate:

```
src/main/java/com/example/vendor/
├── VendorApplication.java         # 🚪 Main entry point
├── config/                        # ⚙️ Spring configuration
├── controller/                    # 🎮 REST endpoints
├── dto/                          # 📦 Data transfer objects
├── entity/                       # 🗃️ Database models
├── exception/                    # 🚨 Error handling
├── interceptor/                  # 🔍 Request interceptors
├── mapper/                       # 🔄 Entity ↔ DTO converters
├── repository/                   # 💾 Database access
├── scheduler/                    # ⏰ Scheduled tasks
├── service/                      # 💼 Business logic
└── util/                         # 🛠️ Helper utilities
```

Everything has its place. No "misc" or "utils2" folders here!

---

## 🔐 Security Features We've Built In

**CSV Export Protection**  
Prevents injection attacks by escaping dangerous characters (`=`, `+`, `-`, `@`). Your audit logs are safe from Excel formula execution exploits.

**Input Validation**  
Every DTO has Bean Validation annotations. Required fields, size limits, range checks—all handled automatically. Bad data gets a friendly 400 response.

**Proper Error Handling**  
Global exception handler catches everything and returns consistent JSON responses. No more mystery 500 errors with stack traces.

**Audit Trail**  
Every API call (except health checks) gets logged with user ID, method, path, and timestamp. Great for debugging and compliance.

---

## ⏰ Background Jobs

### Nightly Score Recalculation
**Runs:** Every day at midnight (00:00:00)  
**Does:** Recalculates performance scores for all vendors  
**Cron:** `0 0 0 * * ?`

Set it once, forget about it. Your vendor scores stay fresh automatically.

---

## 🔍 Sample Requests

### Create a Vendor
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

### Create a Procurement Order
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

### Get the Analytics Dashboard
```bash
curl http://localhost:8080/api/analytics/summary
```

*But seriously, use Postman. It's way easier. 😉*

---

## 🐛 Troubleshooting

**"Port 8080 is already in use"**  
Something else is running on that port. Quick fix in `application.properties`:
```properties
server.port=8081
```

**"Can't connect to H2"**  
Make sure the app is actually running. Check the console for that beautiful Spring Boot banner.

**"My data disappeared!"**  
That's how in-memory databases work. See the database section above for persistent storage.

**"Maven command not found"**  
We got you! The app includes Maven installation. Check the build logs.

---

## 📖 More Documentation

- [Complete API Documentation](docs/API_DOCUMENTATION.md) - Every endpoint explained
- [Implementation Summary](docs/IMPLEMENTATION_SUMMARY.md) - How we built this
- [Original Endpoints Doc](docs/endpoints.md) - Early design notes

---

## 🤝 Want to Contribute?

Found a bug? Want to add a feature? Here's how:

1. Fork the repo
2. Create your feature branch (`git checkout -b cool-new-feature`)
3. Commit your changes (`git commit -m 'Added something awesome'`)
4. Push to the branch (`git push origin cool-new-feature`)
5. Open a Pull Request

We review PRs regularly and appreciate good contributions!

---

## 📜 License

MIT License - go nuts! Build something cool with it.

---

## 🙏 Shoutout

Big thanks to the Spring Boot team for making enterprise Java actually enjoyable. Who would've thought?

---

**Built with ☕ and 💙**  
*Last Updated: December 26, 2025*
