import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;

public class TestLogoutSistem extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {

        // User sudah Login -> langsung ke Dashboard
        Parent root = FXMLLoader.load(
                getClass().getResource(
                        "/com/example/uasvolunteerhub/Volunteer-dashboard-view.fxml"
                )
        );

        stage.setScene(new Scene(root));
        stage.show();
    }

     // Klik Logout -> langsung ke Hello View
    @Test
    void TC_Logout_System_Success() {

        verifyThat("#logoutBtn", node -> node != null);

        clickOn("#logoutBtn");

        verifyThat("#rootPane", node -> node != null);

        verifyThat("#loginButton", node -> node != null);
    }
}