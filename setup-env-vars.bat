@echo off
REM Weather App Environment Variables Setup Script
REM Run this script to set up environment variables for the Weather App

echo.
echo 🌤️ Weather App Environment Variables Setup
echo =============================================
echo.

REM Get user input for environment variables
echo Please provide the following information:
echo.

REM API Key
set /p API_KEY="Enter your OpenWeatherMap API Key: "
if not "%API_KEY%"=="" (
    setx OPENWEATHER_API_KEY "%API_KEY%"
    echo ✅ OpenWeatherMap API Key set
)

REM Database Username
set /p DB_USER="Enter your Database Username (or press Enter for 'system'): "
if "%DB_USER%"=="" set DB_USER=system
setx DB_USERNAME "%DB_USER%"
echo ✅ Database Username set

REM Database Password
set /p DB_PASS="Enter your Database Password (or press Enter for 'kavinkishore'): "
if "%DB_PASS%"=="" set DB_PASS=kavinkishore
setx DB_PASSWORD "%DB_PASS%"
echo ✅ Database Password set

echo.
echo 🎉 Environment Variables Setup Complete!
echo =========================================
echo ✅ OPENWEATHER_API_KEY: %API_KEY%
echo ✅ DB_USERNAME: %DB_USER%
echo ✅ DB_PASSWORD: %DB_PASS%
echo.
echo 📝 Note: You may need to restart your IDE/terminal for changes to take effect.
echo 🚀 You can now run your Weather App!
echo.
pause
