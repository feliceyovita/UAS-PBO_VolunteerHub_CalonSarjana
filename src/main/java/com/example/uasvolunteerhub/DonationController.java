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
        NavigationUtil.goTo(event, "/com/example/uasvolunteerhub/history-view.fxml", "Activity History");
    }

    @FXML
    private void handleSubmitDonation(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Terima Kasih!");
        alert.setHeaderText(null);
        alert.setContentText("Donasi telah disalurkan, terima kasih!");

        alert.showAndWait().ifPresent(response -> {
            // Setelah klik OK, pindah ke halaman dashboard
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
}
