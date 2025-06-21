package com.example.uasvolunteerhub;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AccountVolunteerController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private DatePicker dobPicker;

    @FXML
    private Text helloText;

    @FXML
    public void handleAccount(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/account-Volunteer.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 960, 540);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Account Page");
            stage.setWidth(960);
            stage.setHeight(540);
            stage.setMinWidth(960);
            stage.setMinHeight(540);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 960, 540);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.setWidth(960);
            stage.setHeight(540);
            stage.setMinWidth(960);
            stage.setMinHeight(540);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRecommendation(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/Volunteer-Dashboard-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 960, 540);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Volunteer Dashboard");
            stage.setWidth(960);
            stage.setHeight(540);
            stage.setMinWidth(960);
            stage.setMinHeight(540);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHistory(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/History.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 960, 540);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Activity History");
            stage.setWidth(960);
            stage.setHeight(540);
            stage.setMinWidth(960);
            stage.setMinHeight(540);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        int userId = SessionManager.getUserId();
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT name, email, phone_number, birth_date FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone_number"));

                if (rs.getDate("birth_date") != null) {
                    dobPicker.setValue(rs.getDate("birth_date").toLocalDate());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        int userId = SessionManager.getUserId();
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        LocalDate birthDate = dobPicker.getValue();

        try (Connection conn = Database.getConnection()) {
            String updateQuery = "UPDATE users SET name = ?, email = ?, phone_number = ?, birth_date = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setDate(4, birthDate != null ? java.sql.Date.valueOf(birthDate) : null);
            stmt.setInt(5, userId);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Profile updated successfully!");
                alert.showAndWait();

                if (helloText != null) {
                    helloText.setText("Hello, " + name);
                }
            } else {
                System.out.println("Update failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}