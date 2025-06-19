package com.example.uasvolunteerhub;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class RegistrationFormController {

    // Field sesuai FXML
    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField addressField;

    @FXML
    private TextArea reasonField;  // ✅ Diperbaiki ke TextArea (sesuai FXML)

    @FXML
    private ComboBox<String> jobComboBox;

    private int activityId;

    // Untuk menerima activity ID dari controller sebelumnya
    public void setActivityId(int activityId) {
        this.activityId = activityId;
        System.out.println("Registering for Activity ID: " + activityId);
    }

    @FXML
    private void initialize() {
        jobComboBox.getItems().addAll("Student", "Teacher", "Freelancer", "Unemployed", "Other");
    }

    @FXML
    private void handleSubmit() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String ageStr = ageField.getText();
        String address = addressField.getText();
        String reason = reasonField.getText();
        String job = jobComboBox.getValue();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || job == null || reason.isEmpty() || ageStr.isEmpty() || address.isEmpty()) {
            showAlert("Validation Error", "Please fill all required fields.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Age must be a number.");
            return;
        }

        String userEmail = Session.currentUserEmail;
        if (userEmail == null || userEmail.isEmpty()) {
            showAlert("Session Error", "User not logged in.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            // Ambil id_user berdasarkan email
            String getUserQuery = "SELECT id FROM users WHERE email = ?";
            PreparedStatement getUserStmt = conn.prepareStatement(getUserQuery);
            getUserStmt.setString(1, userEmail);
            ResultSet rs = getUserStmt.executeQuery();

            if (!rs.next()) {
                showAlert("User Error", "User email not found in database.");
                return;
            }

            int userId = rs.getInt("id");

            String insertQuery = "INSERT INTO volunteer (id_user, id_activity, name, email, phone_number, job, age, address, reason_join, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, activityId);
            insertStmt.setString(3, name);
            insertStmt.setString(4, email);
            insertStmt.setString(5, phone);
            insertStmt.setString(6, job);
            insertStmt.setInt(7, age);
            insertStmt.setString(8, address);
            insertStmt.setString(9, reason);
            insertStmt.setString(10, "volunteer");
            insertStmt.executeUpdate();


            // Tandai user baru saja mendaftar
            Session.isUserRegistered = true;
            Session.justRegistered = true;

            showAlert("Success", "You have successfully registered!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to register: " + e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/Volunteer-dashboard-view.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) nameField.getScene().getWindow(); // atau node apapun yang ada di scene saat ini
                    stage.setScene(new Scene(root));
                    stage.setTitle("Dashboard Volunteer");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Navigation Error", "Failed to load dashboard: " + e.getMessage());
                }
            }
        });
    }
    @FXML
    public void handleAccount(ActionEvent event) {
        NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/account-Volunteer.fxml", "Profile Account");
    }

    @FXML
    private void handleRecommendation(ActionEvent event) {
        NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/Volunteer-dashboard-view.fxml", "Volunteer Recommendation");
    }

    @FXML
    private void handleHistory(ActionEvent event) {
        NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/History.fxml", "Activity History");
    }
}
