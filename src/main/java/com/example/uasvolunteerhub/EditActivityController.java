package com.example.uasvolunteerhub;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class EditActivityController {

    @FXML private TextField titleField;
    @FXML private DatePicker datePicker;
    @FXML private TextField locationField;
    @FXML private TextField contactField;
    @FXML private TextField benefitsField;
    @FXML private TextField slotField;
    @FXML private RadioButton donateRadio;
    @FXML private RadioButton notDonateRadio;
    @FXML private ToggleGroup volunteerTypeGroup;
    @FXML private VBox donationBox;
    @FXML private TextField donationField;
    @FXML private TextArea descriptionArea;
    @FXML private Label dragLabel;
    @FXML private Button browseButton;
    @FXML private Button doneButton;
    @FXML private Button backButton;

    private int activityId;
    private File selectedImageFile;
    private boolean hasExistingImage = false;

    public void initialize() {
        // Initialize toggle group first
        if (volunteerTypeGroup == null) {
            volunteerTypeGroup = new ToggleGroup();
        }

        // Set toggle group for radio buttons
        if (donateRadio != null) {
            donateRadio.setToggleGroup(volunteerTypeGroup);
        }
        if (notDonateRadio != null) {
            notDonateRadio.setToggleGroup(volunteerTypeGroup);
            notDonateRadio.setSelected(true);
        }

        // Add number validation to slot field (with null check)
        if (slotField != null) {
            slotField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    slotField.setText(newVal.replaceAll("[^\\d]", ""));
                }
            });
        }

        // Add number validation to donation field (with null check)
        if (donationField != null) {
            donationField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                    donationField.setText(oldVal);
                }
            });
        }

        // Initialize donation box visibility
        if (donationBox != null) {
            donationBox.setVisible(false);
            donationBox.setManaged(false);
        }
    }

    public void setActivityData(int activityId) {
        this.activityId = activityId;
        loadActivityData();
    }

    private void loadActivityData() {
        String query = "SELECT * FROM activity WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, activityId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                if (titleField != null) titleField.setText(rs.getString("title"));

                Date date = rs.getDate("date");
                if (date != null && datePicker != null) {
                    datePicker.setValue(date.toLocalDate());
                }

                if (locationField != null) locationField.setText(rs.getString("location"));
                if (contactField != null) contactField.setText(rs.getString("contact"));
                if (benefitsField != null) benefitsField.setText(rs.getString("benefits"));

                int slot = rs.getInt("slot");
                if (!rs.wasNull() && slotField != null) {
                    slotField.setText(String.valueOf(slot));
                }

                String type = rs.getString("type_of_volunteer");
                if ("donate".equals(type)) {
                    if (donateRadio != null) donateRadio.setSelected(true);
                    handleTypeChange();
                } else {
                    if (notDonateRadio != null) notDonateRadio.setSelected(true);
                    handleTypeChange();
                }

                BigDecimal donationAmount = rs.getBigDecimal("donation_amount");
                if (donationAmount != null && donationField != null) {
                    donationField.setText(donationAmount.toString());
                }

                if (descriptionArea != null) descriptionArea.setText(rs.getString("description"));

                // Check if image exists
                Blob imageBlob = rs.getBlob("image");
                if (imageBlob != null && dragLabel != null) {
                    hasExistingImage = true;
                    dragLabel.setText("Current image available");
                    dragLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #4CAF50;");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load activity data: " + e.getMessage());
        }
    }

    @FXML
    private void handleTypeChange() {
        if (donationBox != null) {
            if (donateRadio != null && donateRadio.isSelected()) {
                // Show donation box
                donationBox.setVisible(true);
                donationBox.setManaged(true);
            } else {
                // Hide donation box
                donationBox.setVisible(false);
                donationBox.setManaged(false);
                if (donationField != null) {
                    donationField.clear();
                }
            }
        }
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Activity Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        Stage stage = (Stage) browseButton.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null && dragLabel != null) {
            dragLabel.setText("Selected: " + selectedImageFile.getName());
            dragLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #4CAF50;");
        }
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        String updateQuery = """
            UPDATE activity SET 
                title = ?, 
                date = ?, 
                benefits = ?, 
                location = ?, 
                contact = ?, 
                slot = ?, 
                description = ?, 
                type_of_volunteer = ?, 
                donation_amount = ?
                """ + (selectedImageFile != null ? ", image = ?" : "") + " WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            int paramIndex = 1;

            stmt.setString(paramIndex++, titleField.getText().trim());
            stmt.setDate(paramIndex++, Date.valueOf(datePicker.getValue()));
            stmt.setString(paramIndex++, benefitsField.getText().trim().isEmpty() ? null : benefitsField.getText().trim());
            stmt.setString(paramIndex++, locationField.getText().trim().isEmpty() ? null : locationField.getText().trim());
            stmt.setString(paramIndex++, contactField.getText().trim().isEmpty() ? null : contactField.getText().trim());

            if (slotField.getText().trim().isEmpty()) {
                stmt.setNull(paramIndex++, Types.INTEGER);
            } else {
                stmt.setInt(paramIndex++, Integer.parseInt(slotField.getText().trim()));
            }

            stmt.setString(paramIndex++, descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim());

            String volunteerType = (donateRadio != null && donateRadio.isSelected()) ? "donate" : "not donate";
            stmt.setString(paramIndex++, volunteerType);

            if (donateRadio != null && donateRadio.isSelected() && donationField != null && !donationField.getText().trim().isEmpty()) {
                stmt.setBigDecimal(paramIndex++, new BigDecimal(donationField.getText().trim()));
            } else {
                stmt.setNull(paramIndex++, Types.DECIMAL);
            }

            // Handle image upload if new image is selected
            if (selectedImageFile != null) {
                try (FileInputStream fis = new FileInputStream(selectedImageFile)) {
                    stmt.setBinaryStream(paramIndex++, fis, (int) selectedImageFile.length());
                }
            }

            stmt.setInt(paramIndex, activityId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Activity updated successfully!");
                handleBack();
            } else {
                showAlert("Error", "Failed to update activity. Please try again.");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update activity: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (titleField == null || titleField.getText().trim().isEmpty()) {
            errors.append("• Title is required\n");
        }

        if (datePicker == null || datePicker.getValue() == null) {
            errors.append("• Date is required\n");
        } else if (datePicker.getValue().isBefore(LocalDate.now())) {
            errors.append("• Date cannot be in the past\n");
        }

        if (slotField != null && !slotField.getText().trim().isEmpty()) {
            try {
                int slot = Integer.parseInt(slotField.getText().trim());
                if (slot <= 0) {
                    errors.append("• Slot must be a positive number\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Slot must be a valid number\n");
            }
        }

        if (donateRadio != null && donateRadio.isSelected() && donationField != null && !donationField.getText().trim().isEmpty()) {
            try {
                BigDecimal amount = new BigDecimal(donationField.getText().trim());
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    errors.append("• Donation amount must be greater than 0\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Donation amount must be a valid number\n");
            }
        }

        if (errors.length() > 0) {
            showAlert("Validation Error", "Please fix the following errors:\n\n" + errors.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void handleBack() {
        try {
            // Debug: Print current resource path
            System.out.println("Attempting to load adminHome-view.fxml...");

            // Try different resource paths for adminHome-view.fxml
            String[] possiblePaths = {
                    "/com/example/uasvolunteerhub/adminHome-view.fxml",
                    "adminHome-view.fxml",
                    "/adminHome-view.fxml"
            };

            FXMLLoader loader = null;
            for (String path : possiblePaths) {
                try {
                    System.out.println("Trying path: " + path);
                    loader = new FXMLLoader(getClass().getResource(path));
                    if (loader.getLocation() != null) {
                        System.out.println("Found resource at: " + path);
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Failed to find resource at: " + path);
                    continue;
                }
            }

            if (loader == null || loader.getLocation() == null) {
                System.err.println("Could not find adminHome-view.fxml in any expected location");
                showAlert("Navigation Error", "Could not find admin home page. Please check the FXML file location.");
                return;
            }

            Parent root = loader.load();

            // Get current stage
            Stage stage = null;
            if (backButton != null && backButton.getScene() != null) {
                stage = (Stage) backButton.getScene().getWindow();
            } else if (doneButton != null && doneButton.getScene() != null) {
                stage = (Stage) doneButton.getScene().getWindow();
            } else if (titleField != null && titleField.getScene() != null) {
                stage = (Stage) titleField.getScene().getWindow();
            }

            if (stage != null) {
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                System.out.println("Successfully navigated back to admin home");
            } else {
                showAlert("Navigation Error", "Could not get current window reference");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException during navigation: " + e.getMessage());
            showAlert("Error", "Failed to navigate back: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error during navigation: " + e.getMessage());
            showAlert("Error", "Unexpected error during navigation: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}