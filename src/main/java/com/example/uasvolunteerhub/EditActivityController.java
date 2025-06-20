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
        if (donateRadio != null && donateRadio.isSelected()) {
            donationField.setDisable(false);
            donationField.setPromptText("Enter amount (Rp)");
        } else {
            donationField.setDisable(true);
            donationField.clear();
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
        // DEBUG: Print method call
        System.out.println("handleSave() called - Activity ID: " + activityId);

        if (!validateForm()) {
            System.out.println("Form validation failed");
            return;
        }

        // FIX 1: Pastikan activityId valid
        if (activityId <= 0) {
            showAlert("Error", "Invalid activity ID. Cannot update activity.");
            System.out.println("Invalid activityId: " + activityId);
            return;
        }

        // FIX 2: Perbaiki query dengan proper formatting
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE activity SET ");
        queryBuilder.append("title = ?, ");
        queryBuilder.append("date = ?, ");
        queryBuilder.append("benefits = ?, ");
        queryBuilder.append("location = ?, ");
        queryBuilder.append("contact = ?, ");
        queryBuilder.append("slot = ?, ");
        queryBuilder.append("description = ?, ");
        queryBuilder.append("type_of_volunteer = ?, ");
        queryBuilder.append("donation_amount = ?");

        if (selectedImageFile != null) {
            queryBuilder.append(", image = ?");
        }

        queryBuilder.append(" WHERE id = ?");

        String updateQuery = queryBuilder.toString();
        System.out.println("Update Query: " + updateQuery);

        Connection conn = null;
        PreparedStatement stmt = null;
        FileInputStream fis = null;

        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement(updateQuery);
            int paramIndex = 1;

            String title = (titleField != null) ? titleField.getText().trim() : "";
            stmt.setString(paramIndex++, title);
            System.out.println("Title: " + title);

            LocalDate selectedDate = (datePicker != null) ? datePicker.getValue() : null;
            if (selectedDate != null) {
                stmt.setDate(paramIndex++, Date.valueOf(selectedDate));
                System.out.println("Date: " + selectedDate);
            } else {
                stmt.setNull(paramIndex++, Types.DATE);
            }

            String benefits = (benefitsField != null && !benefitsField.getText().trim().isEmpty())
                    ? benefitsField.getText().trim() : null;
            stmt.setString(paramIndex++, benefits);
            System.out.println("Benefits: " + benefits);

            String location = (locationField != null && !locationField.getText().trim().isEmpty())
                    ? locationField.getText().trim() : null;
            stmt.setString(paramIndex++, location);
            System.out.println("Location: " + location);

            String contact = (contactField != null && !contactField.getText().trim().isEmpty())
                    ? contactField.getText().trim() : null;
            stmt.setString(paramIndex++, contact);
            System.out.println("Contact: " + contact);

            // Handle slot field
            if (slotField != null && !slotField.getText().trim().isEmpty()) {
                try {
                    int slot = Integer.parseInt(slotField.getText().trim());
                    stmt.setInt(paramIndex++, slot);
                    System.out.println("Slot: " + slot);
                } catch (NumberFormatException e) {
                    stmt.setNull(paramIndex++, Types.INTEGER);
                    System.out.println("Slot: null (invalid number)");
                }
            } else {
                stmt.setNull(paramIndex++, Types.INTEGER);
                System.out.println("Slot: null (empty)");
            }

            String description = (descriptionArea != null && !descriptionArea.getText().trim().isEmpty())
                    ? descriptionArea.getText().trim() : null;
            stmt.setString(paramIndex++, description);
            System.out.println("Description: " + description);

            // FIX 5: Perbaiki logic volunteer type
            String volunteerType = "not donate"; // default
            if (donateRadio != null && donateRadio.isSelected()) {
                volunteerType = "donate";
            }
            stmt.setString(paramIndex++, volunteerType);
            System.out.println("Volunteer Type: " + volunteerType);

            // Handle donation amount
            if ("donate".equals(volunteerType) && donationField != null && !donationField.getText().trim().isEmpty()) {
                try {
                    BigDecimal donationAmount = new BigDecimal(donationField.getText().trim());
                    stmt.setBigDecimal(paramIndex++, donationAmount);
                    System.out.println("Donation Amount: " + donationAmount);
                } catch (NumberFormatException e) {
                    stmt.setNull(paramIndex++, Types.DECIMAL);
                    System.out.println("Donation Amount: null (invalid number)");
                }
            } else {
                stmt.setNull(paramIndex++, Types.DECIMAL);
                System.out.println("Donation Amount: null");
            }

            // Handle image upload
            if (selectedImageFile != null) {
                fis = new FileInputStream(selectedImageFile);
                stmt.setBinaryStream(paramIndex++, fis, (int) selectedImageFile.length());
                System.out.println("Image: " + selectedImageFile.getName());
            }

            // Set WHERE clause parameter
            stmt.setInt(paramIndex, activityId);
            System.out.println("WHERE id = " + activityId);

            // FIX 6: Execute update dengan proper error handling
            System.out.println("Executing update...");
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            if (rowsAffected > 0) {
                // FIX 7: Commit transaction
                conn.commit();
                System.out.println("Transaction committed successfully");
                showAlert("Success", "Activity updated successfully!");
                handleBack();
            } else {
                conn.rollback();
                System.out.println("No rows affected - rolling back");
                showAlert("Error", "No activity found with ID: " + activityId);
            }

        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // FIX 8: Proper resource cleanup
            try {
                if (fis != null) {
                    fis.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                }
                System.out.println("Resources cleaned up");
            } catch (SQLException | IOException e) {
                System.err.println("Error during resource cleanup: " + e.getMessage());
            }
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