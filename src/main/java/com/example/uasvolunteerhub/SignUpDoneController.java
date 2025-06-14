package com.example.uasvolunteerhub;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class SignUpDoneController {

    @FXML
    private Button goToDashboardButton;

    @FXML
    private void handleGoToDashboard() {
        Stage stage = (Stage) goToDashboardButton.getScene().getWindow();
        Session.loggedInDashboard.loadDashboard(stage);
    }
}
