package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager that handles environment variables and properties file fallbacks
 */
public class ConfigManager {
    private static ConfigManager instance;
    private final Properties props;
    
    private ConfigManager() {
        this.props = new Properties();
        loadProperties();
    }
    
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            System.err.println("Failed to load application.properties: " + e.getMessage());
        }
    }
    
    /**
     * Get API key with environment variable priority
     */
    public String getApiKey() {
        // First try environment variable
        String envApiKey = System.getenv("OPENWEATHER_API_KEY");
        if (envApiKey != null && !envApiKey.trim().isEmpty()) {
            System.out.println("✅ Using API key from environment variable OPENWEATHER_API_KEY");
            return envApiKey.trim();
        }
        
        // Fallback to properties file
        String propApiKey = props.getProperty("weather.api.key");
        if (propApiKey != null && !propApiKey.trim().isEmpty() && 
            !"YOUR_ACTUAL_API_KEY_HERE".equals(propApiKey) && 
            !"YOUR_OPENWEATHER_API_KEY".equals(propApiKey)) {
            System.out.println("⚠️  Using API key from application.properties (consider using environment variable for better security)");
            return propApiKey.trim();
        }
        
        // No valid API key found
        System.out.println("❌ No valid API key found in environment variable OPENWEATHER_API_KEY or application.properties");
        return "NO_API_KEY";
    }
    
    /**
     * Get database username with environment variable priority
     */
    public String getDbUsername() {
        // First try environment variable
        String envUsername = System.getenv("DB_USERNAME");
        if (envUsername != null && !envUsername.trim().isEmpty()) {
            System.out.println("✅ Using database username from environment variable DB_USERNAME");
            return envUsername.trim();
        }
        
        // Fallback to properties file
        String propUsername = props.getProperty("database.username");
        if (propUsername != null && !propUsername.trim().isEmpty()) {
            System.out.println("⚠️  Using database username from application.properties (consider using environment variable for better security)");
            return propUsername.trim();
        }
        
        // Default fallback
        System.out.println("⚠️  Using default database username 'system'");
        return "system";
    }
    
    /**
     * Get database password with environment variable priority
     */
    public String getDbPassword() {
        // First try environment variable
        String envPassword = System.getenv("DB_PASSWORD");
        if (envPassword != null && !envPassword.trim().isEmpty()) {
            System.out.println("✅ Using database password from environment variable DB_PASSWORD");
            return envPassword.trim();
        }
        
        // Fallback to properties file
        String propPassword = props.getProperty("database.password");
        if (propPassword != null && !propPassword.trim().isEmpty()) {
            System.out.println("⚠️  Using database password from application.properties (consider using environment variable for better security)");
            return propPassword.trim();
        }
        
        // Default fallback
        System.out.println("⚠️  Using default database password");
        return "kavinkishore";
    }
    
    /**
     * Get database URL
     */
    public String getDbUrl() {
        return props.getProperty("database.url", "jdbc:oracle:thin:@localhost:1521:xe");
    }
    
    /**
     * Get weather service type
     */
    public String getWeatherServiceType() {
        return props.getProperty("weather.service.type", "openweathermap");
    }
    
    /**
     * Get weather API base URL
     */
    public String getWeatherApiBaseUrl() {
        return props.getProperty("weather.api.base.url", "https://api.openweathermap.org/data/2.5");
    }
    
    /**
     * Get weather API geocoding URL
     */
    public String getWeatherApiGeocodingUrl() {
        return props.getProperty("weather.api.geocoding.url", "https://api.openweathermap.org/geo/1.0/direct");
    }
    
    /**
     * Print current configuration status
     */
    public void printConfigStatus() {
        System.out.println("\n🔧 Configuration Status:");
        System.out.println("========================");
        
        // API Key status
        String apiKey = getApiKey();
        if ("NO_API_KEY".equals(apiKey)) {
            System.out.println("❌ API Key: Not configured");
        } else {
            System.out.println("✅ API Key: Configured (" + (apiKey.length() > 8 ? apiKey.substring(0, 8) + "..." : apiKey) + ")");
        }
        
        // Database status
        System.out.println("✅ DB Username: " + getDbUsername());
        System.out.println("✅ DB Password: " + (getDbPassword().length() > 3 ? "***" + getDbPassword().substring(getDbPassword().length()-3) : "***"));
        System.out.println("✅ DB URL: " + getDbUrl());
        System.out.println("✅ Weather Service: " + getWeatherServiceType());
        System.out.println("========================");
    }
}
