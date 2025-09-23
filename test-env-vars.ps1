# Test Environment Variables Script
# This script tests if your environment variables are set correctly

Write-Host "🔍 Testing Environment Variables" -ForegroundColor Cyan
Write-Host "===============================" -ForegroundColor Cyan

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

Write-Host "`n📝 Summary:" -ForegroundColor Yellow
if ($apiKey -and $dbUser -and $dbPass) {
    Write-Host "🎉 All environment variables are set! Your app should use them." -ForegroundColor Green
} else {
    Write-Host "⚠️  Some environment variables are missing. The app will use properties file fallback." -ForegroundColor Yellow
    Write-Host "💡 Run .\setup-env-vars.ps1 to set them up." -ForegroundColor Cyan
}
