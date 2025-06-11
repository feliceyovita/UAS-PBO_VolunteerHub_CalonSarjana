module com.example.uasvolunteerhub {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.uasvolunteerhub to javafx.fxml;
    exports com.example.uasvolunteerhub;
}