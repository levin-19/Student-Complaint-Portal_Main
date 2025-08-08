package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import helpers.SceneSwitcher;
import models.Complaint;
import java.net.URL;
import java.util.ResourceBundle;

public class EditComplaintController implements Initializable {
    
    @FXML private TextField titleField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextArea descriptionArea;
    
    private Complaint currentComplaint;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryComboBox.getItems().addAll("Academic", "Facility", "Admin", "Other");
        
        currentComplaint = SceneSwitcher.getCurrentComplaint();
        if (currentComplaint != null) {
            titleField.setText(currentComplaint.getTitle());
            categoryComboBox.setValue(currentComplaint.getCategory());
            descriptionArea.setText(currentComplaint.getDescription());
        }
    }
    
    @FXML
    private void handleSave() {
        String title = titleField.getText().trim();
        String category = categoryComboBox.getValue();
        String description = descriptionArea.getText().trim();
        
        if (title.isEmpty() || category == null || description.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }
        
        if (currentComplaint != null) {
            currentComplaint.setTitle(title);
            currentComplaint.setCategory(category);
            currentComplaint.setDescription(description);
            
            if (SceneSwitcher.updateComplaint(currentComplaint)) {
                showAlert("Success", "Complaint updated successfully!");
                SceneSwitcher.switchScene("MyComplaints.fxml", "My Complaints");
            } else {
                showAlert("Error", "Failed to update complaint.");
            }
        }
    }
    
    @FXML
    private void handleCancel() {
        SceneSwitcher.switchScene("MyComplaints.fxml", "My Complaints");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
