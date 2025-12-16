import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;

public class TestChangePasswordInvalidEmail extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/com/example/uasvolunteerhub/forgotPassword-view.fxml"
                )
        );

        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void GagalChangePasswordInvalidEmail() {

        clickOn("#emailField")
                .write("email_salah_yang_belum_terdaftar@gmail.com");

        clickOn("Send");

        verifyThat(".dialog-pane", isVisible());

        clickOn("OK");
    }
}