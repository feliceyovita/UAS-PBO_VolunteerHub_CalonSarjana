package com.example.uasvolunteerhub;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class FillProfileController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private void handleSave() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String dob = (dobPicker.getValue() != null) ? dobPicker.getValue().toString() : "";

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Data", "Please fill in all fields.");
        } else {
            // Simulasi penyimpanan
            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile saved successfully!");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
