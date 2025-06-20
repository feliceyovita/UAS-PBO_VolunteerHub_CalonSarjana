package com.example.uasvolunteerhub;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
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

public class DonationController {
    @FXML
    private void handleLogout(ActionEvent event) {
        NavigationUtil.logout(event);
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

    private int activityId;

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    @FXML
    private void handleSubmitDonation(ActionEvent event) {
        try (Connection conn = Database.getConnection()) {
            int userId = Session.currentUserId;

            String titleQuery = "SELECT title FROM activity WHERE id = ?";
            PreparedStatement titleStmt = conn.prepareStatement(titleQuery);
            titleStmt.setInt(1, activityId);
            ResultSet rs = titleStmt.executeQuery();

            String donationActivityTitle = "Donation contribution";
            if (rs.next()) {
                donationActivityTitle = rs.getString("title") + " (donation)";
            }

            String insertQuery = """
                INSERT INTO volunteer (id_user, id_activity, name, email, phone_number, job, age, address, reason_join)
                VALUES (?, ?, 'Donatur', '-', '-', '-', 0, '-', ?)
            """;
            PreparedStatement stmt = conn.prepareStatement(insertQuery);
            stmt.setInt(1, userId);
            stmt.setInt(2, activityId);
            stmt.setString(3, donationActivityTitle);
            stmt.executeUpdate();

            showSuccessAlert(event);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to record donation: " + e.getMessage());
        }
    }

    private void showSuccessAlert(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Terima Kasih!");
        alert.setHeaderText(null);
        alert.setContentText("Donasi telah disalurkan, terima kasih!");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/Volunteer-dashboard-view.fxml"));
                    Parent dashboardRoot = loader.load();

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(dashboardRoot));
                    stage.setTitle("Volunteer Dashboard");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}