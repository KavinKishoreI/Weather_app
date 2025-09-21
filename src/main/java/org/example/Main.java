package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.ui.SceneRouter;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        SceneRouter.init(stage, "Weather App Demo");
        SceneRouter.get().showLogin();
    }

    public static void main(String[] args) {
        launch();
    }
}
