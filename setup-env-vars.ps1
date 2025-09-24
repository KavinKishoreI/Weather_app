# Weather App Environment Variables Setup Script
# Run this script to set up environment variables for the Weather App

Write-Host "🌤️ Weather App Environment Variables Setup" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

# Function to set environment variable
function Set-EnvVar {
    param(
        [string]$Name,
        [string]$Value,
        [string]$Description
    )
    
    Write-Host "Setting $Name..." -ForegroundColor Yellow
    [Environment]::SetEnvironmentVariable($Name, $Value, "User")
    Write-Host "✅ $Description" -ForegroundColor Green
}

# Get user input for environment variables
Write-Host "`nPlease provide the following information:" -ForegroundColor White

# API Key
$apiKey = Read-Host "Enter your OpenWeatherMap API Key"
if ($apiKey) {
    Set-EnvVar "OPENWEATHER_API_KEY" $apiKey "OpenWeatherMap API Key set"
}

# Database Username
$dbUsername = Read-Host "Enter your Database Username"
if (-not $dbUsername) {
    Write-Host "❌ Database username is required!" -ForegroundColor Red
    exit 1
}
Set-EnvVar "DB_USERNAME" $dbUsername "Database Username set"

# Database Password
$dbPassword = Read-Host "Enter your Database Password"
if (-not $dbPassword) {
    Write-Host "❌ Database password is required!" -ForegroundColor Red
    exit 1
}
Set-EnvVar "DB_PASSWORD" $dbPassword "Database Password set"

Write-Host "`n🎉 Environment Variables Setup Complete!" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green
Write-Host "✅ OPENWEATHER_API_KEY: $apiKey" -ForegroundColor Green
Write-Host "✅ DB_USERNAME: $dbUsername" -ForegroundColor Green
Write-Host "✅ DB_PASSWORD: [HIDDEN]" -ForegroundColor Green

Write-Host "`n📝 Note: You may need to restart your IDE/terminal for changes to take effect." -ForegroundColor Yellow
Write-Host "🚀 You can now run your Weather App!" -ForegroundColor Cyan