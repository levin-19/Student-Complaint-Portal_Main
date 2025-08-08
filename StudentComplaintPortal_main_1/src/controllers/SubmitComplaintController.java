package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import helpers.SceneSwitcher;
import models.Complaint;
import models.Student;
import java.net.URL;
import java.util.ResourceBundle;

public class SubmitComplaintController implements Initializable {
    
    @FXML private TextField titleField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextArea descriptionArea;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryComboBox.getItems().addAll("Academic", "Facility", "Admin", "Other");
    }
    
    @FXML
    private void handleSubmit() {
        String title = titleField.getText().trim();
        String category = categoryComboBox.getValue();
        String description = descriptionArea.getText().trim();
        
        if (title.isEmpty() || category == null || description.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }
        
        Student currentStudent = SceneSwitcher.getCurrentStudent();
        if (currentStudent != null) {
            Complaint complaint = new Complaint(title, description, category, currentStudent.getEmail());
            SceneSwitcher.addComplaint(complaint);
            
            showAlert("Success", "Complaint submitted successfully!");
            SceneSwitcher.switchScene("StudentDashboard.fxml", "Student Dashboard");
        }
    }
    
    @FXML
    private void handleBack() {
        SceneSwitcher.switchScene("StudentDashboard.fxml", "Student Dashboard");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
