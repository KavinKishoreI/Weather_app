# 🌤️ Weather App Environment Variables Setup Guide

This guide helps you set up the required environment variables for the Weather App.

## 🔑 Required Environment Variables

- `OPENWEATHER_API_KEY` - Your OpenWeatherMap API key
- `DB_USERNAME` - Your database username  
- `DB_PASSWORD` - Your database password

## 🚀 Quick Setup Options

### **Option 1: Use Setup Scripts (Recommended)**

**Windows PowerShell:**
```powershell
.\setup-env-vars.ps1
```

**Windows Command Prompt:**
```cmd
setup-env-vars.bat
```

### **Option 2: Manual PowerShell Setup**
```powershell
# Set all three environment variables permanently
[Environment]::SetEnvironmentVariable("OPENWEATHER_API_KEY", "YOUR_API_KEY_HERE", "User")
[Environment]::SetEnvironmentVariable("DB_USERNAME", "YOUR_USERNAME_HERE", "User")
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "YOUR_PASSWORD_HERE", "User")

# Verify they're set
echo "API Key: $env:OPENWEATHER_API_KEY"
echo "DB Username: $env:DB_USERNAME"
echo "DB Password: $env:DB_PASSWORD"
```

### **Option 3: Command Prompt Setup**
```cmd
# Set environment variables using setx command
setx OPENWEATHER_API_KEY "YOUR_API_KEY_HERE"
setx DB_USERNAME "YOUR_USERNAME_HERE"
setx DB_PASSWORD "YOUR_PASSWORD_HERE"
```

### **Option 4: Linux/macOS Setup**
```bash
# Add to your shell profile (~/.bashrc, ~/.zshrc, etc.)
export OPENWEATHER_API_KEY="YOUR_API_KEY_HERE"
export DB_USERNAME="YOUR_USERNAME_HERE"
export DB_PASSWORD="YOUR_PASSWORD_HERE"
```

## 🔍 Verify Your Setup

Run the test script to verify your environment variables:
```powershell
.\test-env-vars.ps1
```

## 🛠️ Get Your API Key

1. Visit [OpenWeatherMap API](https://openweathermap.org/api)
2. Sign up for a free account
3. Go to [API Keys dashboard](https://home.openweathermap.org/api_keys)
4. Copy your API key

## 🗄️ Database Setup

Make sure your Oracle database is running and accessible:
- Default connection: `jdbc:oracle:thin:@localhost:1521:xe`
- Ensure your database user has proper permissions

## 🚨 Security Notes

- **Never commit real credentials to version control**
- **Use environment variables for all sensitive data**
- **Keep your API keys secure and don't share them**

## 🆘 Troubleshooting

### **Environment Variables Not Working?**
1. Restart your IDE/terminal after setting variables
2. Check if variables are set: `echo $env:VARIABLE_NAME`
3. Verify spelling of variable names

### **Database Connection Issues?**
1. Check if Oracle database is running
2. Verify connection string in `hibernate.cfg.xml`
3. Test database credentials

### **API Key Issues?**
1. Verify your OpenWeatherMap API key is valid
2. Check if you've exceeded API rate limits
3. Ensure the key has proper permissions

## 📞 Support

If you encounter issues:
1. Check the console output for error messages
2. Verify all environment variables are set correctly
3. Ensure your database is accessible
4. Check your internet connection for API calls

---

**✅ You're all set! Your Weather App should now work with proper environment variables.**