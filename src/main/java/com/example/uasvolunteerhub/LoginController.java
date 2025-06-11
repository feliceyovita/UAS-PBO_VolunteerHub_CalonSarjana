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

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if ("admin".equals(username) && "admin123".equals(password)) {
            try {
                // Navigasi ke fill-profile.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fill-profile.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Fill In Profile");
                stage.sizeToScene();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Error", "Failed to load profile page.");
            }
        } else {
            showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password.");
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
