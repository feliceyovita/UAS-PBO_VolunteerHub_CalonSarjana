package com.example.uasvolunteerhub;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class forgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private void handleSend() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showAlert("Error", "Kolom email harus diisi.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // arahkan ke halaman perbaruiPassword
                FXMLLoader loader = new FXMLLoader(getClass().getResource("changePassword-view.fxml"));
                Parent root = loader.load();

                // kirim data email ke controller berikutnya
                changePasswordController controller = loader.getController();
                controller.setEmail(email);

                Stage stage = new Stage();
                stage.setTitle("Perbarui Password");
                stage.setScene(new Scene(root));
                stage.show();

                // optional: tutup halaman sekarang
                ((Stage) emailField.getScene().getWindow()).close();
            } else {
                showAlert("Error", "Email tidak ditemukan di database.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Terjadi kesalahan saat mengakses database.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
