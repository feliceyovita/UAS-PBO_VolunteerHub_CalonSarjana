import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;


import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class testsignupemailsalah extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/uasvolunteerhub/SignUp-view.fxml")
        );
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }


    @Test
    void testsignupemailsalah() {

        // input
        clickOn("#nameField").write("Dini Permata");
        clickOn("#emailField").write("dini@gmail.com");   // email salah
        clickOn("#passwordField").write("password123");

        // klik tombol signup
        clickOn("#tombolSignupSubmit");

        // tunggu FX thread jalan
        waitForFxEvents();

        // Cek apakah ALERT tampil
        verifyThat(".alert", NodeMatchers.isVisible());
    }
}
