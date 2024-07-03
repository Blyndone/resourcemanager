package osproject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static StringObserver m = new StringObserver();
    private static CpuLoad CpuLoad = new CpuLoad();
    private double tickInterval = 0.5;
    private static DoubleObserver cpuObserver = new DoubleObserver();
    private static DoubleObserver ramObserver = new DoubleObserver();
    private static DoubleObserver diskObserver = new DoubleObserver();
    private static DoubleObserver networkObserver = new DoubleObserver();
    private static StringObserver targetLabel = new StringObserver();
    private static StringObserver processLabel = new StringObserver();
    private static StringObserver infoLabel = new StringObserver();
    private static StringObserver logLabel = new StringObserver();

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("resourcemonitor"));
        stage.setScene(scene);

        stage.show();
        /// Label Bindings
        Label cpuLabel = (Label) scene.lookup("#cpuPer");
        cpuLabel.textProperty().bind(cpuObserver.valueProperty().asString("%.2f%%"));

        Label ramLabel = (Label) scene.lookup("#ramPer");
        ramLabel.textProperty().bind(ramObserver.valueProperty().asString("%.2f%%"));

        Label diskLabel = (Label) scene.lookup("#diskPer");
        diskLabel.textProperty().bind(diskObserver.valueProperty().asString("%.2f%%"));

        Label networkLabel = (Label) scene.lookup("#networkPer");
        networkLabel.textProperty().bind(networkObserver.valueProperty().asString("%.2f%%"));

        Label targetLabelUI = (Label) scene.lookup("#targetLabel");
        targetLabelUI.textProperty().bind(targetLabel.valueProperty());

        Label processLabelUI = (Label) scene.lookup("#processLabel");
        processLabelUI.textProperty().bind(processLabel.valueProperty());

        Label infoLabelUI = (Label) scene.lookup("#infoLabel");
        infoLabelUI.textProperty().bind(infoLabel.valueProperty());

        Label logLabelUI = (Label) scene.lookup("#logLabel");
        logLabelUI.textProperty().bind(logLabel.valueProperty());

        /// Chart Binding and dummy Data
        LineChart<Number, Number> cpuChart = (LineChart<Number, Number>) scene.lookup("#graph");

        cpuChart.setTitle("CPU Usage");
        cpuChart.getXAxis().setAutoRanging(true);
        cpuChart.getYAxis().setAutoRanging(true);

        NumberAxis xAxis = (NumberAxis) cpuChart.getXAxis();
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(12);
        xAxis.setTickUnit(1);
        xAxis.setLabel("X Axis Label");

        NumberAxis yAxis = (NumberAxis) cpuChart.getXAxis();
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);
        yAxis.setLabel("Y Axis Label");

        XYChart.Series series = new XYChart.Series();
        series.setName("Usage");
        // populating the series with data
        series.getData().add(new XYChart.Data(1, 23));
        series.getData().add(new XYChart.Data(2, 14));
        series.getData().add(new XYChart.Data(3, 15));
        series.getData().add(new XYChart.Data(4, 24));
        series.getData().add(new XYChart.Data(5, 47));
        series.getData().add(new XYChart.Data(6, 65));
        series.getData().add(new XYChart.Data(7, 75));
        series.getData().add(new XYChart.Data(8, 45));
        series.getData().add(new XYChart.Data(9, 43));
        series.getData().add(new XYChart.Data(10, 17));
        series.getData().add(new XYChart.Data(11, 29));
        series.getData().add(new XYChart.Data(12, 25));

        cpuChart.getData().add(series);

        // Starts the monitor Loop
        startMonitor();

    }

    private void startMonitor() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame check = new KeyFrame(
                Duration.seconds(tickInterval),
                event -> {
                    tick();
                });

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
        m.setValue(CpuLoad.getProcessCpuLoad());
        CpuLoad.status();
    }

    private static void tick() {
        cpuObserver.setValue(CpuLoad.getProcessCpuLoadDouble());
        CpuLoad.status();
    }

}