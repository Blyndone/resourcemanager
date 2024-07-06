package osproject;

import java.util.*;
import java.util.ArrayList;
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

import osproject.hardware.CpuMonitor;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private double tickInterval = .5;
    // private static XYChart.Series<Number, Number> series = new
    // XYChart.Series<Number, Number>();
    private static LineChart<Number, Number> cpuChart = new LineChart<>(new NumberAxis(),
            new NumberAxis());
    private static StringObserver m = new StringObserver();

    private static CpuLoad CpuLoad = new CpuLoad();
    private static DoubleObserver cpuObserver = new DoubleObserver();
    private static CpuMonitor cpuMonitor = new CpuMonitor();

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
        cpuMonitor.setObserver(cpuObserver);
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

        // LineChart<Number, Number> cpuChart = (LineChart<Number, Number>)
        cpuChart = (LineChart<Number, Number>) scene.lookup("#graph");

        cpuChart.setTitle("CPU Usage");
        cpuChart.getXAxis().setAutoRanging(false);
        cpuChart.getYAxis().setAutoRanging(false);
        cpuChart.setAnimated(false);
        NumberAxis xAxis = (NumberAxis) cpuChart.getXAxis();
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(12);
        xAxis.setTickUnit(1);
        xAxis.setLabel("X Axis Label");

        NumberAxis yAxis = (NumberAxis) cpuChart.getYAxis();
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);
        yAxis.setLabel("Y Axis Label");

        updateChart(cpuChart);

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
        cpuMonitor.getLoadPercent();
        updateChart(cpuChart);

    }

    private static void updateChart(LineChart<Number, Number> cpuChart) {

        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
        // populating the series with data
        ArrayList<Double> cpuLog = cpuMonitor.getLog();

        for (int i = 0; i < cpuLog.size(); i++) {
            // series.getData().add(new XYChart.Data<Number, Number>(i, i));
            series.getData().add(new XYChart.Data<Number, Number>(i, cpuLog.get(i)));

        }

        cpuChart.getData().setAll(series);

    }

}