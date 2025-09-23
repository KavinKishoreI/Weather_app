# JavaFX Weather App - Workflow Diagram

## 🎯 Simple Workflow Overview

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          UI Components (FXML Files)                            │
│                                                                                 │
│  ┌─────────────────┐                    ┌─────────────────┐                    │
│  │   LoginView     │                    │  DashboardView  │                    │
│  │                 │                    │                 │                    │
│  │ • Email Field   │                    │ • Location Table│                    │
│  │ • Password Field│                    │ • Add Location  │                    │
│  │ • Login Button  │                    │ • Weather Chart │                    │
│  │ • Register Btn  │                    │ • Units Toggle  │                    │
│  └─────────────────┘                    └─────────────────┘                    │
└─────────────────────┬───────────────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                         Event Handling Scripts (Controllers)                   │
│                                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐            │
│  │LoginController  │    │SceneRouter      │    │DashboardController│          │
│  │                 │    │                 │    │                 │            │
│  │ • onLogin()     │    │ • showLogin()   │    │ • addLocation() │            │
│  │ • onRegister()  │    │ • showDashboard()│    │ • refreshChart()│            │
│  │ • validate()    │    │ • applyStyles() │    │ • loadUserData()│            │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘            │
└─────────────────────┬───────────────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                            Backend Services                                    │
│                                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐            │
│  │DatabaseService  │    │WeatherService   │    │FreeWeatherService│           │
│  │                 │    │                 │    │                 │            │
│  │ • User Auth     │    │ • OpenWeatherMap│    │ • wttr.in API   │            │
│  │ • Save Data     │    │ • Weather Data  │    │ • Fallback Data │            │
│  │ • Load Data     │    │ • Forecast      │    │ • Demo Data     │            │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘            │
└─────────────────────┬───────────────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              Data Storage                                      │
│                                                                                 │
│  ┌─────────────────┐                    ┌─────────────────┐                    │
│  │ Oracle Database │                    │Weather APIs     │                    │
│  │                 │                    │                 │                    │
│  │ • Users         │                    │ • OpenWeatherMap│                    │
│  │ • Locations     │                    │ • Free APIs     │                    │
│  │ • Preferences   │                    │ • Demo Data     │                    │
│  └─────────────────┘                    └─────────────────┘                    │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## 🔄 Application Flow

### **Simple Flow:**
**UI Components** → **Controllers** → **Services** → **Data Storage**

1. **FXML Files** define the user interface
2. **Controllers** handle user interactions and events  
3. **Services** contain business logic and API calls
4. **Data Storage** (Database & APIs) stores and provides data

## 📋 Component Details

### **UI Components (FXML Files)**
- **LoginView.fxml**: User authentication interface
  - Email input field
  - Password input field
  - Login and Register buttons
  - Status messages

- **DashboardView.fxml**: Main application interface
  - Saved locations table
  - Add location form (name, latitude, longitude)
  - Weather temperature chart
  - Units toggle (Metric/Imperial)

### **Event Handling Scripts (Controllers)**
- **LoginController**: Handles authentication logic
  - `onLogin()`: Validates credentials and authenticates user
  - `onRegister()`: Creates new user account
  - `validate()`: Input validation

- **SceneRouter**: Manages navigation between screens
  - `showLogin()`: Displays login screen
  - `showDashboard()`: Displays main dashboard
  - `applyStyles()`: Applies CSS styling

- **DashboardController**: Manages main application functionality
  - `addLocation()`: Saves new location to database
  - `refreshChart()`: Fetches and displays weather data
  - `loadUserData()`: Loads user's saved data

### **Backend Services**
- **DatabaseService**: Database operations
  - User authentication and registration
  - CRUD operations for locations and preferences
  - Hibernate ORM integration

- **WeatherService**: Primary weather data provider
  - OpenWeatherMap API integration
  - Current weather and forecast data
  - Location geocoding

- **FreeWeatherService**: Fallback weather provider
  - wttr.in API (no API key required)
  - Demo data generation
  - Automatic fallback when primary service fails

### **Data Storage**
- **Oracle Database**: Persistent data storage
  - Users table (authentication data)
  - Saved locations table (user's favorite locations)
  - User preferences table (settings)

- **External Weather APIs**: Real-time weather data
  - OpenWeatherMap (primary)
  - wttr.in (fallback)
  - Demo data (final fallback)

## 🚀 User Journey

```
Startup → Login → Dashboard → Weather Data
   ↓         ↓         ↓           ↓
Launch   Authenticate  Add Loc   View Chart
App      User         Manage     Get Forecast
         Account      Data       Visualize
```

## 🔧 Technical Architecture

- **Frontend**: JavaFX with FXML
- **Backend**: Java with Hibernate ORM
- **Database**: Oracle Database
- **APIs**: OpenWeatherMap, wttr.in
- **Build Tool**: Maven
- **Package Structure**: `org.example`

## 📁 Project Structure

```
src/main/java/org/example/
├── Main.java                    # Application entry point
├── User.java                    # User entity
├── UserPreference.java          # User preferences entity
├── SavedLocation.java           # Saved locations entity
├── DatabaseService.java         # Database operations
├── WeatherService.java          # Weather API integration
├── FreeWeatherService.java      # Fallback weather service
└── ui/
    ├── LoginController.java     # Login screen controller
    ├── DashboardController.java # Main dashboard controller
    └── SceneRouter.java         # Scene navigation
```

This workflow diagram shows the complete architecture of your JavaFX Weather App, from user interface to data storage, following the MVC (Model-View-Controller) pattern.
