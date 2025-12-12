import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class TestManageActivityScrool extends ApplicationTest {

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
    void testManageActivityPageLoadsAndScrolls() {

        // scroll agar content di flowpane tampil
        moveTo("#activitiesFlowPane");
        scroll(5);

        waitForFxEvents();

        verifyThat("#searchField", isVisible());
        verifyThat("#searchButton", isVisible());
        verifyThat("#activitiesFlowPane", isVisible());
    }
}
