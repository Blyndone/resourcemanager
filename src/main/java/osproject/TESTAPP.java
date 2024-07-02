package osproject;

import java.io.IOException;
import javafx.fxml.FXML;

public class TESTAPP {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}