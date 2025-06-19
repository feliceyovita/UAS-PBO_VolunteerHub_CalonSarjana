package com.example.uasvolunteerhub;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class adminHomeController {

    @FXML private TableView<ActivityRow> activityTable;
    @FXML private TableColumn<ActivityRow, String> colActNo;
    @FXML private TableColumn<ActivityRow, String> colActTitle;
    @FXML private TableColumn<ActivityRow, Void> colActAction;
    @FXML private Label totalActivitiesLabel;

    @FXML private TableView<VolunteerRow> volunteerTable;
    @FXML private TableColumn<VolunteerRow, String> colVolNo;
    @FXML private TableColumn<VolunteerRow, String> colVolName;
    @FXML private TableColumn<VolunteerRow, String> colVolActivity;
    @FXML private Label totalVolunteersLabel;

    private ObservableList<ActivityRow> activityList = FXCollections.observableArrayList();
    private ObservableList<VolunteerRow> volunteerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Test database connection first
        if (!Database.testConnection()) {
            showAlert("Database Error", "Cannot connect to database! Please check your database configuration.");
            return;
        }

        styleTableView(activityTable);
        styleTableView(volunteerTable);
        loadActivities();
        loadVolunteers();

        styleTableViewHeaders(activityTable);
        styleTableViewHeaders(volunteerTable);
    }

    private void styleTableView(TableView<?> table) {
        table.setStyle(
                "-fx-background-color: #BFDACC;" +
                        "-fx-control-inner-background: #BFDACC;" +
                        "-fx-table-cell-border-color: transparent transparent transparent #ACB7B7;" +
                        "-fx-table-header-border-color: #ACB7B7;" +
                        "-fx-selection-bar: #8FC1A9;" +
                        "-fx-selection-bar-non-focused: #A9D1C3;" +
                        "-fx-border-color: #ACB7B7;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );
    }

    private void styleTableViewHeaders(TableView<?> tableView) {
        Platform.runLater(() -> {
            // Background kolom judul
            for (Node header : tableView.lookupAll(".column-header")) {
                header.setStyle("-fx-background-color: #BFDACC;");
            }

            // Background baris header keseluruhan
            Node headerBg = tableView.lookup(".column-header-background");
            if (headerBg != null) {
                headerBg.setStyle("-fx-background-color: #BFDACC;");
            }
        });
    }

    private void loadActivities() {
        activityList.clear();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, title FROM activity ORDER BY id")) {

            int index = 1;
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                activityList.add(new ActivityRow(index++, id, title));
            }

            activityTable.setItems(activityList);

            // Setup columns
            colActNo.setCellValueFactory(data -> data.getValue().snProperty());
            colActTitle.setCellValueFactory(data -> data.getValue().titleProperty());

            // Center align SN column
            colActNo.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            });

            // Center align Title column
            colActTitle.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            });

            // Action Column Setup
            colActAction.setCellFactory(param -> new TableCell<ActivityRow, Void>() {
                private final Button editBtn = new Button("✏ Edit");
                private final Button delBtn = new Button("🗑 Delete");
                private final HBox hBox = new HBox(10, editBtn, delBtn);

                // Constructor block
                {
                    // Style buttons
                    editBtn.setStyle(
                            "-fx-background-color: #4CAF50; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-background-radius: 5; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-font-weight: bold;"
                    );
                    delBtn.setStyle(
                            "-fx-background-color: #f44336; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-background-radius: 5; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-font-weight: bold;"
                    );

                    hBox.setAlignment(javafx.geometry.Pos.CENTER);

                    // Button actions dengan proper error handling
                    editBtn.setOnAction(event -> {
                        try {
                            int rowIndex = getIndex();
                            if (rowIndex >= 0 && rowIndex < getTableView().getItems().size()) {
                                ActivityRow row = getTableView().getItems().get(rowIndex);
                                handleEditActivity(row);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showAlert("Error", "Failed to edit activity: " + e.getMessage());
                        }
                    });

                    delBtn.setOnAction(event -> {
                        try {
                            int rowIndex = getIndex();
                            if (rowIndex >= 0 && rowIndex < getTableView().getItems().size()) {
                                ActivityRow row = getTableView().getItems().get(rowIndex);
                                handleDeleteActivity(row);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showAlert("Error", "Failed to delete activity: " + e.getMessage());
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(hBox);
                        setAlignment(javafx.geometry.Pos.CENTER);
                    }
                }
            });

            totalActivitiesLabel.setText("Total: " + activityList.size() + " Activities");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load activities: " + e.getMessage());
        }
    }

    private void handleEditActivity(ActivityRow activity) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/edit-activity.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the activity data
            EditActivityController controller = loader.getController();
            controller.setActivityData(activity.getId());

            // Get current stage and set new scene
            Stage stage = (Stage) activityTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open edit form: " + e.getMessage());
        }
    }

    private void handleDeleteActivity(ActivityRow activity) {
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Activity");
        alert.setHeaderText("Delete Activity: " + activity.getTitle());
        alert.setContentText("Are you sure you want to delete this activity?\nThis will also remove all volunteers registered for this activity.\nThis action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteActivityFromDB(activity.getId());
            }
        });
    }

    private void deleteActivityFromDB(int id) {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // First delete volunteers associated with this activity
            try (PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM volunteer WHERE id_activity = ?")) {
                stmt1.setInt(1, id);
                stmt1.executeUpdate();
            }

            // Then delete the activity
            try (PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM activity WHERE id = ?")) {
                stmt2.setInt(1, id);
                int rowsAffected = stmt2.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit(); // Commit transaction
                    showAlert("Success", "Activity and associated volunteers deleted successfully!");
                    loadActivities(); // Reload activities table
                    loadVolunteers(); // Reload volunteers table
                } else {
                    conn.rollback(); // Rollback transaction
                    showAlert("Error", "Failed to delete activity. Activity not found.");
                }
            }

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            showAlert("Database Error", "Failed to delete activity: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadVolunteers() {
        volunteerList.clear();
        String query = """
            SELECT v.name, a.title 
            FROM volunteer v
            JOIN activity a ON v.id_activity = a.id
            ORDER BY v.name
        """;

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int index = 1;
            while (rs.next()) {
                String name = rs.getString("name");
                String activity = rs.getString("title");
                volunteerList.add(new VolunteerRow(index++, name, activity));
            }

            volunteerTable.setItems(volunteerList);
            colVolNo.setCellValueFactory(data -> data.getValue().snProperty());
            colVolName.setCellValueFactory(data -> data.getValue().nameProperty());
            colVolActivity.setCellValueFactory(data -> data.getValue().activityProperty());
            totalVolunteersLabel.setText("Total: " + volunteerList.size() + " Volunteers");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load volunteers: " + e.getMessage());
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
        System.out.println("Back button clicked");
        // Implementasi navigasi jika perlu
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        NavigationUtil.logout(event);
    }
}
