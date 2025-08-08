package controllers;

import helpers.SceneSwitcher;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Complaint;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private Label totalStudentsLabel;
    @FXML private Label totalComplaintsLabel;
    @FXML private Label pendingLabel;
    @FXML private Label ongoingLabel; // displays "In Progress"
    @FXML private Label resolvedLabel;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Complaint> complaintsTable;
    @FXML private TableColumn<Complaint, Integer> idColumn;
    @FXML private TableColumn<Complaint, String> studentEmailColumn;
    @FXML private TableColumn<Complaint, String> titleColumn;
    @FXML private TableColumn<Complaint, String> categoryColumn;
    @FXML private TableColumn<Complaint, String> statusColumn;
    @FXML private TableColumn<Complaint, String> feedbackColumn;
    @FXML private TableColumn<Complaint, String> actionsColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusFilter.getItems().clear();
        statusFilter.getItems().addAll("All", "Pending", "In Progress", "Resolved");
        statusFilter.setValue("All");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("studentEmail"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        feedbackColumn.setCellValueFactory(new PropertyValueFactory<>("feedback"));

        actionsColumn.setCellValueFactory(cellData -> new SimpleStringProperty("Actions"));
        actionsColumn.setCellFactory(column -> new TableCell<Complaint, String>() {
            private final Button statusButton = new Button("Change Status");
            private final Button feedbackButton = new Button("Feedback");
            private final Button deleteButton = new Button("Delete");

            {
                statusButton.setOnAction(event -> {
                    Complaint complaint = getTableView().getItems().get(getIndex());
                    changeStatus(complaint);
                });

                feedbackButton.setOnAction(event -> {
                    Complaint complaint = getTableView().getItems().get(getIndex());
                    editFeedback(complaint);
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
                    setGraphic(new javafx.scene.layout.HBox(8, statusButton, feedbackButton, deleteButton));
                }
            }
        });

        loadComplaints();
        updateStats();
    }

    private void loadComplaints() {
        String selectedStatus = statusFilter.getValue();
        List<Complaint> complaints;

        if ("All".equals(selectedStatus)) {
            complaints = SceneSwitcher.getAllComplaints();
        } else {
            complaints = SceneSwitcher.getComplaintsByStatus(selectedStatus);
        }

        complaintsTable.getItems().setAll(complaints);
    }

    private void updateStats() {
        int totalStudents = SceneSwitcher.getTotalStudentCount();
        int total = SceneSwitcher.getTotalComplaintCount();
        int pending = SceneSwitcher.getComplaintCountByStatus("Pending");
        int inProgress = SceneSwitcher.getComplaintCountByStatus("In Progress");
        int resolved = SceneSwitcher.getComplaintCountByStatus("Resolved");

        totalStudentsLabel.setText("Students: " + totalStudents);
        totalComplaintsLabel.setText("Total: " + total);
        pendingLabel.setText("Pending: " + pending);
        ongoingLabel.setText("In Progress: " + inProgress);
        resolvedLabel.setText("Resolved: " + resolved);
    }

    private void changeStatus(Complaint complaint) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(complaint.getStatus(), "Pending", "In Progress", "Resolved");
        dialog.setTitle("Change Status");
        dialog.setHeaderText("Change status for Complaint #" + complaint.getId());
        dialog.setContentText("Select new status:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(status -> {
            boolean ok = SceneSwitcher.setComplaintStatus(complaint.getId(), status);
            if (ok) {
                loadComplaints();
                updateStats();
                showInfo("Success", "Status updated to " + status + ".");
            } else {
                showError("Failed to update status.");
            }
        });
    }

    private void editFeedback(Complaint complaint) {
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setPrefRowCount(8);
        textArea.setText(complaint.getFeedback() != null ? complaint.getFeedback() : "");

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add/Edit Feedback");
        dialog.setHeaderText("Feedback for Complaint #" + complaint.getId());
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String newFeedback = textArea.getText().trim();
            boolean ok = SceneSwitcher.setComplaintFeedback(complaint.getId(), newFeedback.isEmpty() ? null : newFeedback);
            if (ok) {
                loadComplaints();
                showInfo("Success", "Feedback updated.");
            } else {
                showError("Failed to update feedback.");
            }
        }
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
                updateStats();
                showInfo("Success", "Complaint deleted successfully.");
            } else {
                showError("Failed to delete complaint.");
            }
        }
    }

    @FXML
    private void handleFilter() {
        loadComplaints();
    }

    @FXML
    private void handleLogout() {
        SceneSwitcher.switchScene("LoginScene.fxml", "Login");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Operation Failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
