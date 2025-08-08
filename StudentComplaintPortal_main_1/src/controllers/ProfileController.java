package controllers;

import helpers.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import models.Student;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField studentIdField;
    @FXML private TextField departmentField;
    @FXML private TextField mobileField;
    @FXML private TextField emailField;
    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    private boolean isEditing = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Student currentStudent = SceneSwitcher.getCurrentStudent();
        if (currentStudent != null) {
            nameField.setText(currentStudent.getName());
            studentIdField.setText(currentStudent.getStudentId());
            departmentField.setText(currentStudent.getDepartment());
            mobileField.setText(currentStudent.getMobile());
            emailField.setText(currentStudent.getEmail());
        }

        setFieldsEditable(false);
    }

    @FXML
    private void handleEdit() {
        isEditing = true;
        setFieldsEditable(true);
        editButton.setVisible(false);
        saveButton.setVisible(true);
        cancelButton.setVisible(true);
        backButton.setVisible(false); // Hide back button during editing
    }

    @FXML
    private void handleSave() {
        Student currentStudent = SceneSwitcher.getCurrentStudent();
        if (currentStudent != null) {
            currentStudent.setName(nameField.getText().trim());
            currentStudent.setStudentId(studentIdField.getText().trim());
            currentStudent.setDepartment(departmentField.getText().trim());
            currentStudent.setMobile(mobileField.getText().trim());

            if (SceneSwitcher.updateStudent(currentStudent)) {
                showAlert("Success", "Profile updated successfully!");
            } else {
                showAlert("Error", "Failed to update profile.");
            }
        }

        isEditing = false;
        setFieldsEditable(false);
        editButton.setVisible(true);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        backButton.setVisible(true); // Show back button after editing
    }

    @FXML
    private void handleCancel() {
        initialize(null, null);
        isEditing = false;
        setFieldsEditable(false);
        editButton.setVisible(true);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        backButton.setVisible(true); // Show back button after canceling
    }

    @FXML
    private void handleBack() {
        if (isEditing) {
            // If currently editing, ask for confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes");
            alert.setContentText("Do you want to discard your changes and go back?");
            
            if (alert.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
                SceneSwitcher.switchScene("StudentDashboard.fxml", "Student Dashboard");
            }
        } else {
            SceneSwitcher.switchScene("StudentDashboard.fxml", "Student Dashboard");
        }
    }

    private void setFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        studentIdField.setEditable(editable);
        departmentField.setEditable(editable);
        mobileField.setEditable(editable);
        emailField.setEditable(false); // Email should never be editable
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
