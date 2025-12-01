import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import static org.junit.jupiter.api.Assertions.*;

import com.example.uasvolunteerhub.VolunteerHome;

public class TestBukaAplikasi extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        new VolunteerHome().start(stage);
        this.stage.show();
    }

    // --- TEST 1: Cek Buka Aplikasi ---
    @Test
    void testAplikasiMuncul() {
        assertTrue(stage.isShowing());
        assertEquals("VHLabPBO", stage.getTitle());
    }

    // --- TEST 2: Responsiveness ---
    @Test
    void testUbahUkuranLayar() {
        // 1. Ubah jadi Besar
        interact(() -> {
            stage.setWidth(1366);
            stage.setHeight(768);
        });
        // Cek apakah ukurannya beneran berubah
        assertEquals(1366, stage.getWidth());

        // 2. Ubah jadi Kecil
        interact(() -> {
            stage.setWidth(600);
            stage.setHeight(400);
        });
        // Cek lagi
        assertEquals(600, stage.getWidth());
    }
}