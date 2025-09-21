package org.example.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public final class SceneRouter {
    private static SceneRouter INSTANCE;
    private final Stage stage;
    private final String appTitle;

    private SceneRouter(Stage stage, String appTitle) {
        this.stage = stage;
        this.appTitle = appTitle;
    }

    public static void init(Stage stage, String appTitle) {
        if (INSTANCE == null) {
            INSTANCE = new SceneRouter(stage, appTitle);
        }
    }

    public static SceneRouter get() {
        if (INSTANCE == null) {
            throw new IllegalStateException("SceneRouter not initialized. Call init() first.");
        }
        return INSTANCE;
    }

    public void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(resource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);
            applyStyles(scene);
            stage.setScene(scene);
            stage.setTitle(appTitle + " - Login");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Login view", e);
        }
    }

    public void showDashboard(org.example.User user) {
        try {
            FXMLLoader loader = new FXMLLoader(resource("/fxml/DashboardView.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setUser(user);

            Scene scene = new Scene(root, 1200, 800);
            applyStyles(scene);
            stage.setScene(scene);
            stage.setTitle(appTitle + " - Dashboard");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Dashboard view", e);
        }
    }

    private void applyStyles(Scene scene) {
        scene.getStylesheets().add(resource("/css/styles.css").toExternalForm());
    }

    private java.net.URL resource(String path) {
        return Objects.requireNonNull(getClass().getResource(path), "Resource not found: " + path);
    }
}
