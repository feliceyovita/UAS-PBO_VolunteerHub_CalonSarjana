import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class TestFillProfile extends ApplicationTest {

    @Override
    @Start
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                Thread.currentThread().getContextClassLoader()
                        .getResource("com/example/uasvolunteerhub/fill-profile.fxml")
        );
        stage.setScene(new Scene(root));
        stage.show();
    }
    @Test
    void testFillProfile() {
        clickOn("#nameField").write("Dini Permata");
        clickOn("#emailField").write("dini@gmail.com");
        clickOn("#phoneField").write("08123456789");
        clickOn("#dobPicker");
        type(javafx.scene.input.KeyCode.ENTER);
        clickOn("#saveButton");
        waitForFxEvents();
        verifyThat(".alert", NodeMatchers.isVisible());
    }
}
