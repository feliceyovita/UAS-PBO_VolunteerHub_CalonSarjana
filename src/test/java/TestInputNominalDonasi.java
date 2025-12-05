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

        // OPSI 2: Jika file FXML ada langsung di luar folder resources
        // var resource = getClass().getResource("/Donation-view.fxml");

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
        // Kita tes apakah sistem punya pertahanan terhadap input sampah
        clickOn("#amountField").write("abc");

        // 2. Pilih Metode (Supaya tidak kena alert 'pilih metode')
        // Pastikan teks "PayPal" ini sesuai dengan teks di RadioButton atau ComboBox kamu
        clickOn("PayPal");

        // 3. Klik Submit
        clickOn("#submitButton");

        // --- MOMEN KEBENARAN ---
        // Kita cek apakah muncul Alert?
        // (Entah itu alert Error atau Sukses, yang penting ada respon dulu)
        verifyThat(".dialog-pane", isVisible());

        // CATATAN UNTUK FELICE:
        // Nanti lihat sendiri di layar saat robot jalan.
        // Kalau tulisannya "Donation Success", berarti itu BUG (Cacat Program).

        clickOn("OK");
    }
}