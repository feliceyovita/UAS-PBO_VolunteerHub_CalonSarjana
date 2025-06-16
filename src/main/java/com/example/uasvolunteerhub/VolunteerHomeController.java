package com.example.uasvolunteerhub;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class VolunteerHomeController {

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    public void initialize() {
        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());
    }

    @FXML
    protected void onLoginButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 960, 540);
            Stage stage = new Stage();
            stage.setTitle("Login Page");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onSignUpButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/uasvolunteerhub/SignUp-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 960, 540);
            Stage stage = new Stage();
            stage.setTitle("Register Page");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
