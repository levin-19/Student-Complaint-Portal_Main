package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import helpers.SceneSwitcher;
import models.Student;
import java.net.URL;
import java.util.ResourceBundle;

public class StudentDashboardController implements Initializable {
    
    @FXML private Label welcomeLabel;
    @FXML private Label totalComplaintsLabel;
    @FXML private Label resolvedComplaintsLabel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Student currentStudent = SceneSwitcher.getCurrentStudent();
        if (currentStudent != null) {
            welcomeLabel.setText("Welcome, " + currentStudent.getName() + "!");
            
            int totalComplaints = SceneSwitcher.getComplaintCountByStudentEmail(currentStudent.getEmail());
            int resolvedComplaints = SceneSwitcher.getResolvedComplaintCountByStudentEmail(currentStudent.getEmail());
            
            totalComplaintsLabel.setText("Total Complaints: " + totalComplaints);
            resolvedComplaintsLabel.setText("Resolved Complaints: " + resolvedComplaints);
        }
    }
    
    @FXML
    private void handleSubmitComplaint() {
        SceneSwitcher.switchScene("SubmitComplaint.fxml", "Submit Complaint");
    }
    
    @FXML
    private void handleViewComplaints() {
        SceneSwitcher.switchScene("MyComplaints.fxml", "My Complaints");
    }
    
    @FXML
    private void handleProfile() {
        SceneSwitcher.switchScene("ProfileScene.fxml", "Profile");
    }
    
    @FXML
    private void handleLogout() {
        SceneSwitcher.setCurrentStudent(null);
        SceneSwitcher.switchScene("LoginScene.fxml", "Login");
    }
}
