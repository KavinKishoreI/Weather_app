# Service Layer Workflow Diagrams

## 🔧 **DatabaseService Workflow**

### **Setup Process:**
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          DatabaseService Constructor                           │
│                                                                                 │
│  1. Configuration.load()                                                       │
│     ↓                                                                           │
│  2. Read hibernate.cfg.xml                                                     │
│     ↓                                                                           │
│  3. config.addAnnotatedClass(User.class)                                       │
│  4. config.addAnnotatedClass(UserPreference.class)                             │
│  5. config.addAnnotatedClass(SavedLocation.class)                              │
│     ↓                                                                           │
│  6. config.buildSessionFactory()                                               │
│     ↓                                                                           │
│  7. Create database connection pool                                            │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### **authenticateUser() Workflow:**
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           authenticateUser()                                   │
│                                                                                 │
│  Input: email="user@example.com", password="password123"                       │
│                                                                                 │
│  1. sessionFactory.openSession()                                               │
│     ↓                                                                           │
│  2. Create HQL Query: "FROM User WHERE email = :email AND password = :password"│
│     ↓                                                                           │
│  3. query.setParameter("email", email)                                         │
│     ↓                                                                           │
│  4. query.setParameter("password", password)                                   │
│     ↓                                                                           │
│  5. query.uniqueResult() → Execute query                                       │
│     ↓                                                                           │
│  6. User found?                                                                │
│     ├─ YES: Update lastLogin → Begin Transaction → Merge User → Commit         │
│     └─ NO: Return null                                                         │
│     ↓                                                                           │
│  7. Return User object                                                         │
│     ↓                                                                           │
│  8. session.close() (automatic)                                                │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### **saveLocation() Workflow:**
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              saveLocation()                                    │
│                                                                                 │
│  Input: userId=123, name="London", lat=51.5, lon=-0.1                         │
│                                                                                 │
│  1. sessionFactory.openSession()                                               │
│     ↓                                                                           │
│  2. session.get(User.class, userId) → Find user by ID                          │
│     ↓                                                                           │
│  3. new SavedLocation(name, lat, lon, user) → Create location object           │
│     ↓                                                                           │
│  4. session.beginTransaction()                                                 │
│     ↓                                                                           │
│  5. session.persist(location) → Save to database                               │
│     ↓                                                                           │
│  6. session.getTransaction().commit() → Save changes                           │
│     ↓                                                                           │
│  7. session.close() (automatic)                                                │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### **getUserLocations() Workflow:**
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                            getUserLocations()                                  │
│                                                                                 │
│  Input: userId=123                                                             │
│                                                                                 │
│  1. sessionFactory.openSession()                                               │
│     ↓                                                                           │
│  2. Create HQL Query: "FROM SavedLocation WHERE user.id = :userId ORDER BY..." │
│     ↓                                                                           │
│  3. query.setParameter("userId", userId)                                       │
│     ↓                                                                           │
│  4. query.list() → Execute query, get List<SavedLocation>                      │
│     ↓                                                                           │
│  5. Return list of locations                                                   │
│     ↓                                                                           │
│  6. session.close() (automatic)                                                │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🌤️ **WeatherService Workflow**

### **Setup Process:**
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          WeatherService Constructor                            │
│                                                                                 │
│  1. new OkHttpClient() → Create HTTP client                                    │
│     ↓                                                                           │
│  2. new ObjectMapper() → Create JSON parser                                    │
│     ↓                                                                           │
│  3. Load application.properties → Get API configuration                        │
│     ↓                                                                           │
│  4. getApiKeyFromEnvironment() → Get API key from environment variable         │
│     ↓                                                                           │
│  5. validateApiKey() → Check if API key is valid                               │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### **getCurrentWeather() Workflow:**
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           getCurrentWeather()                                  │
│                                                                                 │
│  Input: latitude=51.5, longitude=-0.1, units="metric"                         │
│                                                                                 │
│  1. Build API URL:                                                             │
│     "https://api.openweathermap.org/data/2.5/weather?lat=51.5&lon=-0.1&appid=KEY&units=metric"
│     ↓                                                                           │
│  2. new Request.Builder().url(url).build() → Create HTTP request               │
│     ↓                                                                           │
│  3. client.newCall(request).execute() → Send HTTP request to OpenWeatherMap    │
│     ↓                                                                           │
│  4. response.body().string() → Get JSON response                               │
│     Example: {"main":{"temp":15.2},"weather":[{"description":"cloudy"}]}      │
│     ↓                                                                           │
│  5. objectMapper.readTree(responseBody) → Parse JSON to JsonNode               │
│     ↓                                                                           │
│  6. parseCurrentWeather(jsonNode, units) → Extract weather data                │
│     ├─ Get temperature: jsonNode.get("main").get("temp")                       │
│     ├─ Get description: jsonNode.get("weather").get(0).get("description")      │
│     └─ Create WeatherData object                                               │
│     ↓                                                                           │
│  7. Return WeatherData object                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### **getHourlyForecast() Workflow:**
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           getHourlyForecast()                                  │
│                                                                                 │
│  Input: latitude=51.5, longitude=-0.1, units="metric"                         │
│                                                                                 │
│  1. Build API URL for forecast endpoint                                        │
│     "https://api.openweathermap.org/data/2.5/forecast?lat=51.5&lon=-0.1&appid=KEY&units=metric"
│     ↓                                                                           │
│  2. Send HTTP request → Get forecast JSON                                      │
│     ↓                                                                           │
│  3. Parse JSON response → Get list of forecast items                           │
│     ↓                                                                           │
│  4. For each forecast item:                                                    │
│     ├─ Extract temperature, humidity, wind speed                               │
│     ├─ Parse datetime                                                          │
│     └─ Create WeatherData object                                               │
│     ↓                                                                           │
│  5. Return List<WeatherData> → Return 24-hour forecast                         │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 **FreeWeatherService Workflow**

