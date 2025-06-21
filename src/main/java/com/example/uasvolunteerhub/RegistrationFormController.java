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

public class RegistrationFormController extends FormController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField ageField;
    @FXML private TextField addressField;
    @FXML private TextArea reasonField;
    @FXML private ComboBox<String> jobComboBox;

    private int activityId;

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
        if (isEmpty(nameField) || isEmpty(emailField) || isEmpty(phoneField)
                || isEmpty(ageField) || isEmpty(addressField) || isEmpty(reasonField) || jobComboBox.getValue() == null) {
            showAlert("Validation Error", "Please fill all required fields.");
            return;
        }

        if (!isValidEmail(emailField.getText())) {
            showAlert("Validation Error", "Invalid email format.");
            return;
        }

        if (!isNumeric(ageField.getText())) {
            showAlert("Validation Error", "Age must be numeric.");
            return;
        }

        int age = Integer.parseInt(ageField.getText());
        String userEmail = Session.currentUserEmail;
        if (userEmail == null || userEmail.isEmpty()) {
            showAlert("Session Error", "User not logged in.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String getUserQuery = "SELECT id FROM users WHERE email = ?";
            PreparedStatement getUserStmt = conn.prepareStatement(getUserQuery);
            getUserStmt.setString(1, userEmail);
            ResultSet rs = getUserStmt.executeQuery();

            if (!rs.next()) {
                showAlert("User Error", "User not found.");
                return;
            }

            int userId = rs.getInt("id");

            String insertQuery = """
                INSERT INTO volunteer (id_user, id_activity, name, email, phone_number, job, age, address, reason_join)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, activityId);
            insertStmt.setString(3, nameField.getText());
            insertStmt.setString(4, emailField.getText());
            insertStmt.setString(5, phoneField.getText());
            insertStmt.setString(6, jobComboBox.getValue());
            insertStmt.setInt(7, age);
            insertStmt.setString(8, addressField.getText());
            insertStmt.setString(9, reasonField.getText());
            insertStmt.executeUpdate();

            Session.isUserRegistered = true;
            Session.justRegistered = true;

            showSuccess("You have successfully registered!");

            // redirect ke dashboard setelah OK
            redirectToDashboard();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to register: " + e.getMessage());
        }
    }

    private void redirectToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/Volunteer-dashboard-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard Volunteer");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Failed to load dashboard: " + e.getMessage());
        }
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
