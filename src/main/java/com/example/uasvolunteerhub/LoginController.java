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

            try (Connection conn = Database.getConnection()) {
                String query = "SELECT * FROM users WHERE email = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String name = rs.getString("name");
                    SessionManager.setCurrentUser(userId, name);
                    Session.currentUserId = userId;

                    System.out.println("User berhasil login dengan ID: " + userId);

                    String role = rs.getString("role");
                    Session.currentUserEmail = email;

                    Stage stage = (Stage) usernameField.getScene().getWindow();

                    if (role.equalsIgnoreCase("admin")) {
                        Session.loggedInDashboard = new AdminDashboard();
                        Session.loggedInDashboard.loadDashboard(stage);
                    } else {
                        Session.loggedInDashboard = new VolunteerDashboard();

                        if (Session.justRegistered) {
                            Session.justRegistered = false; // reset flag
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("fill-profile.fxml"));
                            Parent root = loader.load();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Complete Your Profile");
                            stage.show();
                        } else {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("SignUpDone-view.fxml"));
                            Parent root = loader.load();
                            stage.setScene(new Scene(root));
                            stage.setTitle("Welcome Back!");
                            stage.show();
                        }
                    }
                } else {
                    showAlert(AlertType.ERROR, "Login Failed", "Invalid email or password.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Error", "Failed to access the database.");
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

    // Tambahkan method ini di paling bawah class LoginController
    // Gunanya: Memberi label ID biar Robot TestFX bisa mengetik di sini.
    @FXML
    public void initialize() {
        if (usernameField != null) usernameField.setId("usernameField");
        if (passwordField != null) passwordField.setId("passwordField");

        // PENTING: Robot butuh ID tombol login juga.
        // Karena kamu belum punya variabel Button di codingan ini,
        // nanti robot akan kita suruh klik berdasarkan TEKS tombolnya saja.
    }
}
