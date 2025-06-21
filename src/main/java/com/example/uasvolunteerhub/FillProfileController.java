package com.example.uasvolunteerhub;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class FillProfileController extends FormController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private DatePicker dobPicker;

    @FXML
    private void handleSave(ActionEvent event) {
        if (isEmpty(nameField) || isEmpty(emailField) || isEmpty(phoneField)) {
            showAlert("Input Error", "Please fill in all fields.");
            return;
        }

        if (!isValidEmail(emailField.getText())) {
            showAlert("Input Error", "Please enter a valid email address.");
            return;
        }

        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        LocalDate dob = dobPicker.getValue();

        int userId = SessionManager.getUserId(); // Ambil ID user aktif

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE users SET name = ?, email = ?, phone_number = ?, birth_date = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);

            if (dob != null) {
                stmt.setDate(4, java.sql.Date.valueOf(dob));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }

            stmt.setInt(5, userId);
            stmt.executeUpdate();

            showSuccess("Profile successfully saved!");

            // Redirect ke halaman selesai signup
            Parent root = FXMLLoader.load(getClass().getResource("SignUpDone-view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to save profile: " + e.getMessage());
        }
    }
}
