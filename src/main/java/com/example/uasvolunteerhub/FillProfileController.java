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
        String dob = (dobPicker.getValue() != null) ? dobPicker.getValue().toString() : "";

        Session.isUserRegistered = true;

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
