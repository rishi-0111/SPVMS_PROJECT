# Quick Start Guide

Get up and running with the Vendor Management System in 5 minutes!

## Step 1: Start the Application (2 minutes)

```bash
# Navigate to project directory
cd vendor-management

# Build and run
mvn spring-boot:run
```

Wait for the message: `Started VendorApplication in X.XXX seconds`

## Step 2: Verify Health (30 seconds)

Open your browser or use curl:
```bash
curl http://localhost:8080/health
```

Expected response: `UP`

## Step 3: Create Sample Data (1 minute)

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
        "description": "Paper and pens",
        "quantity": 100,
        "unitPrice": 5.50
      }
    ],
    "notes": "Urgent order"
  }'
```

## Step 4: Explore the APIs (1 minute)

### View All Vendors
```bash
curl http://localhost:8080/api/vendors
```

### Get Analytics Summary
```bash
curl http://localhost:8080/api/analytics/summary
```

### View Audit Logs
```bash
curl http://localhost:8080/api/admin/audit
```

## Step 5: Run Automated Tests (30 seconds)

### Windows PowerShell
```powershell
cd scripts
.\comprehensive_test.ps1
```

### Alternative (Basic Tests)
```powershell
cd scripts
.\test_endpoints.ps1
```

---

## 🎯 What's Next?

### Explore the Complete Workflow

1. **Submit Order for Approval**
   ```bash
   curl -X POST http://localhost:8080/api/procurement/orders/1/submit
   ```

2. **Approve Order**
   ```bash
   curl -X POST "http://localhost:8080/api/procurement/orders/1/approve?approver=manager"
   ```

3. **Start Processing**
   ```bash
   curl -X POST http://localhost:8080/api/procurement/orders/1/start
   ```

4. **Mark Delivered**
   ```bash
   curl -X POST http://localhost:8080/api/procurement/orders/1/deliver
   ```

### View Analytics

```bash
# Top performing vendors
curl "http://localhost:8080/api/analytics/vendors/top?limit=5"

# Performance distribution
curl http://localhost:8080/api/analytics/distribution

# Search vendors
curl "http://localhost:8080/api/analytics/vendors/search?name=acme"
```

### Access H2 Console

1. Open browser: `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:mem:testdb`
3. Username: `sa`
4. Password: *(leave empty)*
5. Click **Connect**

---

## 📱 Using Postman

Import this collection to test all endpoints:

### Create Vendor Collection
1. Create new collection: "Vendor Management"
2. Add environment variable: `baseUrl = http://localhost:8080`
3. Add header: `userId = test-user`
4. Import endpoints from [docs/API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)

---

## 🐛 Troubleshooting

### Application won't start
- **Port in use:** Change port in `application.properties`
- **Java version:** Ensure Java 17+ is installed
- **Maven issues:** Run `mvn clean install` first

### API returns 500 error
- Check application logs
- Verify request body format (must be valid JSON)
- Ensure all required fields are provided

### Tests fail
- Ensure application is running
- Check port in test script matches application
- Verify no other process is using port 8080

---

## 📚 Documentation

- **Full API Docs:** [docs/API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)
- **Implementation Details:** [docs/IMPLEMENTATION_SUMMARY.md](docs/IMPLEMENTATION_SUMMARY.md)
- **README:** [README.md](README.md)

---

## 🎉 Success!

You're now ready to use the Vendor Management System. Explore all features:

✅ Vendor CRUD operations  
✅ Procurement workflow management  
✅ Advanced analytics and reporting  
✅ Audit logging and CSV export  
✅ Automated performance tracking  

**Happy coding!** 🚀
