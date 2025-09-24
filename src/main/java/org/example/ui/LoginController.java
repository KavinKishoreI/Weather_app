package org.example.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.DatabaseService;
import org.example.User;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    
    private DatabaseService databaseService;

    @FXML
    private void initialize() {
        statusLabel.setText("");
        
        // Check if database is properly configured
        if (!DatabaseService.isDatabaseConfigured()) {
            String errorMsg = DatabaseService.getDatabaseConfigurationError();
            statusLabel.setText("Database configuration error: " + errorMsg);
            statusLabel.setStyle("-fx-text-fill: red;");
            loginButton.setDisable(true);
            registerButton.setDisable(true);
        } else {
            try {
                databaseService = DatabaseService.getInstance();
            } catch (Exception e) {
                statusLabel.setText("Failed to initialize database: " + e.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
                loginButton.setDisable(true);
                registerButton.setDisable(true);
            }
        }
    }

    @FXML
    private void onLogin() {
        if (!validate()) return;
        
        if (databaseService == null) {
            statusLabel.setText("Database not available. Please check configuration.");
            return;
        }
        
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        
        try {
            User user = databaseService.authenticateUser(email, password);
            if (user != null) {
                SceneRouter.get().showDashboard(user);
            } else {
                statusLabel.setText("Invalid email or password.");
            }
        } catch (Exception e) {
            statusLabel.setText("Login failed: " + e.getMessage());
        }
    }

    @FXML
    private void onRegister() {
        if (!validate()) return;
        
        if (databaseService == null) {
            statusLabel.setText("Database not available. Please check configuration.");
            return;
        }
        
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String displayName = inferDisplayName(email);
        
        try {
            User user = databaseService.registerUser(email, password, displayName);
            SceneRouter.get().showDashboard(user);
        } catch (Exception e) {
            statusLabel.setText("Registration failed: " + e.getMessage());
        }
    }

    private boolean validate() {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter email and password.");
            return false;
        }
        if (!email.contains("@")) {
            statusLabel.setText("Please enter a valid email.");
            return false;
        }
        statusLabel.setText("");
        return true;
    }

    private String inferDisplayName(String email) {
        int at = email.indexOf('@');
        String name = at > 0 ? email.substring(0, at) : email;
        if (name.isEmpty()) name = "User";
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
