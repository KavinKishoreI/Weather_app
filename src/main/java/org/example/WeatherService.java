package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WeatherService {
    private final String apiKey;
    private final String baseUrl;
    private final String geocodingUrl;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    
    public WeatherService() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        
        // Use ConfigManager for configuration
        ConfigManager config = ConfigManager.getInstance();
        this.apiKey = config.getApiKey();
        this.baseUrl = config.getWeatherApiBaseUrl();
        this.geocodingUrl = config.getWeatherApiGeocodingUrl();
        
        // Validate API key configuration
        validateApiKey();
    }
    
    
    private void validateApiKey() {
        if ("NO_API_KEY".equals(this.apiKey) || 
            "YOUR_OPENWEATHER_API_KEY".equals(this.apiKey) || 
            "YOUR_ACTUAL_API_KEY_HERE".equals(this.apiKey)) {
            System.out.println("⚠️  WARNING: No valid OpenWeatherMap API key configured!");
            System.out.println("📖 Setup instructions:");
            System.out.println("   1. Get free API key: https://openweathermap.org/api");
            System.out.println("   2. Set environment variable: OPENWEATHER_API_KEY=your_api_key_here");
            System.out.println("   3. Or update application.properties (less secure)");
            System.out.println("🌤️  App will use free weather service as fallback.");
        } else {
            System.out.println("✅ OpenWeatherMap API key configured successfully!");
        }
    }
    
    public WeatherData getCurrentWeather(double latitude, double longitude, String units) throws IOException {
        String url = String.format("%s/weather?lat=%.4f&lon=%.4f&appid=%s&units=%s", 
                                 baseUrl, latitude, longitude, apiKey, units);
        
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            
            return parseCurrentWeather(jsonNode, units);
        }
    }
    
    public List<WeatherData> getHourlyForecast(double latitude, double longitude, String units) throws IOException {
        String url = String.format("%s/forecast?lat=%.4f&lon=%.4f&appid=%s&units=%s", 
                                 baseUrl, latitude, longitude, apiKey, units);
        
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            
            return parseForecast(jsonNode, units);
        }
    }
    
    public LocationData getLocationByName(String locationName) throws IOException {
        String url = String.format("%s?q=%s&limit=1&appid=%s", 
                                 geocodingUrl, locationName.replace(" ", "%20"), apiKey);
        
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            
            if (jsonNode.isArray() && jsonNode.size() > 0) {
                JsonNode location = jsonNode.get(0);
                return new LocationData(
                    location.get("name").asText(),
                    location.get("lat").asDouble(),
                    location.get("lon").asDouble(),
                    location.get("country").asText()
                );
            }
            
            throw new IOException("Location not found: " + locationName);
        }
    }
    
    private WeatherData parseCurrentWeather(JsonNode jsonNode, String units) {
        double temperature = jsonNode.get("main").get("temp").asDouble();
        double feelsLike = jsonNode.get("main").get("feels_like").asDouble();
        int humidity = jsonNode.get("main").get("humidity").asInt();
        double windSpeed = jsonNode.get("wind").get("speed").asDouble();
        String description = jsonNode.get("weather").get(0).get("description").asText();
        String icon = jsonNode.get("weather").get(0).get("icon").asText();
        
        return new WeatherData(
            temperature,
            feelsLike,
            humidity,
            windSpeed,
            description,
            icon,
            LocalDateTime.now(),
            units
        );
    }
    
    private List<WeatherData> parseForecast(JsonNode jsonNode, String units) {
        List<WeatherData> forecast = new ArrayList<>();
        JsonNode list = jsonNode.get("list");
        
        for (JsonNode item : list) {
            double temperature = item.get("main").get("temp").asDouble();
            double feelsLike = item.get("main").get("feels_like").asDouble();
            int humidity = item.get("main").get("humidity").asInt();
            double windSpeed = item.get("wind").get("speed").asDouble();
            String description = item.get("weather").get(0).get("description").asText();
            String icon = item.get("weather").get(0).get("icon").asText();
            
            // Parse datetime
            String dateTimeStr = item.get("dt_txt").asText();
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            forecast.add(new WeatherData(
                temperature,
                feelsLike,
                humidity,
                windSpeed,
                description,
                icon,
                dateTime,
                units
            ));
        }
        
        return forecast;
    }
    
    // Data classes
    public static class WeatherData {
        private final double temperature;
        private final double feelsLike;
        private final int humidity;
        private final double windSpeed;
        private final String description;
        private final String icon;
        private final LocalDateTime dateTime;
        private final String units;
        
        public WeatherData(double temperature, double feelsLike, int humidity, 
                         double windSpeed, String description, String icon, 
                         LocalDateTime dateTime, String units) {
            this.temperature = temperature;
            this.feelsLike = feelsLike;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.description = description;
            this.icon = icon;
            this.dateTime = dateTime;
            this.units = units;
        }
        
        // Getters
        public double getTemperature() { return temperature; }
        public double getFeelsLike() { return feelsLike; }
        public int getHumidity() { return humidity; }
        public double getWindSpeed() { return windSpeed; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
        public LocalDateTime getDateTime() { return dateTime; }
        public String getUnits() { return units; }
    }
    
    public static class LocationData {
        private final String name;
        private final double latitude;
        private final double longitude;
        private final String country;
        
        public LocationData(String name, double latitude, double longitude, String country) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.country = country;
        }
        
        // Getters
        public String getName() { return name; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getCountry() { return country; }
    }
}
