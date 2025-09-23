# Environment Variables Setup Guide

## 🔧 Environment Variable Persistence

### **Temporary (Session-Only)**
```powershell
# These only last for the current PowerShell session
$env:OPENWEATHER_API_KEY="your_api_key"
$env:DB_USERNAME="your_username"
$env:DB_PASSWORD="your_password"
```

### **Permanent (All Sessions) - RECOMMENDED**
```powershell
# These persist across all sessions and reboots
[Environment]::SetEnvironmentVariable("OPENWEATHER_API_KEY", "your_api_key", "User")
[Environment]::SetEnvironmentVariable("DB_USERNAME", "your_username", "User")
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "your_password", "User")
```

## 🚀 Quick Setup Options

### **Option 1: Use the Setup Script (Easiest)**
```powershell
# Run the PowerShell setup script
.\setup-env-vars.ps1
```

### **Option 2: Manual PowerShell Setup**
```powershell
# Set all three environment variables permanently
[Environment]::SetEnvironmentVariable("OPENWEATHER_API_KEY", "6b990d70706ea1c5c1f48a8619836a65", "User")
[Environment]::SetEnvironmentVariable("DB_USERNAME", "system", "User")
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "kavinkishore", "User")

# Verify they're set
echo "API Key: $env:OPENWEATHER_API_KEY"
echo "DB Username: $env:DB_USERNAME"
echo "DB Password: $env:DB_PASSWORD"
```

### **Option 3: Command Prompt Setup**
```cmd
# Set environment variables using setx command
setx OPENWEATHER_API_KEY "6b990d70706ea1c5c1f48a8619836a65"
setx DB_USERNAME "system"
setx DB_PASSWORD "kavinkishore"
```

## 🔍 Verification

### **Check if Environment Variables are Set:**
```powershell
# Check all three variables
echo "OPENWEATHER_API_KEY: $env:OPENWEATHER_API_KEY"
echo "DB_USERNAME: $env:DB_USERNAME"
echo "DB_PASSWORD: $env:DB_PASSWORD"
```

### **Test Your App:**
1. Set the environment variables using any method above
2. **Restart your IDE/terminal** (important!)
3. Run your JavaFX application
4. Check the console output for configuration status

## 📝 Important Notes

### **Persistence Levels:**
- **User Level**: Available to your user account across all sessions
- **System Level**: Available to all users (requires admin rights)
- **Session Level**: Only available in current session

### **When Environment Variables Take Effect:**
- **New processes**: Immediately (new terminal windows, IDE restarts)
- **Existing processes**: Need to restart the application
- **IDE**: Usually need to restart the IDE

### **Fallback Behavior:**
If environment variables aren't set, the app will:
1. Try environment variables first
2. Fall back to `application.properties` file
3. Use default values as last resort

## 🛠️ Troubleshooting

### **Environment Variables Not Working:**
1. **Restart your IDE/terminal** after setting variables
2. **Check if variables are set**: `echo $env:VARIABLE_NAME`
3. **Verify in new terminal window** (not the one where you set them)
4. **Check application.properties** as fallback

### **App Still Using Properties File:**
This is normal! The app will show:
```
⚠️ Using API key from application.properties (consider using environment variable for better security)
```

This means environment variables aren't set, so it's using the properties file (which is fine).

## 🎯 Best Practice

**For Development:**
- Use the setup script once
- Environment variables will persist across all sessions
- No need to set them again

**For Production:**
- Set environment variables on the server
- Never put sensitive data in properties files
- Use environment variables for all secrets
