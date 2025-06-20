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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class adminHomeController {

    // Existing FXML components
    @FXML private Button addActivityButton;

    // Dashboard components
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

    // Add Activity form components
    @FXML private TextField titleField;
    @FXML private TextField locationField;
    @FXML private DatePicker datePicker;
    @FXML private TextField contactField;
    @FXML private TextField benefitsField;
    @FXML private TextField slotField;
    @FXML private TextArea descriptionArea;
    @FXML private RadioButton donateRadio;
    @FXML private RadioButton notDonateRadio;
    @FXML private ToggleGroup volunteerTypeGroup;
    @FXML private TextField donationAmountField; // Field untuk nominal donasi
    @FXML private ImageView uploadImageView;
    @FXML private Button browseButton;

    // Variables
    private ObservableList<ActivityRow> activityList = FXCollections.observableArrayList();
    private ObservableList<VolunteerRow> volunteerList = FXCollections.observableArrayList();
    private File selectedImageFile;

    @FXML
    public void initialize() {
        // Test database connection first
        if (!Database.testConnection()) {
            showAlert("Database Error", "Cannot connect to database! Please check your database configuration.");
            return;
        }

        // Only initialize tables if they exist (dashboard view)
        if (activityTable != null && volunteerTable != null) {
            styleTableView(activityTable);
            styleTableView(volunteerTable);
            loadActivities();
            loadVolunteers();
            styleTableViewHeaders(activityTable);
            styleTableViewHeaders(volunteerTable);
        }

        // Setup donation field toggle (jika form add activity ada)
        if (donateRadio != null && notDonateRadio != null && donationAmountField != null) {
            setupDonationToggle();
        }
    }

    // NEW METHOD: Setup Donation Toggle
    private void setupDonationToggle() {
        // Listener untuk mengaktifkan/menonaktifkan donation field
        volunteerTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (donateRadio.isSelected()) {
                donationAmountField.setDisable(false);
                donationAmountField.setStyle("-fx-border-color: #2E5A3E; -fx-border-radius: 3px; -fx-font-size: 11px;");
            } else {
                donationAmountField.setDisable(true);
                donationAmountField.clear();
                donationAmountField.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 3px; -fx-font-size: 11px;");
            }
        });

        // Validasi input donasi - hanya angka
        donationAmountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                donationAmountField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Set initial state
        donationAmountField.setDisable(!donateRadio.isSelected());
    }

    // NEW METHOD: Handle Browse Image
    @FXML
    private void handleBrowseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Activity Image");

        // Set extension filters
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);

        // Show open file dialog
        Stage stage = (Stage) browseButton.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            try {
                // Display selected image in ImageView (optional preview)
                Image image = new Image(selectedImageFile.toURI().toString());
                uploadImageView.setImage(image);
                uploadImageView.setOpacity(1.0); // Make it fully visible

                showAlert("Success", "Image selected: " + selectedImageFile.getName());
            } catch (Exception e) {
                showAlert("Error", "Failed to load image: " + e.getMessage());
                selectedImageFile = null;
            }
        }
    }

    // UPDATED METHOD: Handle Submit Form (dengan donasi)
    @FXML
    private void handleSubmit(ActionEvent event) {
        // Validate form inputs
        if (!validateForm()) {
            return;
        }

        try {
            // Get form data
            String title = titleField.getText().trim();
            String location = locationField.getText().trim();
            LocalDate date = datePicker.getValue();
            String contact = contactField.getText().trim();
            String benefits = benefitsField.getText().trim();
            int slot = Integer.parseInt(slotField.getText().trim());
            String description = descriptionArea.getText().trim();

            // Get volunteer type
            String volunteerType = "";
            double donationAmount = 0.0;

            if (donateRadio.isSelected()) {
                volunteerType = "Donate";
                // Get donation amount jika donate dipilih
                if (!donationAmountField.getText().trim().isEmpty()) {
                    donationAmount = Double.parseDouble(donationAmountField.getText().trim());
                }
            } else if (notDonateRadio.isSelected()) {
                volunteerType = "Not Donate";
                donationAmount = 0.0;
            }

            // Save to database
            boolean success = saveActivityToDatabase(title, location, date, contact,
                    benefits, slot, description, volunteerType, donationAmount);

            if (success) {
                showAlert("Success", "Activity has been saved successfully!");
                clearForm();

                // Refresh tables if we're on dashboard
                if (activityTable != null) {
                    loadActivities();
                }
            }

        } catch (NumberFormatException e) {
            showAlert("Input Error", "Slot and donation amount must be valid numbers!");
        } catch (Exception e) {
            showAlert("Error", "Failed to save activity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // UPDATED METHOD: Validate Form (dengan validasi donasi)
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (titleField.getText().trim().isEmpty()) {
            errors.append("- Title is required\n");
        }
        if (locationField.getText().trim().isEmpty()) {
            errors.append("- Location is required\n");
        }
        if (datePicker.getValue() == null) {
            errors.append("- Date is required\n");
        }
        if (contactField.getText().trim().isEmpty()) {
            errors.append("- Contact is required\n");
        }
        if (benefitsField.getText().trim().isEmpty()) {
            errors.append("- Benefits is required\n");
        }
        if (slotField.getText().trim().isEmpty()) {
            errors.append("- Slot is required\n");
        } else {
            try {
                int slot = Integer.parseInt(slotField.getText().trim());
                if (slot <= 0) {
                    errors.append("- Slot must be greater than 0\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Slot must be a valid number\n");
            }
        }
        if (descriptionArea.getText().trim().isEmpty()) {
            errors.append("- Description is required\n");
        }
        if (volunteerTypeGroup.getSelectedToggle() == null) {
            errors.append("- Please select volunteer type\n");
        }

        // Validasi donasi
        if (donateRadio != null && donateRadio.isSelected()) {
            String donationText = donationAmountField.getText().trim();
            if (donationText.isEmpty()) {
                errors.append("- Donation amount is required when 'Donate' is selected\n");
            } else {
                try {
                    double amount = Double.parseDouble(donationText);
                    if (amount <= 0) {
                        errors.append("- Donation amount must be greater than 0\n");
                    }
                } catch (NumberFormatException e) {
                    errors.append("- Donation amount must be a valid number\n");
                }
            }
        }

        if (errors.length() > 0) {
            showAlert("Validation Error", "Please fix the following errors:\n\n" + errors.toString());
            return false;
        }

        return true;
    }

    // UPDATED METHOD: Save Activity to Database (dengan donasi)
    private boolean saveActivityToDatabase(String title, String location, LocalDate date,
                                           String contact, String benefits, int slot,
                                           String description, String volunteerType, double donationAmount) {

        String sql = """
            INSERT INTO activity (title, location, date, contact, benefits, slot, description, type_of_volunteer, donation_amount, image)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, location);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setString(4, contact);
            stmt.setString(5, benefits);
            stmt.setInt(6, slot);
            stmt.setString(7, description);
            stmt.setString(8, volunteerType);
            stmt.setDouble(9, donationAmount); // Set donation amount

            // Handle image path
            String imagePath = null;
            if (selectedImageFile != null) {
                // You might want to copy the file to a specific directory
                // For now, just store the original path
                imagePath = selectedImageFile.getAbsolutePath();
            }
            stmt.setString(10, imagePath);

            int rowsAffected = stmt.executeUpdate();

            // Debug print
            System.out.println("Activity saved with donation amount: " + donationAmount);

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to save activity to database: " + e.getMessage());
            return false;
        }
    }

    // UPDATED METHOD: Clear Form (dengan clear donation field)
    private void clearForm() {
        titleField.clear();
        locationField.clear();
        datePicker.setValue(null);
        contactField.clear();
        benefitsField.clear();
        slotField.clear();
        descriptionArea.clear();
        volunteerTypeGroup.selectToggle(null);
        if (donationAmountField != null) {
            donationAmountField.clear();
        }
        uploadImageView.setImage(new Image(getClass().getResourceAsStream("/icon/uploadImage.png")));
        uploadImageView.setOpacity(0.5);
        selectedImageFile = null;
    }

    // EXISTING METHODS (keep all your existing methods)
    private void navigateToPage(String fxmlPath, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleAdminDashboard(ActionEvent event) {
        try {
            navigateToPage("/com/example/uasvolunteerhub/adminHome-view.fxml", event);
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to open Dashboard Admin page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddActivity(ActionEvent event) {
        try {
            navigateToPage("/com/example/uasvolunteerhub/addActivity-view.fxml", event);
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to open Add Activity page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleManageActivities(ActionEvent event) {
        try {
            navigateToPage("/com/example/uasvolunteerhub/manageActivity-view.fxml", event);
        } catch (IOException e) {
            showAlert("Navigation Error", "Failed to open Manage Activity page: " + e.getMessage());
            e.printStackTrace();
        }
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
            for (Node header : tableView.lookupAll(".column-header")) {
                header.setStyle("-fx-background-color: #BFDACC;");
            }
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
            colActNo.setCellValueFactory(data -> data.getValue().snProperty());
            colActTitle.setCellValueFactory(data -> data.getValue().titleProperty());

            colActNo.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            });

            colActTitle.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            });

            colActAction.setCellFactory(param -> new TableCell<ActivityRow, Void>() {
                private final Button editBtn = new Button("✏ Edit");
                private final Button delBtn = new Button("🗑 Delete");
                private final HBox hBox = new HBox(10, editBtn, delBtn);

                {
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
            EditActivityController controller = loader.getController();
            controller.setActivityData(activity.getId());
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
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM volunteer WHERE id_activity = ?")) {
                stmt1.setInt(1, id);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM activity WHERE id = ?")) {
                stmt2.setInt(1, id);
                int rowsAffected = stmt2.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    showAlert("Success", "Activity and associated volunteers deleted successfully!");
                    loadActivities();
                    loadVolunteers();
                } else {
                    conn.rollback();
                    showAlert("Error", "Failed to delete activity. Activity not found.");
                }
            }

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            showAlert("Database Error", "Failed to delete activity: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
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
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        NavigationUtil.logout(event);
    }
}