package com.example.uasvolunteerhub;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.example.uasvolunteerhub.Session;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void handleLogin() throws IOException {
        String email = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Login Failed", "Please enter both email and password.");
            return;
        }
        if (email.equals("admin@gmail.com") && password.equals("admin123")) {
            // arahkan langsung ke adminhome-view.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("adminhome-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
            stage.sizeToScene();
        } else {
            try (Connection conn = Database.getConnection()) {
                String query = "SELECT * FROM users WHERE email = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, password); // consider hashing for production
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // asumsi: user sudah pernah register
                    Session.isUserRegistered = true;

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUpDone-view.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Welcome");
                    stage.sizeToScene();

                } else {
                    showAlert(AlertType.ERROR, "Login Failed", "Invalid email or password.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Error", "Failed to access the database.");
            }
        }
    }

    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("forgotPassword-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Forgot Password");
            stage.sizeToScene();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Failed to load forgot password page.");
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
