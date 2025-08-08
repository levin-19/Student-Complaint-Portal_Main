package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import helpers.SceneSwitcher;
import models.Student;
import javafx.scene.layout.Region;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private void showMessage(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setResizable(true);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText() != null ? emailField.getText().trim().toLowerCase() : "";
        String password = passwordField.getText() != null ? passwordField.getText() : "";

        System.out.println("🔄 Login attempt for: " + email);

        if (email.isEmpty() || password.isEmpty()) {
            showMessage(Alert.AlertType.ERROR, "Login Error", "Missing credentials", "Please enter both email and password.");
            return;
        }

        // Check admin login first
        if (SceneSwitcher.isAdminLogin(email, password)) {
            System.out.println("✅ Admin login successful");
            SceneSwitcher.switchScene("AdminDashboard.fxml", "Admin Dashboard");
            return;
        }

        // Check student login
        if (SceneSwitcher.isValidLogin(email, password)) {
            Student student = SceneSwitcher.findStudentByEmail(email);
            SceneSwitcher.setCurrentStudent(student);
            System.out.println("✅ Student login successful: " + email);
            SceneSwitcher.switchScene("StudentDashboard.fxml", "Student Dashboard");
        } else {
            System.err.println("❌ Login failed for: " + email);
            showMessage(Alert.AlertType.ERROR, "Login Error", "Invalid credentials", "Email or password is incorrect.");
        }
    }

    @FXML
    private void handleSignUp() {
        System.out.println("🔄 Attempting to switch to sign-up scene...");
        
        try {
            // Debug: Check if FXML file exists
            var resource = getClass().getResource("/scenes/SignUpScene.fxml");
            if (resource == null) {
                System.err.println("❌ SignUpScene.fxml not found in /scenes/ directory");
                showMessage(Alert.AlertType.ERROR, "File Not Found", "SignUp scene missing", 
                    "SignUpScene.fxml file is missing from the scenes folder.");
                return;
            }
            
            System.out.println("✅ SignUpScene.fxml found at: " + resource.toString());
            
            // Attempt scene switch
            SceneSwitcher.switchScene("SignUpScene.fxml", "Sign Up");
            System.out.println("✅ Successfully switched to sign-up scene");
            
        } catch (Exception e) {
            System.err.println("❌ Error switching to sign-up scene: " + e.getMessage());
            e.printStackTrace();
            
            // Show detailed error to user
            showMessage(Alert.AlertType.ERROR, "Navigation Error", 
                "Cannot load sign-up page", 
                "Error: " + e.getMessage() + "\n\nPlease check:\n" +
                "1. SignUpScene.fxml exists in scenes folder\n" +
                "2. SignUpController class exists\n" +
                "3. All imports are correct");
        }
    }
}
