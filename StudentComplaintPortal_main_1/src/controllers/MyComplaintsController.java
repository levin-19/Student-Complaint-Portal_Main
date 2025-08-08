package controllers;

import helpers.SceneSwitcher;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Complaint;
import models.Student;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyComplaintsController implements Initializable {

    @FXML private TableView<Complaint> complaintsTable;
    @FXML private TableColumn<Complaint, Integer> idColumn;
    @FXML private TableColumn<Complaint, String> titleColumn;
    @FXML private TableColumn<Complaint, String> categoryColumn;
    @FXML private TableColumn<Complaint, String> statusColumn;
    @FXML private TableColumn<Complaint, String> feedbackColumn; // new
    @FXML private TableColumn<Complaint, String> actionsColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        feedbackColumn.setCellValueFactory(new PropertyValueFactory<>("feedback"));

        actionsColumn.setCellValueFactory(cellData -> new SimpleStringProperty("Actions"));
        actionsColumn.setCellFactory(column -> new TableCell<Complaint, String>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    Complaint complaint = getTableView().getItems().get(getIndex());
                    editComplaint(complaint);
                });

                deleteButton.setOnAction(event -> {
                    Complaint complaint = getTableView().getItems().get(getIndex());
                    deleteComplaint(complaint);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new javafx.scene.layout.HBox(5, editButton, deleteButton));
                }
            }
        });

        loadComplaints();
    }

    private void loadComplaints() {
        Student currentStudent = SceneSwitcher.getCurrentStudent();
        if (currentStudent != null) {
            complaintsTable.getItems().clear();
            List<Complaint> complaints = SceneSwitcher.getComplaintsByStudentEmail(currentStudent.getEmail());
            complaintsTable.getItems().addAll(complaints);
        }
    }

    private void editComplaint(Complaint complaint) {
        SceneSwitcher.setCurrentComplaint(complaint);
        SceneSwitcher.switchScene("EditComplaint.fxml", "Edit Complaint");
    }

    private void deleteComplaint(Complaint complaint) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this complaint?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (SceneSwitcher.deleteComplaint(complaint.getId())) {
                loadComplaints();
                showAlert("Success", "Complaint deleted successfully.");
            } else {
                showAlert("Error", "Failed to delete complaint.");
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        SceneSwitcher.switchScene("StudentDashboard.fxml", "Student Dashboard");
    }
}
