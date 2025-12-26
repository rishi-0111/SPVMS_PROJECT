# Vendor Management System - Complete API Documentation

## Overview
This is a comprehensive Vendor Management System built with Spring Boot 3.2.0 that includes vendor performance tracking, procurement workflow management, audit logging, and advanced analytics.

---

## Table of Contents
1. [Health Check](#health-check)
2. [Vendor Management APIs](#vendor-management-apis)
3. [Procurement Workflow APIs](#procurement-workflow-apis)
4. [Analytics APIs](#analytics-apis)
5. [Audit APIs](#audit-apis)
6. [Test APIs](#test-apis)

---

## Health Check

### GET /health
Health check endpoint (excluded from audit logging).

**Response:**
```
UP
```

---

## Vendor Management APIs

### GET /api/vendors
Get all vendors.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Acme Supplies",
    "deliveryRate": 98.5,
    "qualityRating": 4.7,
    "priceScore": 85.0,
    "performanceScore": 92.3
  }
]
```

### GET /api/vendors/{id}
Get a vendor by ID.

**Response:** `200 OK` | `404 Not Found`

### POST /api/vendors
Create a new vendor.

**Request Body:**
```json
{
  "name": "New Vendor Inc",
  "deliveryRate": 95.0,
  "qualityRating": 4.5,
  "priceScore": 80.0
}
```

**Validation Rules:**
- `name`: Required, 2-200 characters
- `deliveryRate`: Required, 0-100
- `qualityRating`: Required, 0.0-5.0
- `priceScore`: Required, 0-100

**Response:** `201 Created` | `400 Bad Request`

### PUT /api/vendors/{id}
Update an existing vendor.

**Request Body:** Same as POST
**Response:** `200 OK` | `404 Not Found` | `400 Bad Request`

### DELETE /api/vendors/{id}
Delete a vendor.

**Response:** `204 No Content` | `404 Not Found`

### POST /api/vendors/{id}/recalculate-score
Recalculate performance score for a specific vendor.

**Response:** `200 OK`

### POST /api/vendors/recalculate-all-scores
Recalculate performance scores for all vendors.

**Response:** `200 OK`

### GET /api/vendors/performance?minScore=70
Get vendors with performance score above threshold.

**Query Parameters:**
- `minScore` (optional, default: 70)

**Response:** `200 OK`

---

## Procurement Workflow APIs

### POST /api/procurement/orders
Create a new procurement order (starts in DRAFT status).

**Request Body:**
```json
{
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
}
```

**Validation Rules:**
- `vendorId`: Required
- `requestedBy`: Required, not blank
- `items`: Required, at least one item
- Each item:
  - `itemName`: Required, not blank
  - `quantity`: Required, minimum 1
  - `unitPrice`: Required, minimum 0.01

**Response:** `201 Created` | `400 Bad Request` | `404 Not Found`

### GET /api/procurement/orders
Get all orders (ordered by creation date, newest first).

**Response:** `200 OK`

### GET /api/procurement/orders/{id}
Get order by ID.

**Response:** `200 OK` | `404 Not Found`

### GET /api/procurement/orders/by-status/{status}
Get orders by status.

**Valid Statuses:** DRAFT, PENDING_APPROVAL, APPROVED, IN_PROGRESS, DELIVERED, CANCELLED

**Response:** `200 OK`

### GET /api/procurement/orders/by-vendor/{vendorId}
Get all orders for a specific vendor.

**Response:** `200 OK`

### POST /api/procurement/orders/{id}/submit
Submit order for approval (DRAFT → PENDING_APPROVAL).

**Response:** `200 OK` | `400 Bad Request` | `404 Not Found`

### POST /api/procurement/orders/{id}/approve?approver=manager
Approve an order (PENDING_APPROVAL → APPROVED).

**Query Parameters:**
- `approver`: Required

**Response:** `200 OK` | `400 Bad Request` | `404 Not Found`

### POST /api/procurement/orders/{id}/start
Start order processing (APPROVED → IN_PROGRESS).

**Response:** `200 OK` | `400 Bad Request` | `404 Not Found`

### POST /api/procurement/orders/{id}/deliver
Mark order as delivered (IN_PROGRESS → DELIVERED).

**Response:** `200 OK` | `400 Bad Request` | `404 Not Found`

### POST /api/procurement/orders/{id}/cancel
Cancel an order (any status except DELIVERED → CANCELLED).

**Response:** `200 OK` | `400 Bad Request` | `404 Not Found`

---

## Analytics APIs

### GET /api/analytics/summary
Get comprehensive vendor analytics summary.

**Response:** `200 OK`
```json
{
  "totalVendors": 50,
  "averagePerformanceScore": 75.5,
  "averageDeliveryRate": 92.3,
  "averageQualityRating": 4.2,
  "averagePriceScore": 78.5,
  "maxPerformanceScore": 98.5,
  "minPerformanceScore": 45.2,
  "topPerformersCount": 15,
  "lowPerformersCount": 5
}
```

### GET /api/analytics/distribution
Get performance score distribution by ranges.

**Response:** `200 OK`
```json
{
  "excellent_90_plus": 12,
  "good_70_to_89": 25,
  "average_50_to_69": 10,
  "poor_below_50": 3
}
```

### GET /api/analytics/vendors/top?limit=10
Get top N vendors by performance score.

**Query Parameters:**
- `limit` (optional, default: 10)

**Response:** `200 OK`

### GET /api/analytics/vendors/by-score-range?minScore=70&maxScore=90
Get vendors within a score range.

**Query Parameters:**
- `minScore` (optional, default: 0)
- `maxScore` (optional, default: 100)

**Response:** `200 OK`

### GET /api/analytics/vendors/by-delivery-rate?minDeliveryRate=90
Get vendors by minimum delivery rate.

**Query Parameters:**
- `minDeliveryRate` (optional, default: 90)

**Response:** `200 OK`

### GET /api/analytics/vendors/by-quality-rating?minRating=4.0
Get vendors by minimum quality rating.

**Query Parameters:**
- `minRating` (optional, default: 4.0)

**Response:** `200 OK`

### GET /api/analytics/vendors/search?name=acme
Search vendors by name (case-insensitive, partial match).

**Query Parameters:**
- `name`: Required

**Response:** `200 OK`

---

## Audit APIs

### GET /api/admin/audit
Get all audit logs.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "userId": "john.doe",
    "action": "POST",
    "requestPath": "/api/vendors",
    "timestamp": "2025-12-26T10:30:00"
  }
]
```

### GET /api/admin/audit/export
Export audit logs as CSV file with timestamp.

**Response:** `200 OK` (text/csv)
- Filename format: `audit_export_yyyyMMdd_HHmmss.csv`
- Includes proper CSV escaping and injection protection
- Headers: userId, action, path, timestamp

---

## Test APIs

### POST /api/test
Create a test entity.

**Request Body:**
```json
{
  "name": "Test Name"
}
```

**Validation Rules:**
- `name`: Required, 2-100 characters

**Response:** `201 Created` | `400 Bad Request`

### GET /api/test
Get all test entities.

**Response:** `200 OK`

---

## Error Responses

All endpoints return standardized error responses:

### Validation Error (400 Bad Request)
```json
{
  "timestamp": "2025-12-26T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation error",
  "path": "/api/vendors",
  "validationErrors": {
    "name": "Vendor name is required",
    "deliveryRate": "Delivery rate must be between 0 and 100"
  }
}
```

### Resource Not Found (404 Not Found)
```json
{
  "timestamp": "2025-12-26T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Vendor not found with id: 999",
  "path": "/api/vendors/999"
}
```

### Invalid Operation (400 Bad Request)
```json
{
  "timestamp": "2025-12-26T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Only DRAFT orders can be submitted for approval",
  "path": "/api/procurement/orders/5/submit"
}
```

---

## Performance Score Calculation

Vendor performance score is calculated using the formula:
```
performanceScore = (deliveryRate × 0.4) + (qualityRating × 20 × 0.4) + (priceScore × 0.2)
```

Where:
- `deliveryRate`: 0-100 (percentage)
- `qualityRating`: 0.0-5.0 (rating)
- `priceScore`: 0-100 (score)

**Example:**
- deliveryRate = 95
- qualityRating = 4.5
- priceScore = 80

**Calculation:**
- (95 × 0.4) + (4.5 × 20 × 0.4) + (80 × 0.2)
- 38 + 36 + 16
- **= 90.0**

---

## Scheduled Jobs

### Vendor Score Recalculation
- **Schedule:** Daily at midnight (00:00:00)
- **Action:** Recalculates performance scores for all vendors
- **Cron Expression:** `0 0 0 * * ?`

---

## Audit Logging

All API requests (except `/health`) are automatically logged with:
- User ID (from `userId` header, defaults to "ANONYMOUS")
- HTTP method
- Request path
- Timestamp

---

## Testing

Run the PowerShell test script:
```powershell
.\scripts\test_endpoints.ps1
```

Or with custom base URL:
```powershell
.\scripts\test_endpoints.ps1 -BaseUrl http://localhost:8081
```

---

## Database

- **Type:** H2 In-Memory Database
- **URL:** `jdbc:h2:mem:testdb`
- **Console:** Enabled at `/h2-console`
- **Schema:** Auto-generated with JPA

---

## Application Properties

```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```
