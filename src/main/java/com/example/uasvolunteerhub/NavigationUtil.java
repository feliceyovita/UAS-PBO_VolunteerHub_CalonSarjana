package com.example.uasvolunteerhub;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationUtil {

    public static void goTo(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(NavigationUtil.class.getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logout(ActionEvent event) {
        goTo(event, "/com/example/uasvolunteerhub/hello-view.fxml", "Login");
    }
}