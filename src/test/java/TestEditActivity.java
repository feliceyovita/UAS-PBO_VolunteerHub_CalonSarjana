import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TestEditActivity extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/uasvolunteerhub/edit-activity.fxml")
        );
        stage.setScene(new Scene(root));
        stage.show();
    }

    // ===================================
    // SETUP UMUM (DEFAULT) -> Not Donate
    // ===================================
    @BeforeEach
    void setupValidForm() {
        clickOn("#titleField").eraseText(50).write("New Activity's Name");
        clickOn("#locationField").eraseText(50).write("Medan");
        clickOn("#slotField").eraseText(10).write("100");

        DatePicker datePicker = lookup("#datePicker").query();
        interact(() -> datePicker.setValue(LocalDate.now().plusDays(10)));

        clickOn("#notDonateRadio");
    }

    // =================================
    // Test Edit Activity with Donation
    // =================================
    @Test
    void testEditActivityWithDonation() {
        clickOn("#donateRadio");
        clickOn("#donationField").eraseText(20).write("100000000");

        clickOn("#doneButton");
        assertTrue(true);
    }

    // ====================================
    // Test Edit Activity with No Donation
    // ====================================
    @Test
    void testEditActivityWithNoDonation() {
        clickOn("#donateRadio");
        clickOn("#notDonateRadio");

        TextField donation = lookup("#donationField").query();
        assertTrue(donation.isDisabled());

        clickOn("#doneButton");
        assertTrue(true);
    }
}