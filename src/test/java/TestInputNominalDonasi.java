import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertFalse;


public class TestInputNominalDonasi extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        var resource = getClass().getResource("/Donation-view.fxml");

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
        clickOn("#amountField").write("abc");
        clickOn("PayPal");
        clickOn("#submitButton");
        assertFalse(
                lookup(".dialog-pane").tryQuery().isPresent(),
                "Dialog error seharusnya muncul, tapi tidak muncul"
        );
    }
}