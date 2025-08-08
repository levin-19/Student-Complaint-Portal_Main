package controllers;

import helpers.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.Student;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField studentIdField;
    @FXML private TextField departmentField;
    @FXML private TextField mobileField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("✅ SignUpController initialized successfully");
        System.out.println("📍 Location: " + (location != null ? location.toString() : "null"));
        
        // Test all field references
        System.out.println("🔍 Field check:");
        System.out.println("  nameField: " + (nameField != null ? "✅" : "❌"));
        System.out.println("  emailField: " + (emailField != null ? "✅" : "❌"));
        System.out.println("  passwordField: " + (passwordField != null ? "✅" : "❌"));
    }

    private void showError(String header, String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(header);
        alert.setContentText(details);
        alert.setResizable(true);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setResizable(true);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isDigits(String value) {
        return value.matches("^\\d+$");
    }

    @FXML
    private void handleRegister() {
        System.out.println("🔄 Registration process started");
        
        String name = nameField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String department = departmentField.getText().trim();
        String mobile = mobileField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        System.out.println("📝 Registration data:");
        System.out.println("  Name: " + name);
        System.out.println("  Student ID: " + studentId);
        System.out.println("  Email: " + email);

        List<String> issues = new ArrayList<>();

        // Validation
        if (name.isEmpty()) issues.add("Full Name is required.");
        if (studentId.isEmpty()) issues.add("Student ID is required.");
        if (department.isEmpty()) issues.add("Department is required.");

        if (mobile.isEmpty()) {
            issues.add("Mobile Number is required.");
        } else {
            if (!isDigits(mobile)) issues.add("Mobile Number must contain digits only.");
            if (mobile.length() < 7 || mobile.length() > 15) issues.add("Mobile Number must be 7 to 15 digits long.");
        }

        if (email.isEmpty()) {
            issues.add("Email Address is required.");
        } else if (!isValidEmail(email)) {
            issues.add("Email Address format is invalid.");
        }

        if (password.isEmpty()) {
            issues.add("Password is required.");
        } else if (password.length() < 6) {
            issues.add("Password must be at least 6 characters long.");
        }

        if (!password.equals(confirmPassword)) {
            issues.add("Passwords do not match.");
        }

        // Check for existing email/student ID
        if (SceneSwitcher.isEmailExists(email)) {
            issues.add("Email already exists. Use a different email.");
        }

        if (SceneSwitcher.isStudentIdExists(studentId)) {
            issues.add("Student ID already exists. Use a different ID.");
        }

        if (!issues.isEmpty()) {
            System.err.println("❌ Validation failed: " + issues.size() + " issues");
            showError("Please fix the following:", "• " + String.join("\n• ", issues));
            return;
        }

        // Create student
        Student newStudent = new Student(name, studentId, department, mobile, email, password);

        System.out.println("💾 Attempting to save student to database...");
        
        if (SceneSwitcher.addStudent(newStudent)) {
            System.out.println("✅ Student registered successfully: " + email);
            showInfo("Success", "Account created successfully! You can now login.");
            
            // Clear form
            clearForm();
            
            // Switch back to login
            SceneSwitcher.switchScene("LoginScene.fxml", "Login");
        } else {
            System.err.println("❌ Registration failed for: " + email);
            showError("Registration failed", "We couldn't create your account. Please try again.");
        }
    }

    @FXML
    private void handleBack() {
        System.out.println("🔄 Returning to login scene...");
        clearForm();
        SceneSwitcher.switchScene("LoginScene.fxml", "Login");
    }
    
    private void clearForm() {
        nameField.clear();
        studentIdField.clear();
        departmentField.clear();
        mobileField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}
