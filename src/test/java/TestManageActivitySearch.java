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

public class TestManageActivitySearch extends ApplicationTest {

    @Start
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                Thread.currentThread().getContextClassLoader()
                        .getResource("com/example/uasvolunteerhub/manageActivity-view.fxml")
        );
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testSearch() {

        clickOn("#searchField").write("pohon"); // input kosong
        clickOn("#searchButton");

        waitForFxEvents();

        // FlowPane tetap tampil = aplikasi tidak crash
        verifyThat("#activitiesFlowPane", NodeMatchers.isVisible());
    }
}
