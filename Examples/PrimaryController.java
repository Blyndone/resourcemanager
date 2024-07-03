package osproject;

import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    
    @FXML
    private void TEST() throws IOException {
        App.setRoot("TESTAPP");
        System.out.println("TEST");
    }
}
