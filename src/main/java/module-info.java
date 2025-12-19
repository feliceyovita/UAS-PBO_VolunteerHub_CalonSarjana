module com.example.uasvolunteerhub {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    opens com.example.uasvolunteerhub to javafx.fxml, org.testfx;
    exports com.example.uasvolunteerhub;
}
