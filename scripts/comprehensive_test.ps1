<#
Comprehensive API Test Script for Vendor Management System
Tests all implemented features including procurement workflows and analytics
#>
param(
    [string]$BaseUrl = "http://localhost:8080"
)

$Headers = @{ 
    'Content-Type' = 'application/json'
    'userId' = 'test-user'
}

$SuccessCount = 0
$FailureCount = 0

function Write-TestHeader($title) {
    Write-Host "`n" ("=" * 80) -ForegroundColor Cyan
    Write-Host " $title" -ForegroundColor Cyan
    Write-Host ("=" * 80) -ForegroundColor Cyan
}

function Write-Success($message) {
    Write-Host "✓ $message" -ForegroundColor Green
    $script:SuccessCount++
}

function Write-Failure($message) {
    Write-Host "✗ $message" -ForegroundColor Red
    $script:FailureCount++
}

function Test-Endpoint($method, $path, $body = $null, $description) {
    $url = "$BaseUrl$path"
    Write-Host "`n[$method] $url" -ForegroundColor Yellow
    if ($description) { Write-Host "   $description" -ForegroundColor Gray }
    
    try {
        if ($body) {
            $jsonBody = $body | ConvertTo-Json -Depth 10
            Write-Host "Request Body:" -ForegroundColor Gray
            Write-Host $jsonBody -ForegroundColor DarkGray
        }
        
        $params = @{
            Uri = $url
            Method = $method
            Headers = $Headers
            ErrorAction = 'Stop'
        }
        if ($body) { $params['Body'] = ($body | ConvertTo-Json -Depth 10) }
        
        $response = Invoke-RestMethod @params
        
        Write-Host "Response:" -ForegroundColor Gray
        Write-Host ($response | ConvertTo-Json -Depth 6) -ForegroundColor DarkGray
        Write-Success "$method $path"
        return $response
    }
    catch {
        Write-Failure "$method $path - Error: $_"
        return $null
    }
}

Write-Host "`n╔════════════════════════════════════════════════════════════════════╗" -ForegroundColor Magenta
Write-Host "║  Vendor Management System - Comprehensive API Test Suite          ║" -ForegroundColor Magenta
Write-Host "║  Testing against: $BaseUrl                                        ║" -ForegroundColor Magenta
Write-Host "╚════════════════════════════════════════════════════════════════════╝" -ForegroundColor Magenta

# ============================================================================
# 1. HEALTH CHECK
# ============================================================================
Write-TestHeader "1. Health Check"
Test-Endpoint "GET" "/health" -description "Check if service is running"

# ============================================================================
# 2. VENDOR MANAGEMENT
# ============================================================================
Write-TestHeader "2. Vendor Management APIs"

# Create vendors
$vendor1 = @{
    name = "Acme Supplies"
    deliveryRate = 98.5
    qualityRating = 4.7
    priceScore = 85.0
}
$createdVendor1 = Test-Endpoint "POST" "/api/vendors" $vendor1 "Create vendor 1"

$vendor2 = @{
    name = "TechCorp Inc"
    deliveryRate = 92.0
    qualityRating = 4.3
    priceScore = 90.0
}
$createdVendor2 = Test-Endpoint "POST" "/api/vendors" $vendor2 "Create vendor 2"

$vendor3 = @{
    name = "Global Trading"
    deliveryRate = 85.0
    qualityRating = 4.0
    priceScore = 75.0
}
$createdVendor3 = Test-Endpoint "POST" "/api/vendors" $vendor3 "Create vendor 3"

Start-Sleep -Milliseconds 500

# Get all vendors
Test-Endpoint "GET" "/api/vendors" -description "Get all vendors"

# Get vendor by ID
if ($createdVendor1) {
    Test-Endpoint "GET" "/api/vendors/$($createdVendor1.id)" -description "Get vendor by ID"
}

# Update vendor
if ($createdVendor1) {
    $updateVendor = @{
        name = "Acme Supplies (Updated)"
        deliveryRate = 99.0
        qualityRating = 4.8
        priceScore = 88.0
    }
    Test-Endpoint "PUT" "/api/vendors/$($createdVendor1.id)" $updateVendor "Update vendor"
}

# Get vendors by performance score
Test-Endpoint "GET" "/api/vendors/performance?minScore=80" -description "Get high-performing vendors"

# Recalculate scores
Test-Endpoint "POST" "/api/vendors/recalculate-all-scores" -description "Recalculate all vendor scores"

# ============================================================================
# 3. PROCUREMENT WORKFLOW
# ============================================================================
Write-TestHeader "3. Procurement Workflow APIs"

