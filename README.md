# Weather App - JavaFX with Database Integration

A comprehensive weather application built with JavaFX that integrates with a database for user management and weather data from public APIs.

## Features

- **User Authentication**: Register and login with email/password
- **Database Integration**: Oracle database with Hibernate ORM
- **Weather Data**: Real-time weather data from OpenWeatherMap API
- **Location Management**: Save and manage favorite locations
- **User Preferences**: Store user settings (units, etc.)
- **Interactive Charts**: Visualize temperature data with JavaFX charts
- **Responsive UI**: Modern JavaFX interface with CSS styling

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Oracle Database 11g or higher (or compatible database)
- OpenWeatherMap API key

## Setup Instructions

### 1. Database Setup

1. Install and configure Oracle Database
2. Create a database user with appropriate permissions
3. Update the database connection details in `src/main/resources/hibernate.cfg.xml`:
   ```xml
   <property name="hibernate.connection.url">jdbc:oracle:thin:@localhost:1521:xe</property>
   <property name="hibernate.connection.username">your_username</property>
   <property name="hibernate.connection.password">your_password</property>
   ```

### 2. Weather API Setup

1. **Sign up for a free account** at [OpenWeatherMap](https://openweathermap.org/api)
2. **Get your API key** from the [API Keys dashboard](https://home.openweathermap.org/api_keys)
3. **Set environment variable** (recommended - more secure):
   ```bash
   # Windows (PowerShell)
   $env:OPENWEATHER_API_KEY = "your_actual_api_key_here"
   
   # Linux/macOS
   export OPENWEATHER_API_KEY="your_actual_api_key_here"
   ```
4. **Alternative**: Update `weather.api.key` in `src/main/resources/application.properties` (less secure)
5. **See `ENVIRONMENT_SETUP.md`** for detailed step-by-step instructions

### 3. Build and Run

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd Javafx
   ```

2. Build the project:
   ```bash
   mvn clean compile
   ```

3. Run the application:
   ```bash
   mvn javafx:run
   ```

## Project Structure

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── Main.java                 # Application entry point
│   │   ├── User.java                 # User entity
│   │   ├── UserPreference.java       # User preferences entity
│   │   ├── SavedLocation.java        # Saved locations entity
│   │   ├── Hibernate.java           # Database test class
│   │   ├── DatabaseService.java     # Database operations
│   │   ├── WeatherService.java      # Weather API integration
│   │   └── ui/
│   │       ├── SceneRouter.java     # Scene navigation
│   │       ├── LoginController.java # Login screen controller
│   │       └── DashboardController.java # Main dashboard controller
│   └── resources/
│       ├── fxml/                    # FXML UI files
│       ├── css/                     # CSS styling
│       └── hibernate.cfg.xml       # Hibernate configuration
```

## Database Schema

The application creates the following tables automatically:

- **users**: User accounts with authentication
- **user_preferences**: User settings and preferences
- **saved_locations**: User's saved weather locations

## Usage

1. **Registration/Login**: Create a new account or login with existing credentials
2. **Add Locations**: Enter location name, latitude, and longitude to save locations
3. **View Weather**: Select a location to view temperature charts
4. **Settings**: Toggle between metric/imperial units
5. **Data Persistence**: All data is automatically saved to the database

## API Integration

The app integrates with OpenWeatherMap API to fetch:
- Current weather conditions
- Hourly forecasts
- Location geocoding

## Error Handling

- Database connection errors
- API unavailability (falls back to demo data)
- Invalid user input
- Network connectivity issues

## Development Notes

- Uses Hibernate 6.5.2 with Jakarta Persistence API
- JavaFX 23 for modern UI components
- OkHttp for HTTP requests
- Jackson for JSON processing
- Oracle JDBC driver for database connectivity

## Troubleshooting

### Database Connection Issues
- Verify Oracle database is running
- Check connection parameters in `hibernate.cfg.xml`
- Ensure database user has CREATE/INSERT/UPDATE/DELETE permissions

### Weather API Issues
- Verify API key is correct and active
- Check internet connectivity
- Ensure API quota hasn't been exceeded

### Build Issues
- Ensure Java 21+ is installed
- Verify Maven is properly configured
- Check all dependencies are resolved

## License

This project is for educational purposes. Please ensure you comply with OpenWeatherMap's terms of service when using their API.
