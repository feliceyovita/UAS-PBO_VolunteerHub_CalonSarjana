package com.example.uasvolunteerhub;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class changePasswordController {

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    @FXML
    private void handleChangePassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Semua kolom harus diisi.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "Password tidak cocok.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE users SET password = ? WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newPassword); // bisa dienkripsi jika perlu
            stmt.setString(2, email);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Sukses", "Password berhasil diperbarui.");
                ((Stage) newPasswordField.getScene().getWindow()).close();
            } else {
                showAlert("Error", "Gagal memperbarui password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Terjadi kesalahan saat memperbarui password.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
