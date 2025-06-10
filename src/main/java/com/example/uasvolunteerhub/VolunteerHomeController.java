package com.example.uasvolunteerhub;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class VolunteerHomeController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}