import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class TestRegistrationFormNegative extends ApplicationTest {

    @Override
    @Start
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                Thread.currentThread().getContextClassLoader()
                        .getResource("com/example/uasvolunteerhub/RegistrationForm-view.fxml")
        );
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testRegistrationFormInvalidAge() {

        // Isi semua field valid, tapi age terlalu kecil
        clickOn("#nameField").write("Dini");
        clickOn("#emailField").write("dinisahfitriiiii@gmail.com");
        clickOn("#phoneField").write("081234567890");
        clickOn("#jobComboBox").clickOn("Student");
        clickOn("#ageField").write(" lima"); // usia gagal
        clickOn("#addressField").write("Jakarta Selatan");
        clickOn("#reasonField").write("ingin berkontribusi");

        // Klik tombol Submit
        clickOn("#submitBtn");

        // Tunggu event JavaFX selesai
        waitForFxEvents();


    }
}
