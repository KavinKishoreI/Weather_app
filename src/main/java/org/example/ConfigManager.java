package org.example;

/**
 * Configuration manager that handles environment variables only - no fallbacks
 */
public class ConfigManager {
    private static ConfigManager instance;
    
    private ConfigManager() {
        // No properties file loading needed
    }
    
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Get API key from environment variable only
     */
    public String getApiKey() {
        String envApiKey = System.getenv("OPENWEATHER_API_KEY");
        if (envApiKey == null || envApiKey.trim().isEmpty()) {
            throw new IllegalStateException("Environment variable OPENWEATHER_API_KEY is not set or is empty");
        }
        
        System.out.println("✅ Using API key from environment variable OPENWEATHER_API_KEY");
        return envApiKey.trim();
    }
    
    /**
     * Get database username from environment variable only
     */
    public String getDbUsername() {
        String envUsername = System.getenv("DB_USERNAME");
        if (envUsername == null || envUsername.trim().isEmpty()) {
            throw new IllegalStateException("Environment variable DB_USERNAME is not set or is empty");
        }
        
        System.out.println("✅ Using database username from environment variable DB_USERNAME");
        return envUsername.trim();
    }
    
    /**
     * Get database password from environment variable only
     */
    public String getDbPassword() {
        String envPassword = System.getenv("DB_PASSWORD");
        if (envPassword == null || envPassword.trim().isEmpty()) {
            throw new IllegalStateException("Environment variable DB_PASSWORD is not set or is empty");
        }
        
        System.out.println("✅ Using database password from environment variable DB_PASSWORD");
        return envPassword.trim();
    }
    
    /**
     * Get database URL with default fallback
     */
    public String getDbUrl() {
        String envDbUrl = System.getenv("DB_URL");
        if (envDbUrl != null && !envDbUrl.trim().isEmpty()) {
            System.out.println("✅ Using database URL from environment variable DB_URL");
            return envDbUrl.trim();
        }
        
        // Default to Oracle XE on localhost
        System.out.println("⚠️ Using default database URL (jdbc:oracle:thin:@localhost:1521:xe)");
        return "jdbc:oracle:thin:@localhost:1521:xe";
    }
    
    /**
     * Get weather service type with default fallback
     */
    public String getWeatherServiceType() {
        String envServiceType = System.getenv("WEATHER_SERVICE_TYPE");
        if (envServiceType != null && !envServiceType.trim().isEmpty()) {
            System.out.println("✅ Using weather service type from environment variable WEATHER_SERVICE_TYPE");
            return envServiceType.trim();
        }
        
        System.out.println("⚠️ Using default weather service type (openweathermap)");
        return "openweathermap";
    }
    
    /**
     * Get weather API base URL with default fallback
     */
    public String getWeatherApiBaseUrl() {
        String envBaseUrl = System.getenv("WEATHER_API_BASE_URL");
        if (envBaseUrl != null && !envBaseUrl.trim().isEmpty()) {
            System.out.println("✅ Using weather API base URL from environment variable WEATHER_API_BASE_URL");
            return envBaseUrl.trim();
        }
        
        System.out.println("⚠️ Using default weather API base URL (https://api.openweathermap.org/data/2.5)");
        return "https://api.openweathermap.org/data/2.5";
    }
    
    /**
     * Get weather API geocoding URL with default fallback
     */
    public String getWeatherApiGeocodingUrl() {
        String envGeocodingUrl = System.getenv("WEATHER_API_GEOCODING_URL");
        if (envGeocodingUrl != null && !envGeocodingUrl.trim().isEmpty()) {
            System.out.println("✅ Using weather API geocoding URL from environment variable WEATHER_API_GEOCODING_URL");
            return envGeocodingUrl.trim();
        }
        
        System.out.println("⚠️ Using default weather API geocoding URL (https://api.openweathermap.org/geo/1.0/direct)");
        return "https://api.openweathermap.org/geo/1.0/direct";
    }
    
    /**
     * Print current configuration status - environment variables only
     */
    public void printConfigStatus() {
        System.out.println("\n🔧 Configuration Status (Environment Variables Only):");
        System.out.println("=====================================================");
        
        try {
            // API Key status
            String apiKey = getApiKey();
            System.out.println("✅ API Key: Configured (" + (apiKey.length() > 8 ? apiKey.substring(0, 8) + "..." : apiKey) + ")");
        } catch (IllegalStateException e) {
            System.out.println("❌ API Key: " + e.getMessage());
        }
        
        try {
            // Database status
            System.out.println("✅ DB Username: " + getDbUsername());
        } catch (IllegalStateException e) {
            System.out.println("❌ DB Username: " + e.getMessage());
        }
        
        try {
            String password = getDbPassword();
            System.out.println("✅ DB Password: " + (password.length() > 3 ? "***" + password.substring(password.length()-3) : "***"));
        } catch (IllegalStateException e) {
            System.out.println("❌ DB Password: " + e.getMessage());
        }
        
        try {
            System.out.println("✅ DB URL: " + getDbUrl());
        } catch (IllegalStateException e) {
            System.out.println("❌ DB URL: " + e.getMessage());
        }
        
        try {
            System.out.println("✅ Weather Service: " + getWeatherServiceType());
        } catch (IllegalStateException e) {
            System.out.println("❌ Weather Service: " + e.getMessage());
        }
        
        try {
            System.out.println("✅ Weather API Base URL: " + getWeatherApiBaseUrl());
        } catch (IllegalStateException e) {
            System.out.println("❌ Weather API Base URL: " + e.getMessage());
        }
        
        try {
            System.out.println("✅ Weather API Geocoding URL: " + getWeatherApiGeocodingUrl());
        } catch (IllegalStateException e) {
            System.out.println("❌ Weather API Geocoding URL: " + e.getMessage());
        }
        
        System.out.println("=====================================================");
        System.out.println("Required Environment Variables:");
        System.out.println("- OPENWEATHER_API_KEY (for weather data)");
        System.out.println("- DB_USERNAME (for database connection)");
        System.out.println("- DB_PASSWORD (for database connection)");
        System.out.println("");
        System.out.println("Optional Environment Variables (with defaults):");
        System.out.println("- DB_URL (defaults to jdbc:oracle:thin:@localhost:1521:xe)");
        System.out.println("- WEATHER_SERVICE_TYPE (defaults to openweathermap)");
        System.out.println("- WEATHER_API_BASE_URL (defaults to https://api.openweathermap.org/data/2.5)");
        System.out.println("- WEATHER_API_GEOCODING_URL (defaults to https://api.openweathermap.org/geo/1.0/direct)");
    }
}
