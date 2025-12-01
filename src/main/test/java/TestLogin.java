import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import static org.testfx.api.FxAssert.verifyThat;

import com.example.uasvolunteerhub.VolunteerHome;

public class TestLogin extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new VolunteerHome().start(stage);
        stage.show();
    }

    @Test
    void testLoginKosong() {
        // 1. KLIK TOMBOL DI HALAMAN DEPAN
        clickOn("#loginButton");

        // 2. ISI FORM LOGIN
        clickOn("#usernameField").write("");
        clickOn("#passwordField").write("");

        // 3. KLIK TOMBOL LOG IN
        clickOn("#tombolLoginSubmit");

        // 4. TUNGGU ALERT MUNCUL
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}

        // 5. VALIDASI ALERT
        verifyThat(".dialog-pane", NodeMatchers.isVisible());
    }
}