# Test Configuration Script
Write-Host "Testing Environment Variables..." -ForegroundColor Cyan

# Test API Key
$apiKey = $env:OPENWEATHER_API_KEY
if ($apiKey) {
    Write-Host "✅ OPENWEATHER_API_KEY: $apiKey" -ForegroundColor Green
} else {
    Write-Host "❌ OPENWEATHER_API_KEY: Not set" -ForegroundColor Red
}

# Test DB Username
$dbUser = $env:DB_USERNAME
if ($dbUser) {
    Write-Host "✅ DB_USERNAME: $dbUser" -ForegroundColor Green
} else {
    Write-Host "❌ DB_USERNAME: Not set" -ForegroundColor Red
}

# Test DB Password
$dbPass = $env:DB_PASSWORD
if ($dbPass) {
    Write-Host "✅ DB_PASSWORD: $dbPass" -ForegroundColor Green
} else {
    Write-Host "❌ DB_PASSWORD: Not set" -ForegroundColor Red
}

Write-Host "`nNote: Environment variables may not be visible in current session." -ForegroundColor Yellow
Write-Host "They will be available in new terminal windows/IDE restarts." -ForegroundColor Yellow
