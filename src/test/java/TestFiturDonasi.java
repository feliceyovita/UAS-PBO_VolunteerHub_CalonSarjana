import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import com.example.uasvolunteerhub.VolunteerHome; // Pastikan import ini benar sesuai package kamu

import java.net.URL;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class TestFiturDonasi extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        // Coba akses langsung dari root
        URL fxmlLocation = getClass().getResource("/Donation-view.fxml");

        if (fxmlLocation == null) {
            throw new IllegalStateException("File FXML GAK KETEMU! Pastikan file ada di folder resources/com/example/uasvolunteerhub/");
        }
        Parent root = FXMLLoader.load(fxmlLocation);

        stage.setScene(new Scene(root));
        stage.show();
    }

    // --- TC-09: Metode Pembayaran Kosong (Cek Isi Alert) ---
    @Test
    public void testTC09_MetodeKosong() {
        clickOn("#amountField").write("50000");

        clickOn("#submitButton");

        verifyThat(".dialog-pane .content", hasText("Please select payment method"));

        clickOn("OK");
    }
}