### **Setup Process:**
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        FreeWeatherService Constructor                          │
│                                                                                 │
│  1. new OkHttpClient() → Create HTTP client                                    │
│     ↓                                                                           │
│  2. new ObjectMapper() → Create JSON parser                                    │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### **getCurrentWeather() Workflow:**
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        FreeWeatherService.getCurrentWeather()                  │
│                                                                                 │
│  Input: latitude=51.5, longitude=-0.1, units="metric"                         │
│                                                                                 │
│  1. Build wttr.in API URL: "https://wttr.in/?format=j1&lang=en"               │
│     ↓                                                                           │
│  2. new Request.Builder()                                                      │
│     ├─ .url(url)                                                               │
│     ├─ .addHeader("User-Agent", "WeatherApp/1.0") → Add required header       │
│     └─ .build() → Create HTTP request                                          │
│     ↓                                                                           │
│  3. client.newCall(request).execute() → Send HTTP request to wttr.in           │
│     ↓                                                                           │
│  4. response.body().string() → Get JSON response                               │
│     Example: {"current_condition":[{"temp_C":"15","FeelsLikeC":"14"}]}        │
│     ↓                                                                           │
│  5. objectMapper.readTree(responseBody) → Parse JSON                           │
│     ↓                                                                           │
│  6. parseWttrWeather(jsonNode, units) → Extract weather data                   │
│     ├─ Get temperature: jsonNode.get("current_condition").get(0).get("temp_C") │
│     ├─ Get feels like: jsonNode.get("current_condition").get(0).get("FeelsLikeC")│
│     ├─ Convert to imperial if needed                                           │
│     └─ Create WeatherData object                                               │
│     ↓                                                                           │
│  7. Return WeatherData object                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 **Complete Service Interaction Flow**

### **User Adds Location Example:**
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                         Complete Service Flow                                  │
│                                                                                 │
│  User Action: Types "Paris" (48.8566, 2.3522) and clicks "Add"                │
│                                                                                 │
│  1. DashboardController.addLocation() called                                   │
│     ↓                                                                           │
│  2. DatabaseService.saveLocation(123, "Paris", 48.8566, 2.3522)               │
│     ├─ Open database session                                                   │
│     ├─ Find user by ID 123                                                     │
│     ├─ Create SavedLocation object                                             │
│     ├─ Save to database                                                        │
│     └─ Close session                                                           │
│     ↓                                                                           │
│  3. Add "Paris" to UI table                                                    │
│     ↓                                                                           │
│  4. DashboardController.refreshChart() called                                  │
│     ↓                                                                           │
│  5. WeatherService.getHourlyForecast(48.8566, 2.3522, "metric")               │
│     ├─ Build API URL with coordinates                                          │
│     ├─ Send HTTP request to OpenWeatherMap                                     │
│     ├─ Parse JSON response                                                     │
│     └─ Return weather forecast                                                 │
│     ↓                                                                           │
│  6. Update temperature chart with Paris weather data                           │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### **Weather Service Fallback Flow:**
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                         Weather Service Fallback                               │
│                                                                                 │
│  5. WeatherService throws IOException (API key invalid/network error)          │
│     ↓                                                                           │
│  6. FreeWeatherService.getHourlyForecast(48.8566, 2.3522, "metric")           │
│     ├─ Send HTTP request to wttr.in                                            │
│     ├─ Parse different JSON format                                             │
│     └─ Return fallback weather data                                            │
│     ↓                                                                           │
│  7. Update chart with fallback data                                            │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 📊 **Service Layer Summary**

| Service | Purpose | Input | Output | Database/API |
|---------|---------|-------|--------|--------------|
| **DatabaseService** | Data persistence | User data, locations | Database records | Oracle Database |
| **WeatherService** | Primary weather data | Coordinates, units | Weather forecast | OpenWeatherMap API |
| **FreeWeatherService** | Fallback weather data | Coordinates, units | Weather forecast | wttr.in API |

## 🔧 **Key Design Patterns Used**

1. **Singleton Pattern**: DatabaseService (only one instance needed)
2. **Strategy Pattern**: WeatherService + FreeWeatherService (multiple weather providers)
3. **Factory Pattern**: Hibernate SessionFactory (creates database sessions)
4. **Template Method**: All services follow similar HTTP request patterns

## 🎯 **Service Responsibilities**

- **DatabaseService**: Handles all database operations (CRUD)
- **WeatherService**: Fetches real-time weather data from OpenWeatherMap
- **FreeWeatherService**: Provides backup weather data when primary service fails

This workflow shows how each service class operates independently while working together to provide the complete functionality of your weather application.
