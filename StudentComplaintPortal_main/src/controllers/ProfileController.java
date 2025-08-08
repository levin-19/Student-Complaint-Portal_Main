package controllers;

import helpers.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import models.Student;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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
    @FXML private Button uploadPictureButton;
    @FXML private ImageView profileImageView;

    private boolean isEditing = false;
    private byte[] newProfilePicture = null;
    private String newProfilePictureMime = null;
    private static final long MAX_IMAGE_BYTES = 2L * 1024 * 1024; // 2 MB

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Student currentStudent = SceneSwitcher.getCurrentStudent();
        if (currentStudent != null) {
            nameField.setText(currentStudent.getName());
            studentIdField.setText(currentStudent.getStudentId());
            departmentField.setText(currentStudent.getDepartment());
            mobileField.setText(currentStudent.getMobile());
            emailField.setText(currentStudent.getEmail());

            // Load profile image if present
            if (currentStudent.getProfilePicture() != null && currentStudent.getProfilePicture().length > 0) {
                try {
                    Image image = new Image(new ByteArrayInputStream(currentStudent.getProfilePicture()));
                    profileImageView.setImage(image);
                    profileImageView.setPreserveRatio(true);
                    profileImageView.setFitWidth(140);
                    profileImageView.setFitHeight(140);
                } catch (Exception e) {
                    System.err.println("Error loading profile image: " + e.getMessage());
                    profileImageView.setImage(null);
                }
            } else {
                profileImageView.setImage(null);
            }
        }

        setFieldsEditable(false);
        uploadPictureButton.setVisible(false);
    }

    @FXML
    private void handleEdit() {
        isEditing = true;
        setFieldsEditable(true);
        editButton.setVisible(false);
        saveButton.setVisible(true);
        cancelButton.setVisible(true);
        uploadPictureButton.setVisible(true);
        backButton.setVisible(false); // Hide back button during editing
    }

    @FXML
    private void handleUploadPicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (file != null) {
            if (file.length() > MAX_IMAGE_BYTES) {
                double mb = file.length() / (1024.0 * 1024.0);
                showAlert("Image too large", String.format("Selected image is %.2f MB. Please choose a file under 2.00 MB.", mb));
                return;
            }

            try {
                String mimeByExt = detectMimeFromFile(file);
                if (!mimeByExt.equals("image/png") && !mimeByExt.equals("image/jpeg") && !mimeByExt.equals("image/gif")) {
                    showAlert("Unsupported image type", "Allowed file types: PNG, JPG, JPEG, GIF.");
                    return;
                }

                newProfilePicture = Files.readAllBytes(file.toPath());
                newProfilePictureMime = mimeByExt;

                try (FileInputStream fis = new FileInputStream(file)) {
                    Image image = new Image(fis);
                    profileImageView.setImage(image);
                    profileImageView.setPreserveRatio(true);
                    profileImageView.setFitWidth(140);
                    profileImageView.setFitHeight(140);
                }
            } catch (IOException e) {
                newProfilePicture = null;
                newProfilePictureMime = null;
                showAlert("Failed to load image", "Could not read the selected file. Please try another image.");
            }
        }
    }

    private String detectMimeFromFile(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }

    @FXML
    private void handleSave() {
        Student currentStudent = SceneSwitcher.getCurrentStudent();
        if (currentStudent != null) {
            currentStudent.setName(nameField.getText().trim());
            currentStudent.setStudentId(studentIdField.getText().trim());
            currentStudent.setDepartment(departmentField.getText().trim());
            currentStudent.setMobile(mobileField.getText().trim());

            // Update profile picture if new one was selected
            if (newProfilePicture != null) {
                currentStudent.setProfilePicture(newProfilePicture);
                currentStudent.setProfilePictureMime(newProfilePictureMime);
            }

            if (SceneSwitcher.updateStudent(currentStudent)) {
                showAlert("Success", "Profile updated successfully!");
                newProfilePicture = null;
                newProfilePictureMime = null;
            } else {
                showAlert("Error", "Failed to update profile.");
            }
        }

        isEditing = false;
        setFieldsEditable(false);
        editButton.setVisible(true);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        uploadPictureButton.setVisible(false);
        backButton.setVisible(true); // Show back button after editing
    }

    @FXML
    private void handleCancel() {
        initialize(null, null);
        newProfilePicture = null;
        newProfilePictureMime = null;
        isEditing = false;
        setFieldsEditable(false);
        editButton.setVisible(true);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        uploadPictureButton.setVisible(false);
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
