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
    private void handleSave(ActionEvent event) {
        System.out.println("TOMBOL SAVE DIKLIK!");

        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        LocalDate dob = dobPicker.getValue();

        int userId = SessionManager.getUserId(); // ambil ID user aktif
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

            System.out.println("Data berhasil disimpan ke database.");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Lanjut ke halaman berikutnya
        try {
            Parent root = FXMLLoader.load(getClass().getResource("SignUpDone-view.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
