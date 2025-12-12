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

public class TestSignUpInputValid extends ApplicationTest {
    @Override
    @Start
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                Thread.currentThread().getContextClassLoader()
                        .getResource("com/example/uasvolunteerhub/SignUp-view.fxml")
        );
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void TestSignUpInputValid() {

        // input
        clickOn("#nameField").write("Dini Permata");
        clickOn("#emailField").write("dinisahfitri@gmail.com");   // email salah
        clickOn("#passwordField").write("password123");

        // klik tombol signup
        clickOn("#tombolSignupSubmit");

        // tunggu FX thread jalan
        waitForFxEvents();

        // Cek apakah ALERT tampil
        verifyThat(".alert", NodeMatchers.isVisible());
    }
}
