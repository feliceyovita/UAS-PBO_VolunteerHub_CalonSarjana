package com.example.uasvolunteerhub;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class addActivityController implements Initializable {
    @FXML
    private Button backButton;
    @FXML
    private Button manageActivitiesButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button browseButton;
    @FXML
    private Button submitButton;

    @FXML
    private TextField titleField;
    @FXML
    private TextField locationField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField contactField;
    @FXML
    private TextField benefitsField;
    @FXML
    private TextField slotField;
    @FXML
    private TextArea descriptionArea;

    @FXML
    private RadioButton donateRadio;
    @FXML
    private RadioButton notDonateRadio;
    @FXML
    private ToggleGroup volunteerTypeGroup;
    @FXML
    private TextField donationAmountField;

    @FXML
    private ImageView uploadImageView;

    private File selectedImageFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set default values
        datePicker.setValue(LocalDate.now());
        donateRadio.setSelected(true); // Default selection

        // Add some styling or validation if needed
        setupValidation();
        setupDonationToggle();
    }

    private void setupValidation() {
        // Add input validation listeners if needed
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                titleField.setText(oldValue);
            }
        });

        slotField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                slotField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Validasi input donasi - hanya angka
        donationAmountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                donationAmountField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

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

        // Set initial state
        donationAmountField.setDisable(!donateRadio.isSelected());
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            navigateToPage("/com/example/uasvolunteerhub/adminHome.fxml", event);
        } catch (IOException e) {
            showAlert("Error", "Failed to navigate to Dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleManageActivities(ActionEvent event) {
        try {
            // Navigate to Manage Activities page (you'll need to create this FXML)
            navigateToPage("/com/example/uasvolunteerhub/manageActivities.fxml", event);
        } catch (IOException e) {
            showAlert("Error", "Failed to navigate to Manage Activities: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Navigate back to login page
            navigateToPage("/com/example/uasvolunteerhub/login.fxml", event);
        } catch (IOException e) {
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }

    @FXML
    private void handleBrowseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Activity Image");

        // Set extension filters
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);

        Stage stage = (Stage) browseButton.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            // You can display the selected image name or preview
            browseButton.setText("Selected: " + selectedImageFile.getName());
        }
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        if (validateForm()) {
            // Create Activity object and save to database
            Activity newActivity = createActivityFromForm();

            if (saveActivity(newActivity)) {
                showAlert("Success", "Activity added successfully!");
                clearForm();
            } else {
                showAlert("Error", "Failed to save activity. Please try again.");
            }
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (titleField.getText().trim().isEmpty()) {
            errors.append("Title is required.\n");
        }

        if (locationField.getText().trim().isEmpty()) {
            errors.append("Location is required.\n");
        }

        if (datePicker.getValue() == null) {
            errors.append("Date is required.\n");
        } else if (datePicker.getValue().isBefore(LocalDate.now())) {
            errors.append("Date cannot be in the past.\n");
        }

        if (contactField.getText().trim().isEmpty()) {
            errors.append("Contact is required.\n");
        }

        if (slotField.getText().trim().isEmpty()) {
            errors.append("Slot is required.\n");
        } else {
            try {
                int slot = Integer.parseInt(slotField.getText().trim());
                if (slot <= 0) {
                    errors.append("Slot must be a positive number.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("Slot must be a valid number.\n");
            }
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            errors.append("Description is required.\n");
        }

        // Validasi donasi
        if (donateRadio.isSelected()) {
            String donationText = donationAmountField.getText().trim();
            if (donationText.isEmpty()) {
                errors.append("Donation amount is required when 'Donate' is selected.\n");
            } else {
                try {
                    double amount = Double.parseDouble(donationText);
                    if (amount <= 0) {
                        errors.append("Donation amount must be greater than 0.\n");
                    }
                } catch (NumberFormatException e) {
                    errors.append("Donation amount must be a valid number.\n");
                }
            }
        }

        if (errors.length() > 0) {
            showAlert("Validation Error", errors.toString());
            return false;
        }

        return true;
    }

    private Activity createActivityFromForm() {
        Activity activity = new Activity();
        activity.setTitle(titleField.getText().trim());
        activity.setLocation(locationField.getText().trim());
        activity.setDate(java.sql.Date.valueOf(datePicker.getValue()));
        activity.setContact(contactField.getText().trim());
        activity.setBenefits(benefitsField.getText().trim());
        activity.setSlot(Integer.parseInt(slotField.getText().trim()));
        activity.setDescription(descriptionArea.getText().trim());
        activity.setTypeOfVolunteer(donateRadio.isSelected() ? "Donate" : "Not Donate");

        // Set donation amount
        if (donateRadio.isSelected() && !donationAmountField.getText().trim().isEmpty()) {
            try {
                double donationAmount = Double.parseDouble(donationAmountField.getText().trim());
                activity.setDonationAmount(donationAmount);
            } catch (NumberFormatException e) {
                activity.setDonationAmount(0.0);
            }
        } else {
            activity.setDonationAmount(0.0);
        }

        if (selectedImageFile != null) {
            activity.setImage(selectedImageFile.getAbsolutePath());
        }

        return activity;
    }

    private boolean saveActivity(Activity activity) {
        try {

            // Print untuk debugging
            System.out.println("Saving Activity:");
            System.out.println("Title: " + activity.getTitle());
            System.out.println("Type: " + activity.getTypeOfVolunteer());
            System.out.println("Donation Amount: " + activity.getDonationAmount());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    private void clearForm() {
        titleField.clear();
        locationField.clear();
        datePicker.setValue(LocalDate.now());
        contactField.clear();
        benefitsField.clear();
        slotField.clear();
        descriptionArea.clear();
        donateRadio.setSelected(true);
        donationAmountField.clear();
        selectedImageFile = null;
        browseButton.setText("Browse");
    }

    private void navigateToPage(String fxmlPath, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}