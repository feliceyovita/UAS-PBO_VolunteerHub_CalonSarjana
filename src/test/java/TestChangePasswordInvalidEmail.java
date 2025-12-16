import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.isVisible;

import com.example.uasvolunteerhub.VolunteerHome;

public class TestForgotPasswordInvalidEmail extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new VolunteerHome().start(stage);
        stage.show();
    }

    @Test
    void TC13_GagalGantiPassword_EmailTidakTerdaftar() {

        // 1. Dari halaman login → Forgot Password
        clickOn("#forgotPasswordButton");

        // 2. Input email yang TIDAK terdaftar
        clickOn("#emailField")
                .eraseText(50)
                .write("emailtidakterdaftar@gmail.com");

        // 3. Klik tombol Send
        clickOn("#sendButton");

        // 4. Verifikasi sistem MENOLAK & alert muncul
        verifyThat(".dialog-pane", isVisible());

        // (Opsional) Verifikasi teks error jika ingin lebih ketat
        // verifyThat("Email tidak ditemukan di database.", isVisible());

        // 5. Tutup alert
        clickOn("OK");
    }
}