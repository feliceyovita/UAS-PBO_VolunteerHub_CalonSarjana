module com.example.uasvolunteerhub {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.uasvolunteerhub to javafx.fxml;
    exports com.example.uasvolunteerhub;
}