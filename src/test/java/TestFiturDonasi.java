import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import com.example.uasvolunteerhub.VolunteerHome;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

public class TestFiturDonasi extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        var resourceUrl = VolunteerHome.class.getResource("Donation-view.fxml");
        System.out.println("URL Resource: " + resourceUrl); // Jika ini null, path-nya salah

        if (resourceUrl == null) {
            throw new IllegalStateException("File FXML tidak ditemukan! Cek path-nya.");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(resourceUrl);
        Scene scene = new Scene(fxmlLoader.load(), 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    // --- TC-09: Metode Pembayaran Kosong (Cek Isi Alert) ---
    @Test
    public void testTC09_MetodeKosong() {
        // 1. Input Nominal Valid
        clickOn("#amountField").write("50000");

        clickOn("#submitButton");

        verifyThat(".dialog-pane .content", hasText("Please select payment method"));

        clickOn("OK");
    }
}