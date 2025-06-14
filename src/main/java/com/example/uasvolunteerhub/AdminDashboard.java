package com.example.uasvolunteerhub;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class AdminDashboard implements Dashboard {
    @Override
    public void loadDashboard(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("adminhome-view.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}