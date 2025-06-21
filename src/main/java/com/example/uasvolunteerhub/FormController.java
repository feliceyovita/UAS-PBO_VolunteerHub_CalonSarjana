package com.example.uasvolunteerhub;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

import java.util.regex.Pattern;

public abstract class FormController {

    protected boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    protected boolean isEmpty(TextArea area) {
        return area.getText() == null || area.getText().trim().isEmpty();
    }

    protected boolean isValidEmail(String email) {
        if (email == null) return false;
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(emailRegex, email);
    }

    protected boolean isNumeric(String str) {
        if (str == null) return false;
        return str.matches("\\d+");
    }

    protected void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
