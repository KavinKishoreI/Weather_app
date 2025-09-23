# JavaFX UI Components Guide

This guide explains the FXML structure, event handlers, and UI components used in this JavaFX Weather Application.

## Table of Contents
1. [FXML Overview](#fxml-overview)
2. [Login Screen (LoginView.fxml)](#login-screen)
3. [Dashboard Screen (DashboardView.fxml)](#dashboard-screen)
4. [Event Handlers](#event-handlers)
5. [CSS Styling](#css-styling)
6. [Controller Architecture](#controller-architecture)

---

## FXML Overview

FXML (FXML Markup Language) is JavaFX's XML-based language for defining user interfaces. It separates UI design from business logic, making applications more maintainable.

### Key FXML Concepts:
- **fx:controller**: Links the FXML to a Java controller class
- **fx:id**: Unique identifier for UI elements (connects to @FXML fields)
- **onAction**: Event handler method name (prefixed with #)
- **styleClass**: CSS class name for styling

---

## Login Screen

**File**: `src/main/resources/fxml/LoginView.fxml`
**Controller**: `org.example.ui.LoginController`

### FXML Structure:
```xml
<VBox fx:controller="org.example.ui.LoginController">
    <!-- App Title -->
    <Label text="Weather App Demo" styleClass="app-title"/>
    
    <!-- Form Grid -->
    <GridPane>
        <TextField fx:id="emailField" promptText="you@example.com"/>
        <PasswordField fx:id="passwordField" promptText="••••••••"/>
        <Button fx:id="loginButton" onAction="#onLogin"/>
        <Button fx:id="registerButton" onAction="#onRegister"/>
    </GridPane>
    
    <!-- Status Display -->
    <Label fx:id="statusLabel" textFill="#d32f2f"/>
</VBox>
```

### UI Components:

| Component | fx:id | Purpose | Controller Field |
|-----------|-------|---------|------------------|
| `TextField` | `emailField` | Email input | `@FXML private TextField emailField` |
| `PasswordField` | `passwordField` | Password input | `@FXML private PasswordField passwordField` |
| `Button` | `loginButton` | Login action | `@FXML private Button loginButton` |
| `Button` | `registerButton` | Registration action | `@FXML private Button registerButton` |
| `Label` | `statusLabel` | Error/success messages | `@FXML private Label statusLabel` |

### Event Handlers:
- `onAction="#onLogin"` → `@FXML private void onLogin()`
- `onAction="#onRegister"` → `@FXML private void onRegister()`

---

## Dashboard Screen

**File**: `src/main/resources/fxml/DashboardView.fxml`
**Controller**: `org.example.ui.DashboardController`

### FXML Structure:
```xml
<BorderPane fx:controller="org.example.ui.DashboardController">
    <top>    <!-- Top Bar with Welcome & Units -->
    <left>   <!-- Side Panel with Locations & Add Form -->
    <center> <!-- Main Chart Area -->
</BorderPane>
```

### Top Bar Components:

| Component | fx:id | Purpose |
|-----------|-------|---------|
| `Label` | `welcomeLabel` | Welcome message |
| `ChoiceBox` | `unitsChoice` | Temperature units (Metric/Imperial) |

### Left Panel Components:

| Component | fx:id | Purpose |
|-----------|-------|---------|
| `TableView` | `locationsTable` | Saved locations list |
| `TableColumn` | `colName` | Location name column |
| `TableColumn` | `colLat` | Latitude column |
| `TableColumn` | `colLon` | Longitude column |
| `TextField` | `nameField` | New location name |
| `TextField` | `latField` | New location latitude |
| `TextField` | `lonField` | New location longitude |
| `Button` | `addButton` | Add location |
| `Button` | `removeButton` | Remove selected location |
| `Button` | `refreshButton` | Refresh weather chart |

### Center Panel Components:

| Component | fx:id | Purpose |
|-----------|-------|---------|
| `LineChart` | `temperatureChart` | Weather data visualization |
| `CategoryAxis` | `timeAxis` | X-axis (time) |
| `NumberAxis` | `valueAxis` | Y-axis (temperature) |

---

## Event Handlers

### LoginController Event Handlers:

```java
@FXML
private void onLogin() {
    // Handles login button click
    // Validates input, authenticates user, navigates to dashboard
}

@FXML
private void onRegister() {
    // Handles register button click
    // Validates input, creates new user, navigates to dashboard
}

@FXML
private void initialize() {
    // Called automatically after FXML injection
    // Sets up initial state
}
```

### DashboardController Event Handlers:

```java
@FXML
private void initialize() {
    // Sets up table columns, choice box, button actions
    // Configures chart properties
}

// Button Actions (set in initialize() method):
addButton.setOnAction(e -> addLocation());
removeButton.setOnAction(e -> removeSelected());
refreshButton.setOnAction(e -> refreshChart());

// Choice Box Action:
unitsChoice.setOnAction(e -> refreshChart());

// Table Selection Listener:
locationsTable.getSelectionModel().selectedItemProperty()
    .addListener((obs, oldSel, newSel) -> refreshChart());

// Keyboard Event:
lonField.setOnKeyPressed(e -> {
    if (e.getCode() == KeyCode.ENTER) addLocation();
});
```

---

## CSS Styling

**File**: `src/main/resources/css/styles.css`

### Key CSS Classes:

```css
.app-title {
    -fx-font-size: 28px;
    -fx-font-weight: bold;
}

.section-title {
    -fx-font-size: 16px;
    -fx-font-weight: bold;
    -fx-text-fill: #333;
}

.top-bar {
    -fx-background-color: linear-gradient(to bottom, #ffffff, #f2f2f2);
    -fx-border-color: #e0e0e0;
    -fx-border-width: 0 0 1 0;
}

.side-panel {
    -fx-background-color: #ffffff;
    -fx-border-color: #e0e0e0;
    -fx-border-width: 0 1 0 0;
}
```

### CSS Usage in FXML:
```xml
<Label text="Weather App Demo" styleClass="app-title"/>
<HBox styleClass="top-bar">
<VBox styleClass="side-panel">
```

---

## Controller Architecture

### @FXML Annotation Usage:

#### 1. Field Injection:
```java
@FXML private TextField emailField;        // Injected from FXML
@FXML private Button loginButton;           // Injected from FXML
@FXML private TableView<LocationRow> locationsTable; // Injected from FXML
```

#### 2. Method Injection:
```java
@FXML
private void initialize() {
    // Called automatically after FXML loading
    // Perfect for setup code
}

@FXML
private void onLogin() {
    // Event handler method
    // Called when button is clicked
}
```

### Controller Lifecycle:

1. **FXML Loading**: JavaFX loads the FXML file
2. **Controller Creation**: Controller instance is created
3. **Field Injection**: @FXML fields are populated with UI components
4. **initialize() Call**: @FXML initialize() method is called
5. **Event Binding**: Event handlers are connected
6. **User Interaction**: Events trigger controller methods

### Data Binding:

```java
// Table Column Data Binding
colName.setCellValueFactory(data -> 
    new SimpleStringProperty(data.getValue().getName()));

// Observable List for Table
private final ObservableList<LocationRow> locations = 
    FXCollections.observableArrayList();
locationsTable.setItems(locations);
```

---

## Key JavaFX Concepts

### 1. **Layout Containers**:
- `VBox`: Vertical layout
- `HBox`: Horizontal layout
- `GridPane`: Grid-based layout
- `BorderPane`: Top, bottom, left, right, center regions

### 2. **Control Components**:
- `TextField`: Text input
- `PasswordField`: Password input
- `Button`: Clickable buttons
- `Label`: Text display
- `TableView`: Data table
- `ChoiceBox`: Dropdown selection
- `LineChart`: Data visualization

### 3. **Event Handling**:
- `onAction`: Button clicks
- `setOnAction()`: Programmatic event binding
- `selectedItemProperty()`: Selection change listeners
- `setOnKeyPressed()`: Keyboard events

### 4. **Data Binding**:
- `ObservableList`: Reactive data collections
- `SimpleStringProperty`: Observable string values
- `CellValueFactory`: Table column data binding

---

## Best Practices

### 1. **FXML Design**:
- Use meaningful fx:id names
- Group related components in containers
- Apply consistent styleClass names
- Keep FXML files focused and readable

### 2. **Controller Design**:
- Use @FXML for all UI-related fields and methods
- Keep business logic separate from UI logic
- Use initialize() for setup code
- Handle exceptions gracefully

### 3. **Event Handling**:
- Use descriptive method names (onLogin, onRegister)
- Validate input before processing
- Provide user feedback for all actions
- Handle both success and error cases

### 4. **Styling**:
- Use CSS classes instead of inline styles
- Create reusable style classes
- Follow consistent naming conventions
- Test across different screen sizes

---

This guide covers the complete UI architecture of your JavaFX Weather Application, from FXML structure to event handling patterns. The separation of concerns between FXML (view), controllers (logic), and CSS (styling) makes the application maintainable and scalable.
