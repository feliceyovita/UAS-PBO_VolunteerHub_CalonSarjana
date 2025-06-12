module com.example.uasvolunteerhub {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;


    opens com.example.uasvolunteerhub to javafx.fxml;
    exports com.example.uasvolunteerhub;
}