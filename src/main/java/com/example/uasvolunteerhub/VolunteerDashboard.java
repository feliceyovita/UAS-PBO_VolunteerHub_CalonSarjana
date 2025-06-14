package com.example.uasvolunteerhub;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class VolunteerDashboard implements Dashboard {
    @Override
    public void loadDashboard(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("volunteerhome-view.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Volunteer Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
