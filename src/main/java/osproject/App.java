package osproject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

import java.util.TimerTask;

import com.sun.glass.ui.Timer;
import javafx.animation.AnimationTimer;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static StringObserver m = new StringObserver();
    private static TestClass TestClass = new TestClass();
    
    
    
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("resourcemonitor"), 640, 480);
        stage.setScene(scene);
        
        stage.show();

        Label lookedupLabel = (Label) scene.lookup("#testpercentage");        
        
        lookedupLabel.textProperty().bind(m.valueProperty());



        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame check = new KeyFrame(
        Duration.seconds(0.5),
        event -> {
             m.setValue(TestClass.getProcessCpuLoad());
        TestClass.status();
        }
        );

        timeline.getKeyFrames().addAll(check);
        timeline.play();



    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {

        launch();
    }

    @FXML
    private void buttonClick() throws IOException {
        System.out.println("Button Clicked");
        System.out.println(m);
        m.setValue(TestClass.getProcessCpuLoad());
        TestClass.status();
    }
    
}