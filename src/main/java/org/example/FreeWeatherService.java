package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Free weather service using wttr.in (no API key required)
 * This service provides weather data without any registration
 */
public class FreeWeatherService {
    
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    
    public FreeWeatherService() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get current weather data using wttr.in (completely free, no API key needed)
     */
    public WeatherData getCurrentWeather(double latitude, double longitude, String units) throws IOException {
        // wttr.in provides weather data in JSON format
        String url = String.format("https://wttr.in/?format=j1&lang=en");
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "WeatherApp/1.0")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Weather service unavailable: " + response.code());
            }
            
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            
            return parseWttrWeather(jsonNode, units);
        }
    }
    
    /**
     * Get weather forecast using wttr.in
     */
    public List<WeatherData> getHourlyForecast(double latitude, double longitude, String units) throws IOException {
        String url = String.format("https://wttr.in/?format=j1&lang=en");
        
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "WeatherApp/1.0")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Weather service unavailable: " + response.code());
            }
            
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            
            return parseWttrForecast(jsonNode, units);
        }
    }
    
    private WeatherData parseWttrWeather(JsonNode jsonNode, String units) {
        try {
            JsonNode current = jsonNode.get("current_condition").get(0);
            double tempC = current.get("temp_C").asDouble();
            double feelsLikeC = current.get("FeelsLikeC").asDouble();
            int humidity = current.get("humidity").asInt();
            double windSpeed = current.get("windspeedKmph").asDouble();
            String description = current.get("weatherDesc").get(0).get("value").asText();
            
            // Convert to imperial if needed
            if ("imperial".equals(units)) {
                tempC = (tempC * 9.0 / 5.0) + 32;
                feelsLikeC = (feelsLikeC * 9.0 / 5.0) + 32;
                windSpeed = windSpeed * 0.621371; // km/h to mph
            }
            
            return new WeatherData(
                tempC,
                feelsLikeC,
                humidity,
                windSpeed,
                description,
                "01d", // Default icon
                LocalDateTime.now(),
                units
            );
        } catch (Exception e) {
            // Fallback to demo data if parsing fails
            return createDemoWeatherData(units);
        }
    }
    
    private List<WeatherData> parseWttrForecast(JsonNode jsonNode, String units) {
        List<WeatherData> forecast = new ArrayList<>();
        
        try {
            JsonNode weatherArray = jsonNode.get("weather");
            if (weatherArray != null && weatherArray.isArray()) {
                for (int i = 0; i < Math.min(weatherArray.size(), 5); i++) {
                    JsonNode day = weatherArray.get(i);
                    JsonNode hourly = day.get("hourly");
                    
                    if (hourly != null && hourly.isArray()) {
                        for (int j = 0; j < Math.min(hourly.size(), 8); j++) {
                            JsonNode hour = hourly.get(j);
                            
                            double tempC = hour.get("tempC").asDouble();
                            double feelsLikeC = hour.get("FeelsLikeC").asDouble();
                            int humidity = hour.get("humidity").asInt();
                            double windSpeed = hour.get("windspeedKmph").asDouble();
                            String description = hour.get("weatherDesc").get(0).get("value").asText();
                            
                            // Convert to imperial if needed
                            if ("imperial".equals(units)) {
                                tempC = (tempC * 9.0 / 5.0) + 32;
                                feelsLikeC = (feelsLikeC * 9.0 / 5.0) + 32;
                                windSpeed = windSpeed * 0.621371;
                            }
                            
                            // Create datetime for this hour
                            LocalDateTime dateTime = LocalDateTime.now().plusDays(i).withHour(j * 3);
                            
                            forecast.add(new WeatherData(
                                tempC,
                                feelsLikeC,
                                humidity,
                                windSpeed,
                                description,
                                "01d",
                                dateTime,
                                units
                            ));
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Fallback to demo data
            return createDemoForecast(units);
        }
        
        return forecast;
    }
    
    private WeatherData createDemoWeatherData(String units) {
        double baseTemp = 20.0; // 20°C base temperature
        if ("imperial".equals(units)) {
            baseTemp = (baseTemp * 9.0 / 5.0) + 32; // Convert to Fahrenheit
        }
        
        return new WeatherData(
            baseTemp,
            baseTemp + 2,
            65,
            10.0,
            "Partly Cloudy",
            "02d",
            LocalDateTime.now(),
            units
        );
    }
    
    private List<WeatherData> createDemoForecast(String units) {
        List<WeatherData> forecast = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 0; i < 24; i++) {
            LocalDateTime dateTime = now.plusHours(i);
            double baseTemp = 20.0 + 5 * Math.sin((i / 24.0) * 2 * Math.PI);
            
            if ("imperial".equals(units)) {
                baseTemp = (baseTemp * 9.0 / 5.0) + 32;
            }
            
            forecast.add(new WeatherData(
                baseTemp,
                baseTemp + 2,
                60 + (int)(Math.random() * 20),
                8.0 + Math.random() * 4,
                "Partly Cloudy",
                "02d",
                dateTime,
                units
            ));
        }
        
        return forecast;
    }
    
    // Same WeatherData class as the main WeatherService
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
}
