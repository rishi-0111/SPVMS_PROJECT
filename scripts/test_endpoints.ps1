<#
PowerShell script to test all endpoints in the vendor-management app.
Usage:
  .\test_endpoints.ps1                # uses default http://localhost:8080
  .\test_endpoints.ps1 -BaseUrl http://localhost:8081
#>
param(
    [string]$BaseUrl = "http://localhost:8080"
)

$Headers = @{ 'Content-Type' = 'application/json' }

function SafeWrite($title, $obj){
    Write-Host "---- $title ----" -ForegroundColor Cyan
    if ($null -eq $obj) { Write-Host "(no content)"; return }
    try { $json = $obj | ConvertTo-Json -Depth 6; Write-Host $json } catch { Write-Host $obj }
    Write-Host "--------------------`n"
}

function Test-GET($path){
    $url = "$BaseUrl$path"
    Write-Host "GET $url"
    try {
        $res = Invoke-RestMethod -Uri $url -Method GET -Headers $Headers -ErrorAction Stop
        SafeWrite "Response for GET $path" $res
    } catch {
        Write-Host "ERROR GET $path : $_" -ForegroundColor Red
    }
}

function Test-POST($path, $body){
    $url = "$BaseUrl$path"
    Write-Host "POST $url"
    try {
        $res = Invoke-RestMethod -Uri $url -Method POST -Headers $Headers -Body ($body | ConvertTo-Json -Depth 6) -ErrorAction Stop
        SafeWrite "Response for POST $path" $res
    } catch {
        Write-Host "ERROR POST $path : $_" -ForegroundColor Red
    }
}

Write-Host "Testing endpoints against $BaseUrl`n" -ForegroundColor Green

# 1. Health
Test-GET "/health"

# 2. Audit list
Test-GET "/api/admin/audit"

# 3. Audit export (download CSV)
try {
    $exportUrl = "$BaseUrl/api/admin/audit/export"
    Write-Host "GET $exportUrl -> audit_export.csv"
    Invoke-WebRequest -Uri $exportUrl -OutFile "audit_export.csv" -ErrorAction Stop
    Write-Host "Saved audit_export.csv`n" -ForegroundColor Green
} catch {
    Write-Host "ERROR downloading audit export: $_" -ForegroundColor Red
}

# 4. Create TestDTO
$testBody = @{ name = "example test from script" }
Test-POST "/api/test" $testBody

# 5. List TestDTO
Test-GET "/api/test"

# 6. Vendor performance (with query param)
Test-GET "/api/vendors/performance?minScore=80"

Write-Host "Finished tests." -ForegroundColor Green
exit 0
