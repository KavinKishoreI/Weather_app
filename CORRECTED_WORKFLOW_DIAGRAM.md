# Simplified JavaFX Weather App Workflow Diagram

## Main Issues Found in Original Diagram:

1. **Missing Configuration Layer** - ConfigManager not shown
2. **No Fallback Mechanism** - Weather service fallback chain missing
3. **Missing Database Components** - Database initialization not included
4. **Incomplete Data Flow** - No error handling shown

---

## CORRECTED WORKFLOW DIAGRAM

```
┌─────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                      │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐        ┌─────────────────────────────────┐ │
│  │   LoginView     │        │      DashboardView             │ │
│  │                 │        │                                 │ │
│  │ • Email Field   │        │ • Location Table (TableView)   │ │
│  │ • Password Field│        │ • Add Location Form            │ │
│  │ • Login Button  │        │ • Weather Chart (LineChart)    │ │
│  │ • Register Btn  │        │ • Units Toggle (ChoiceBox)     │ │
│  │ • Status Label  │        │ • Remove Selected Button       │ │
│  │                 │        │ • Refresh Chart Button         │ │
│  └─────────────────┘        │ • Welcome Label                │ │
│                             └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                       CONTROLLER LAYER                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐        ┌─────────────────────────────────┐ │
│  │ LoginController │        │    DashboardController         │ │
│  │                 │        │                                 │ │
│  │ • onLogin()     │        │ • addLocation()                │ │
│  │ • onRegister()  │        │ • removeSelected()             │ │
│  │ • validate()    │        │ • refreshChart()               │ │
│  │ • initialize()  │        │ • loadUserData()               │ │
│  │                 │        │ • updateChartData()            │ │
│  │                 │        │ • showDemoData()               │ │
│  │                 │        │ • initialize()                 │ │
│  └─────────────────┘        └─────────────────────────────────┘ │
│                                                               │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                SceneRouter                              │   │
│  │ • showLogin()                                          │   │
│  │ • showDashboard()                                      │   │
│  │ • applyStyles()                                        │   │
│  │ • init() - Singleton pattern                           │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                    CONFIGURATION LAYER                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                ConfigManager (Singleton)                   │ │
│  │                                                             │ │
│  │ • getApiKey() - Environment + Properties                   │ │
│  │ • getDbUsername() - Environment + Properties               │ │
│  │ • getDbPassword() - Environment + Properties               │ │
│  │ • getWeatherApiBaseUrl()                                   │ │
│  │ • getWeatherApiGeocodingUrl()                              │ │
│  │ • printConfigStatus()                                      │ │
│  │ • loadProperties() - from application.properties          │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                       SERVICE LAYER                            │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                DatabaseService (Singleton)                 │ │
│  │                                                             │ │
│  │ • authenticateUser()                                       │ │
│  │ • registerUser()                                           │ │
│  │ • saveUserPreference()                                     │ │
│  │ • getUserPreference()                                      │ │
│  │ • saveLocation()                                           │ │
│  │ • getUserLocations()                                       │ │
│  │ • deleteLocation()                                         │ │
│  │ • close() - Session management                             │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                               │                                 │
│  ┌─────────────────────────────▼─────────────────────────────┐   │
│  │            WeatherService (Primary)                      │   │
│  │                                                           │   │
│  │ • getCurrentWeather()                                    │   │
│  │ • getHourlyForecast()                                    │   │
│  │ • getLocationByName()                                    │   │
│  │ • parseCurrentWeather()                                  │   │
│  │ • parseForecast()                                        │   │
│  │ • validateApiKey() - Warning system                      │   │
│  └─────────────────────────────────────────────────────────┘   │
│                               │                                 │
│  ┌─────────────────────────────▼─────────────────────────────┐   │
│  │         FreeWeatherService (Fallback)                    │   │
│  │                                                           │   │
│  │ • getCurrentWeather() - wttr.in API                      │   │
│  │ • getHourlyForecast()                                    │   │
│  │ • parseWttrWeather()                                     │   │
│  │ • parseWttrForecast()                                    │   │
│  │ • createDemoWeatherData() - Final fallback               │   │
│  │ • createDemoForecast()                                   │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                     DATA STORAGE LAYER                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │              Oracle Database (Hibernate/JPA)              │ │
│  │                                                             │ │
│  │ ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │ │
│  │ │    Users    │  │SavedLocations│  │  UserPreferences   │ │ │
│  │ │             │  │             │  │                     │ │ │
│  │ │ • id (PK)   │◄─┤ • id (PK)   │  │ • id (PK)          │ │ │
│  │ │ • email     │  │ • location  │  │ • key/value        │ │ │
│  │ │ • password  │  │ • lat/lon   │  │ • user_id (FK)─────┼─┘ │
│  │ │ • display   │  │ • created_at│  │ • created_at       │ │ │
│  │ │ • created_at│  │ • user_id   │  └─────────────────────┘   │ │
│  │ │ • last_login│  └─────────────┘                           │ │
│  │ │ • preferences│                                          │ │
│  │ │ • locations │                                           │ │
│  │ └─────────────┘                                           │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘

## Key Corrections Made:

✅ **Added Configuration Layer** - ConfigManager for API keys and credentials  
✅ **Fixed Weather Service Architecture** - Primary → Fallback → Demo Data chain  
✅ **Added Database Components** - Database initialization and connection testing  
✅ **Complete Error Handling** - Multiple fallback mechanisms ensure app always works  
✅ **Proper Entity Relationships** - JPA relationships between User, Location, and Preferences  
✅ **Simplified Layer Structure** - Clear separation of concerns with less complexity  

This corrected diagram shows the actual architecture with proper fallback mechanisms and all essential components.
