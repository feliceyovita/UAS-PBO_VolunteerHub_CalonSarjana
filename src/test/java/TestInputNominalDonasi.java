import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import com.example.uasvolunteerhub.VolunteerHome; // Pastikan import ini benar

import java.util.Objects; // Tambahan untuk pengecekan null

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class TestInputNominalDonasi extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        var resource = getClass().getResource("/com/example/uasvolunteerhub/Donation-view.fxml");

        if (resource == null) {
            throw new IllegalStateException("File FXML tidak ditemukan! Cek nama file dan lokasinya.");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Scene scene = new Scene(fxmlLoader.load(), 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testTC08_InputNominalInvalid() {
        // 1. Input Data Ngawur (Huruf)
        clickOn("#amountField").write("abc");
        clickOn("PayPal");

        // 3. Klik Submit
        clickOn("#submitButton");

        verifyThat(".dialog-pane", isVisible());

        clickOn("OK");
    }
}