import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import com.example.uasvolunteerhub.VolunteerHome;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;

public class TestFiturActivityAdmin extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new VolunteerHome().start(stage);
        stage.show();
    }

    @Test
    public void testInputActivityKosong() {
        // --- 1. DARI HALAMAN DEPAN, KLIK TOMBOL LOGIN ---
        clickOn("#loginButton");

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // --- 2. ISI FORM LOGIN ---
        clickOn("#usernameField").write("admin@gmail.com");

        clickOn("#passwordField").write("admin123");

        clickOn("#tombolLoginSubmit");

        // --- 3. MASUK KE MENU ADD ACTIVITY ---
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        clickOn("#addActivityButton");

        // --- 4. KLIK SUBMIT (TANPA ISI DATA) ---
        clickOn("#submitButton");

        // --- 5. VERIFIKASI ALERT ERROR MUNCUL ---
        verifyThat(".dialog-pane", isVisible());

        // Tutup Alert
        clickOn("OK");
    }
}