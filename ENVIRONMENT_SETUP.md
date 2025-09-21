# Environment Variable Setup Guide

This guide explains how to set up environment variables for the Weather App to securely store API keys.

## 🔑 OpenWeatherMap API Key Setup

### **Step 1: Get Your API Key**

1. Visit [OpenWeatherMap API](https://openweathermap.org/api)
2. Sign up for a free account
3. Go to [API Keys dashboard](https://home.openweathermap.org/api_keys)
4. Copy your API key (starts with letters/numbers)

### **Step 2: Set Environment Variable**

#### **Windows (PowerShell)**
```powershell
# Set for current session only
$env:OPENWEATHER_API_KEY = "your_actual_api_key_here"

# Set permanently for your user account
[Environment]::SetEnvironmentVariable("OPENWEATHER_API_KEY", "your_actual_api_key_here", "User")
```

#### **Windows (Command Prompt)**
```cmd
# Set for current session only
set OPENWEATHER_API_KEY=your_actual_api_key_here

# Set permanently
setx OPENWEATHER_API_KEY "your_actual_api_key_here"
```

#### **Windows (System Properties)**
1. Right-click "This PC" → Properties
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Under "User variables", click "New"
5. Variable name: `OPENWEATHER_API_KEY`
6. Variable value: `your_actual_api_key_here`

#### **Linux/macOS (Terminal)**
```bash
# Add to your shell profile (~/.bashrc, ~/.zshrc, etc.)
echo 'export OPENWEATHER_API_KEY="your_actual_api_key_here"' >> ~/.bashrc

# Or set for current session
export OPENWEATHER_API_KEY="your_actual_api_key_here"

# Reload your shell profile
source ~/.bashrc
```

#### **macOS (Environment Variables)**
```bash
# Create/edit ~/.zshrc (for zsh shell)
echo 'export OPENWEATHER_API_KEY="your_actual_api_key_here"' >> ~/.zshrc
source ~/.zshrc
```

### **Step 3: Verify Setup**

#### **Windows (PowerShell)**
```powershell
# Check if environment variable is set
echo $env:OPENWEATHER_API_KEY
```

#### **Windows (Command Prompt)**
```cmd
# Check if environment variable is set
echo %OPENWEATHER_API_KEY%
```

#### **Linux/macOS**
```bash
# Check if environment variable is set
echo $OPENWEATHER_API_KEY
```

### **Step 4: Test the Application**

1. **Set the environment variable** using one of the methods above
2. **Run the application:**
   ```bash
   mvn javafx:run
   ```
3. **Check the console output** - you should see:
   ```
   ✅ Using API key from environment variable OPENWEATHER_API_KEY
   ✅ OpenWeatherMap API key configured successfully!
   ```

## 🔄 Fallback Behavior

The application has intelligent fallback behavior:

1. **Primary**: Uses environment variable `OPENWEATHER_API_KEY`
2. **Fallback**: Uses `weather.api.key` from `application.properties`
3. **Final Fallback**: Uses free weather service (wttr.in)
4. **Demo Mode**: Shows sample data if all APIs fail

## 🛡️ Security Benefits

Using environment variables provides several security advantages:

- ✅ **No API keys in source code**
- ✅ **No API keys in version control**
- ✅ **Different keys for different environments**
- ✅ **Easy key rotation**
- ✅ **Team members can use their own keys**

## 🚨 Troubleshooting

### **Environment Variable Not Found**
```
❌ No valid API key found in environment variable OPENWEATHER_API_KEY or application.properties
```

**Solutions:**
1. Verify the environment variable is set correctly
2. Restart your terminal/IDE after setting the variable
3. Check the variable name spelling: `OPENWEATHER_API_KEY`
4. Use the fallback method in `application.properties`

### **API Key Invalid**
```
401 Unauthorized
```

**Solutions:**
1. Verify your API key is correct
2. Check if the API key is activated (may take a few minutes)
3. Ensure you haven't exceeded API limits
4. The app will automatically fall back to the free weather service

### **Still Using Properties File**
```
⚠️ Using API key from application.properties (consider using environment variable for better security)
```

**Solutions:**
1. Remove or comment out `weather.api.key` in `application.properties`
2. Set the environment variable properly
3. Restart the application

## 📝 IDE-Specific Setup

### **IntelliJ IDEA**
1. Run → Edit Configurations
2. Select your application
3. Environment variables → Add
4. Name: `OPENWEATHER_API_KEY`
5. Value: `your_api_key_here`

### **Eclipse**
1. Run → Run Configurations
2. Select your application
3. Environment tab → Add
4. Name: `OPENWEATHER_API_KEY`
5. Value: `your_api_key_here`

### **VS Code**
1. Create `.vscode/launch.json`:
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Weather App",
            "request": "launch",
            "mainClass": "org.example.Main",
            "env": {
                "OPENWEATHER_API_KEY": "your_api_key_here"
            }
        }
    ]
}
```

## 🔧 Advanced Configuration

### **Multiple Environments**
You can set different API keys for different environments:

```bash
# Development
export OPENWEATHER_API_KEY="dev_api_key_here"

# Production
export OPENWEATHER_API_KEY="prod_api_key_here"
```

### **Docker Environment**
```dockerfile
# In your Dockerfile
ENV OPENWEATHER_API_KEY=your_api_key_here
```

### **Docker Compose**
```yaml
# In docker-compose.yml
services:
  weather-app:
    environment:
      - OPENWEATHER_API_KEY=your_api_key_here
```

---

## ✅ Quick Start Checklist

- [ ] Get OpenWeatherMap API key
- [ ] Set `OPENWEATHER_API_KEY` environment variable
- [ ] Verify variable is set correctly
- [ ] Run application: `mvn javafx:run`
- [ ] Check console for success message
- [ ] Test weather data loading

Your API key is now securely stored and the application will use it automatically!
