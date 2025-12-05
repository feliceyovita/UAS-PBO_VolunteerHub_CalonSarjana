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

        // [PERBAIKAN DISINI]
        // Beri waktu 1 detik agar animasi pindah ke halaman Login selesai
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // --- 2. ISI FORM LOGIN ---
        // ID ini sudah benar sesuai FXML baris 21
        clickOn("#usernameField").write("admin@gmail.com");

        // ID ini sudah benar sesuai FXML baris 26
        clickOn("#passwordField").write("admin123");

        // ID ini sudah benar sesuai FXML baris 29
        clickOn("#tombolLoginSubmit");

        // --- 3. MASUK KE MENU ADD ACTIVITY ---
        // Tunggu lagi karena login butuh proses verifikasi database/pindah scene
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        // Pastikan ID tombol ini ada di halaman Dashboard Admin
        clickOn("#addActivityButton");

        // --- 4. KLIK SUBMIT (TANPA ISI DATA) ---
        // Pastikan ID tombol ini ada di form Add Activity
        clickOn("#submitButton");

        // --- 5. VERIFIKASI ALERT ERROR MUNCUL ---
        verifyThat(".dialog-pane", isVisible());

        // Tutup Alert
        clickOn("OK");
    }
}