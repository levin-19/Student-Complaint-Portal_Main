package main;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import helpers.SceneSwitcher;
import helpers.DatabaseConfig;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("=== Student Complaint Portal Starting ===");

        if (!DatabaseConfig.testConnection()) {
            showDatabaseError();
            return;
        }

        SceneSwitcher.setPrimaryStage(primaryStage);
        SceneSwitcher.switchScene("LoginScene.fxml", "Student Complaint Portal - Login");

        primaryStage.setResizable(false);
        primaryStage.show();
        
        System.out.println("✅ Application started successfully!");
    }

    private void showDatabaseError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Connection Error");
        alert.setHeaderText("Cannot connect to database");
        alert.setContentText("Please make sure XAMPP MySQL is running on port 3306");
        alert.showAndWait();
        System.exit(1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
