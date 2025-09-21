package org.example.ui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import org.example.DatabaseService;
import org.example.SavedLocation;
import org.example.User;
import org.example.WeatherService;
import org.example.FreeWeatherService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DashboardController {

    @FXML private BorderPane root;
    @FXML private Label welcomeLabel;

    @FXML private ChoiceBox<String> unitsChoice;

    @FXML private TableView<LocationRow> locationsTable;
    @FXML private TableColumn<LocationRow, String> colName;
    @FXML private TableColumn<LocationRow, Number> colLat;
    @FXML private TableColumn<LocationRow, Number> colLon;

    @FXML private TextField nameField;
    @FXML private TextField latField;
    @FXML private TextField lonField;
    @FXML private Button addButton;
    @FXML private Button removeButton;
    @FXML private Button refreshButton;

    @FXML private LineChart<String, Number> temperatureChart;
    @FXML private CategoryAxis timeAxis;
    @FXML private NumberAxis valueAxis;

    private final ObservableList<LocationRow> locations = FXCollections.observableArrayList();
    private final Random random = new Random();
    private final DatabaseService databaseService = DatabaseService.getInstance();
    private final WeatherService weatherService = new WeatherService();
    private final FreeWeatherService freeWeatherService = new FreeWeatherService();
    private User currentUser;

    @FXML
    private void initialize() {
        // Table setup
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colLat.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getLatitude()));
        colLon.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getLongitude()));
        locationsTable.setItems(locations);

        // Units
        unitsChoice.setItems(FXCollections.observableArrayList("Metric (°C)", "Imperial (°F)"));
        unitsChoice.getSelectionModel().selectFirst();
        unitsChoice.setOnAction(e -> refreshChart());

        // Buttons
        addButton.setOnAction(e -> addLocation());
        removeButton.setOnAction(e -> removeSelected());
        refreshButton.setOnAction(e -> refreshChart());

        // Enter key adds location
        lonField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) addLocation();
        });

        // Select row updates chart
        locationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> refreshChart());

        configureChart();
    }

    public void setUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getDisplayName() + "!");
        loadUserData();
    }
    
    private void loadUserData() {
        if (currentUser == null) return;
        
        try {
            // Load user's saved locations
            List<SavedLocation> savedLocations = databaseService.getUserLocations(currentUser.getId());
            locations.clear();
            for (SavedLocation location : savedLocations) {
                locations.add(new LocationRow(
                    location.getLocationName(),
                    location.getLatitude(),
                    location.getLongitude()
                ));
            }
            
            // Load user preferences
            String unitsPreference = databaseService.getUserPreference(currentUser.getId(), "units");
            if (unitsPreference != null) {
                if ("imperial".equals(unitsPreference)) {
                    unitsChoice.getSelectionModel().selectLast();
                } else {
                    unitsChoice.getSelectionModel().selectFirst();
                }
            }
            
            // Select first location if available
            if (!locations.isEmpty()) {
                locationsTable.getSelectionModel().selectFirst();
            }
            
        } catch (Exception e) {
            showError("Failed to load user data: " + e.getMessage());
        }
    }

    private void configureChart() {
        temperatureChart.setAnimated(false);
        temperatureChart.setLegendVisible(true);
        timeAxis.setLabel("Time");
        updateValueAxisLabel();
    }

    private void updateValueAxisLabel() {
        boolean metric = unitsChoice.getSelectionModel().getSelectedIndex() == 0;
        valueAxis.setLabel(metric ? "Temperature (°C)" : "Temperature (°F)");
    }

    private void addLocation() {
        String name = safe(nameField.getText());
        String latText = safe(latField.getText());
        String lonText = safe(lonField.getText());

        if (name.isEmpty() || latText.isEmpty() || lonText.isEmpty()) {
            showInfo("Please enter name, latitude, and longitude.");
            return;
        }
        try {
            double lat = Double.parseDouble(latText);
            double lon = Double.parseDouble(lonText);
            
            // Save to database
            if (currentUser != null) {
                databaseService.saveLocation(currentUser.getId(), name, lat, lon);
            }
            
            // Add to UI
            LocationRow newLocation = new LocationRow(name, lat, lon);
            locations.add(newLocation);
            nameField.clear();
            latField.clear();
            lonField.clear();
            locationsTable.getSelectionModel().selectLast();
            
        } catch (NumberFormatException ex) {
            showInfo("Latitude and Longitude must be valid numbers.");
        } catch (Exception e) {
            showError("Failed to save location: " + e.getMessage());
        }
    }

    private void removeSelected() {
        LocationRow sel = locationsTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            try {
                // Find and remove from database
                if (currentUser != null) {
                    List<SavedLocation> savedLocations = databaseService.getUserLocations(currentUser.getId());
                    for (SavedLocation location : savedLocations) {
                        if (location.getLocationName().equals(sel.getName()) &&
                            location.getLatitude().equals(sel.getLatitude()) &&
                            location.getLongitude().equals(sel.getLongitude())) {
                            databaseService.deleteLocation(location.getId());
                            break;
                        }
                    }
                }
                
                // Remove from UI
                locations.remove(sel);
            } catch (Exception e) {
                showError("Failed to remove location: " + e.getMessage());
            }
        }
    }

    private void refreshChart() {
        updateValueAxisLabel();
        LocationRow sel = locationsTable.getSelectionModel().getSelectedItem();
        temperatureChart.getData().clear();
        if (sel == null) return;

        try {
            // Get real weather data
            boolean metric = unitsChoice.getSelectionModel().getSelectedIndex() == 0;
            String units = metric ? "metric" : "imperial";
            
            List<WeatherService.WeatherData> forecast = null;
            String dataSource = "OpenWeatherMap";
            
            try {
                // Try the main weather service first
                forecast = weatherService.getHourlyForecast(sel.getLatitude(), sel.getLongitude(), units);
            } catch (IOException e) {
                if (e.getMessage().contains("401")) {
                    // API key issue, try free service
                    try {
                        List<FreeWeatherService.WeatherData> freeForecast = freeWeatherService.getHourlyForecast(
                            sel.getLatitude(), sel.getLongitude(), units);
                        
                        // Convert to main WeatherData format
                        forecast = new ArrayList<>();
                        for (FreeWeatherService.WeatherData freeData : freeForecast) {
                            forecast.add(new WeatherService.WeatherData(
                                freeData.getTemperature(),
                                freeData.getFeelsLike(),
                                freeData.getHumidity(),
                                freeData.getWindSpeed(),
                                freeData.getDescription(),
                                freeData.getIcon(),
                                freeData.getDateTime(),
                                freeData.getUnits()
                            ));
                        }
                        dataSource = "Free Weather Service";
                    } catch (IOException freeE) {
                        throw e; // Re-throw original error
                    }
                } else {
                    throw e; // Re-throw if not API key issue
                }
            }
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Temperature - " + sel.getName() + " (" + dataSource + ")");
            
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            
            for (WeatherService.WeatherData weather : forecast) {
                String timeLabel = weather.getDateTime().format(fmt);
                series.getData().add(new XYChart.Data<>(timeLabel, weather.getTemperature()));
            }
            
            temperatureChart.getData().add(series);
            
            // Save user preference
            if (currentUser != null) {
                databaseService.saveUserPreference(currentUser.getId(), "units", units);
            }
            
        } catch (IOException e) {
            // Fallback to demo data if API fails
            showDemoData(sel);
            if (e.getMessage().contains("401")) {
                showError("Weather API Key Required!\n\n" +
                         "The app tried to use OpenWeatherMap but needs a valid API key.\n\n" +
                         "✅ FREE OPTIONS (No Payment Required):\n" +
                         "1. Get free OpenWeatherMap key: https://openweathermap.org/api\n" +
                         "2. Or use the built-in free weather service (no setup needed)\n" +
                         "3. Or continue with demo data (already working!)\n\n" +
                         "See FREE_API_SETUP.md for detailed instructions.\n\n" +
                         "Showing demo data for now.");
            } else {
                showError("Weather API unavailable. Showing demo data.\nError: " + e.getMessage());
            }
        } catch (Exception e) {
            showDemoData(sel);
            showError("Failed to load weather data: " + e.getMessage());
        }
    }
    
    private void showDemoData(LocationRow sel) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Temperature - " + sel.getName() + " (Demo)");

        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        boolean metric = unitsChoice.getSelectionModel().getSelectedIndex() == 0;
        for (int i = -6; i <= 18; i++) { // show 25 hours around now
            LocalDateTime t = now.plusHours(i);
            double base = 12 + 8 * Math.sin((i / 24.0) * 2 * Math.PI); // simple day curve
            double noise = random.nextGaussian();
            double tempC = base + noise;
            double value = metric ? tempC : (tempC * 9 / 5) + 32;
            series.getData().add(new XYChart.Data<>(fmt.format(t), value));
        }

        temperatureChart.getData().add(series);
    }


    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    
    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText("Error");
        alert.showAndWait();
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    // Simple row model for the TableView
    public static class LocationRow {
        private final String name;
        private final double latitude;
        private final double longitude;

        public LocationRow(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
        public String getName() { return name; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
    }
}