if ($createdVendor1) {
    # Create procurement order
    $order = @{
        vendorId = $createdVendor1.id
        requestedBy = "john.doe"
        items = @(
            @{
                itemName = "Office Supplies"
                description = "Paper and pens"
                quantity = 100
                unitPrice = 5.50
            },
            @{
                itemName = "Printer Cartridges"
                description = "Black ink cartridges"
                quantity = 20
                unitPrice = 25.00
            }
        )
        notes = "Urgent order - needed for Q1 planning"
    }
    $createdOrder = Test-Endpoint "POST" "/api/procurement/orders" $order "Create procurement order"
    
    Start-Sleep -Milliseconds 500
    
    if ($createdOrder) {
        $orderId = $createdOrder.id
        
        # Get order by ID
        Test-Endpoint "GET" "/api/procurement/orders/$orderId" -description "Get order by ID"
        
        # Submit for approval
        Test-Endpoint "POST" "/api/procurement/orders/$orderId/submit" -description "Submit order for approval"
        
        Start-Sleep -Milliseconds 300
        
        # Approve order
        Test-Endpoint "POST" "/api/procurement/orders/$orderId/approve?approver=manager" -description "Approve order"
        
        Start-Sleep -Milliseconds 300
        
        # Start progress
        Test-Endpoint "POST" "/api/procurement/orders/$orderId/start" -description "Start order processing"
        
        Start-Sleep -Milliseconds 300
        
        # Mark delivered
        Test-Endpoint "POST" "/api/procurement/orders/$orderId/deliver" -description "Mark order as delivered"
    }
    
    # Get all orders
    Test-Endpoint "GET" "/api/procurement/orders" -description "Get all procurement orders"
    
    # Get orders by status
    Test-Endpoint "GET" "/api/procurement/orders/by-status/DELIVERED" -description "Get delivered orders"
    
    # Get orders by vendor
    Test-Endpoint "GET" "/api/procurement/orders/by-vendor/$($createdVendor1.id)" -description "Get orders for vendor"
}

# ============================================================================
# 4. ANALYTICS
# ============================================================================
Write-TestHeader "4. Analytics APIs"

# Comprehensive summary
Test-Endpoint "GET" "/api/analytics/summary" -description "Get comprehensive analytics summary"

# Performance distribution
Test-Endpoint "GET" "/api/analytics/distribution" -description "Get performance distribution"

# Top vendors
Test-Endpoint "GET" "/api/analytics/vendors/top?limit=5" -description "Get top 5 vendors"

# Vendors by score range
Test-Endpoint "GET" "/api/analytics/vendors/by-score-range?minScore=70&maxScore=95" -description "Get vendors in score range"

# Vendors by delivery rate
Test-Endpoint "GET" "/api/analytics/vendors/by-delivery-rate?minDeliveryRate=90" -description "Get vendors with high delivery rate"

# Vendors by quality rating
Test-Endpoint "GET" "/api/analytics/vendors/by-quality-rating?minRating=4.0" -description "Get vendors with quality rating >= 4.0"

# Search vendors
Test-Endpoint "GET" "/api/analytics/vendors/search?name=acme" -description "Search vendors by name"

# ============================================================================
# 5. AUDIT LOGS
# ============================================================================
Write-TestHeader "5. Audit Log APIs"

# Get audit logs
Test-Endpoint "GET" "/api/admin/audit" -description "Get all audit logs"

# Export CSV
try {
    $exportUrl = "$BaseUrl/api/admin/audit/export"
    Write-Host "`n[GET] $exportUrl" -ForegroundColor Yellow
    Write-Host "   Exporting audit logs to CSV" -ForegroundColor Gray
    
    $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $filename = "audit_export_$timestamp.csv"
    
    Invoke-WebRequest -Uri $exportUrl -Headers $Headers -OutFile $filename -ErrorAction Stop
    Write-Success "CSV export saved to $filename"
}
catch {
    Write-Failure "CSV export failed: $_"
}

# ============================================================================
# 6. TEST ENTITY (with validation)
# ============================================================================
Write-TestHeader "6. Test Entity APIs"

$testEntity = @{ name = "Test from comprehensive script" }
Test-Endpoint "POST" "/api/test" $testEntity "Create test entity"

Test-Endpoint "GET" "/api/test" -description "Get all test entities"

# ============================================================================
# 7. VALIDATION TESTS
# ============================================================================
Write-TestHeader "7. Validation Tests (Should Fail)"

# Invalid vendor (missing required field)
Write-Host "`nTesting validation - missing vendor name (should fail):" -ForegroundColor Yellow
$invalidVendor = @{
    deliveryRate = 95.0
    qualityRating = 4.5
    priceScore = 80.0
}
Test-Endpoint "POST" "/api/vendors" $invalidVendor "Create vendor with missing name (validation test)"

# Invalid delivery rate (out of range)
Write-Host "`nTesting validation - invalid delivery rate (should fail):" -ForegroundColor Yellow
$invalidVendor2 = @{
    name = "Test Vendor"
    deliveryRate = 150.0  # Invalid: > 100
    qualityRating = 4.5
    priceScore = 80.0
}
Test-Endpoint "POST" "/api/vendors" $invalidVendor2 "Create vendor with invalid delivery rate (validation test)"

# Non-existent resource
Write-Host "`nTesting error handling - non-existent resource (should fail):" -ForegroundColor Yellow
Test-Endpoint "GET" "/api/vendors/99999" -description "Get non-existent vendor (error test)"

# ============================================================================
# 8. CLEANUP (Optional)
# ============================================================================
Write-TestHeader "8. Cleanup Operations"

if ($createdVendor3) {
    Test-Endpoint "DELETE" "/api/vendors/$($createdVendor3.id)" -description "Delete vendor 3"
}

# ============================================================================
# TEST SUMMARY
# ============================================================================
Write-Host "`n" ("=" * 80) -ForegroundColor Magenta
Write-Host " TEST SUMMARY" -ForegroundColor Magenta
Write-Host ("=" * 80) -ForegroundColor Magenta
Write-Host "✓ Successful Tests: $SuccessCount" -ForegroundColor Green
Write-Host "✗ Failed Tests:     $FailureCount" -ForegroundColor Red
Write-Host ("=" * 80) -ForegroundColor Magenta

if ($FailureCount -eq 0) {
    Write-Host "`n🎉 All tests passed successfully!" -ForegroundColor Green
    exit 0
} else {
    Write-Host "`n⚠️  Some tests failed. Please review the output above." -ForegroundColor Yellow
    exit 1
}
