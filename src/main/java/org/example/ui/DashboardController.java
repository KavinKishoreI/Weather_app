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
import javafx.application.Platform;
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
    private DatabaseService databaseService;
    private WeatherService weatherService;
    private FreeWeatherService freeWeatherService;
    private User currentUser;

    @FXML
    private void initialize() {
        // Initialize services safely
        initializeServices();
        
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
        refreshButton.setOnAction(e -> {
            System.out.println("=== REFRESH BUTTON CLICKED ===");
            refreshChart();
        });

        // Enter key adds location
        lonField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) addLocation();
        });

        // Select row updates chart
        locationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            System.out.println("=== TABLE SELECTION CHANGED ===");
            System.out.println("From: " + (oldSel != null ? oldSel.getName() : "null"));
            System.out.println("To: " + (newSel != null ? newSel.getName() : "null"));
            refreshChart();
        });

        configureChart();
        
        // Test chart with simple data
        testChart();
    }
    
    private void initializeServices() {
        // Initialize DatabaseService
        try {
            if (DatabaseService.isDatabaseConfigured()) {
                databaseService = DatabaseService.getInstance();
                System.out.println("✅ Database service initialized successfully");
            } else {
                System.out.println("⚠️ Database not configured: " + DatabaseService.getDatabaseConfigurationError());
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to initialize database service: " + e.getMessage());
        }
        
        // Initialize WeatherService
        try {
            weatherService = new WeatherService();
            System.out.println("✅ Weather service initialized successfully");
        } catch (Exception e) {
            System.out.println("❌ Failed to initialize weather service: " + e.getMessage());
            System.out.println("⚠️ Weather features will be limited");
        }
        
        // Initialize FreeWeatherService (fallback)
        try {
            freeWeatherService = new FreeWeatherService();
            System.out.println("✅ Free weather service initialized successfully");
        } catch (Exception e) {
            System.out.println("❌ Failed to initialize free weather service: " + e.getMessage());
        }
    }
    
    private void testChart() {
        System.out.println("=== TESTING CHART ===");
        XYChart.Series<String, Number> testSeries = new XYChart.Series<>();
        testSeries.setName("Test Data");
        testSeries.getData().add(new XYChart.Data<>("00:00", 20));
        testSeries.getData().add(new XYChart.Data<>("06:00", 15));
        testSeries.getData().add(new XYChart.Data<>("12:00", 25));
        testSeries.getData().add(new XYChart.Data<>("18:00", 22));
        
        // IMPROVED: Add test series with immediate visual refresh
        temperatureChart.getData().add(testSeries);
        System.out.println("Test data added to chart");
        
        // Force immediate layout for test data
        temperatureChart.requestLayout();
        temperatureChart.applyCss();
        temperatureChart.layout();
        
        // Apply aggressive refresh for test data
        Platform.runLater(() -> {
            forceChartRefresh();
        });
    }

    public void setUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getDisplayName() + "!");
        loadUserData();
    }
    
    private void loadUserData() {
        if (currentUser == null) return;
        
        if (databaseService == null) {
            System.out.println("⚠️ Database service not available - user data not loaded");
            return;
        }
        
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
                // Force chart refresh after loading data
                refreshChart();
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
            if (currentUser != null && databaseService != null) {
                databaseService.saveLocation(currentUser.getId(), name, lat, lon);
            } else if (currentUser != null && databaseService == null) {
                System.out.println("⚠️ Database service not available - location not saved to database");
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
                if (currentUser != null && databaseService != null) {
                    List<SavedLocation> savedLocations = databaseService.getUserLocations(currentUser.getId());
                    for (SavedLocation location : savedLocations) {
                        if (location.getLocationName().equals(sel.getName()) &&
                            location.getLatitude().equals(sel.getLatitude()) &&
                            location.getLongitude().equals(sel.getLongitude())) {
                            databaseService.deleteLocation(location.getId());
                            break;
                        }
                    }
                } else if (currentUser != null && databaseService == null) {
                    System.out.println("⚠️ Database service not available - location not removed from database");
                }
                
                // Remove from UI
                locations.remove(sel);
            } catch (Exception e) {
                showError("Failed to remove location: " + e.getMessage());
            }
        }
    }

    private void refreshChart() {
        System.out.println("=== REFRESH CHART CALLED ===");
        updateValueAxisLabel();
        LocationRow sel = locationsTable.getSelectionModel().getSelectedItem();
        
        // Clear chart data first
        temperatureChart.getData().clear();
        
        if (sel == null) {
            System.out.println("No location selected, chart cleared");
            return;
        }
        
        System.out.println("Refreshing chart for location: " + sel.getName() + " (" + sel.getLatitude() + ", " + sel.getLongitude() + ")");
        
        // Force chart to update by running on JavaFX thread
        Platform.runLater(() -> {
            System.out.println("=== PLATFORM.RUNLATER EXECUTING ===");
            updateChartData(sel);
        });
    }
    
    private void forceChartRefresh() {
        System.out.println("=== FORCING AGGRESSIVE CHART REFRESH ===");
        
        // Method 1: Visibility manipulation to force complete redraw
        temperatureChart.setVisible(false);
        temperatureChart.setManaged(false);
        
        Platform.runLater(() -> {
            temperatureChart.setVisible(true);
            temperatureChart.setManaged(true);
            
            // Method 2: Multiple layout cycles
            temperatureChart.requestLayout();
            temperatureChart.applyCss();
            temperatureChart.layout();
            
            // Method 3: Axis manipulation to force refresh
            timeAxis.setAutoRanging(false);
            timeAxis.setAutoRanging(true);
            valueAxis.setAutoRanging(false);
            valueAxis.setAutoRanging(true);
            
            // Method 4: Animation toggle to trigger refresh
            temperatureChart.setAnimated(true);
            temperatureChart.setAnimated(false);
            temperatureChart.setAnimated(true);
            
            Platform.runLater(() -> {
                // Final forced refresh
                temperatureChart.requestLayout();
                temperatureChart.layout();
                System.out.println("=== AGGRESSIVE CHART REFRESH COMPLETED ===");
            });
        });
    }
    
    private void updateChartData(LocationRow sel) {
        System.out.println("=== UPDATE CHART DATA CALLED ===");
        // Ensure chart is properly cleared and ready for new data
        temperatureChart.getData().clear();
        
        // Force a complete chart refresh
        temperatureChart.setAnimated(false);
        temperatureChart.layout();
        
        try {
            // Get real weather data
            boolean metric = unitsChoice.getSelectionModel().getSelectedIndex() == 0;
            String units = metric ? "metric" : "imperial";
            
            List<WeatherService.WeatherData> forecast = null;
            String dataSource = "OpenWeatherMap";
            
            if (weatherService != null) {
                try {
                    // Try the main weather service first
                    forecast = weatherService.getHourlyForecast(sel.getLatitude(), sel.getLongitude(), units);
                } catch (IOException e) {
                    if (e.getMessage().contains("401")) {
                        // API key issue, try free service
                        if (freeWeatherService != null) {
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
                            throw new IOException("No weather services available");
                        }
                    } else {
                        throw e; // Re-throw if not API key issue
                    }
                }
            } else if (freeWeatherService != null) {
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
                } catch (IOException e) {
                    throw new IOException("Free weather service failed: " + e.getMessage());
                }
            } else {
                throw new IOException("No weather services available");
            }
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Temperature - " + sel.getName() + " (" + dataSource + ")");
            
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            
            for (WeatherService.WeatherData weather : forecast) {
                String timeLabel = weather.getDateTime().format(fmt);
                series.getData().add(new XYChart.Data<>(timeLabel, weather.getTemperature()));
            }
            
            // IMPROVED: Add series with immediate visual refresh
            temperatureChart.getData().add(series);
            System.out.println("Added " + series.getData().size() + " data points to chart");
            System.out.println("Chart data size after adding: " + temperatureChart.getData().size());
            
            // Immediate layout refresh
            temperatureChart.setAnimated(false);
            temperatureChart.requestLayout();
            temperatureChart.applyCss();
            temperatureChart.layout();
            
            // Force a complete scene refresh
            Platform.runLater(() -> {
                temperatureChart.requestLayout();
                temperatureChart.layout();
                forceChartRefresh();
                System.out.println("=== CHART LAYOUT COMPLETED ===");
            });
            
            // Save user preference
            if (currentUser != null && databaseService != null) {
                databaseService.saveUserPreference(currentUser.getId(), "units", units);
            } else if (currentUser != null && databaseService == null) {
                System.out.println("⚠️ Database service not available - units preference not saved");
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

        // IMPROVED: Add demo series with immediate visual refresh
        temperatureChart.getData().add(series);
        System.out.println("Added " + series.getData().size() + " demo data points to chart");
        
        // Immediate layout refresh for demo data
        temperatureChart.setAnimated(false);
        temperatureChart.requestLayout();
        temperatureChart.applyCss();
        temperatureChart.layout();
        
        // Force a complete scene refresh
        Platform.runLater(() -> {
            temperatureChart.requestLayout();
            temperatureChart.layout();
            forceChartRefresh();
            System.out.println("=== DEMO CHART LAYOUT COMPLETED ===");
        });
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
