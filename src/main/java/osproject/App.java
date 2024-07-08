package osproject;

import java.util.*;
import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import osproject.hardware.ProcessMonitor;
import osproject.hardware.CpuMonitor;
import osproject.hardware.DiskMonitor;
import osproject.hardware.HardwareMonitor;
import osproject.hardware.NetworkMonitor;
import osproject.hardware.RamMonitor;
import osproject.logging.LogMonitor;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private double tickInterval = .5;
    // private static XYChart.Series<Number, Number> series = new
    // XYChart.Series<Number, Number>();
    private static LineChart<Number, Number> currentChart = new LineChart<>(new NumberAxis(),
            new NumberAxis());
    private static HardwareMonitor hardwareMonitor;

    private static StringObserver m = new StringObserver();

    private static CpuLoad CpuLoad = new CpuLoad();
    private static DoubleObserver cpuObserver = new DoubleObserver();
    private static CpuMonitor cpuMonitor = new CpuMonitor();
    private static RamMonitor ramMonitor = new RamMonitor();
    private static DiskMonitor diskMonitor = new DiskMonitor();
    private static NetworkMonitor networkMonitor = new NetworkMonitor();
    private static ProcessMonitor processMonitor = new ProcessMonitor();

    private static DoubleObserver ramObserver = new DoubleObserver();
    private static DoubleObserver diskObserver = new DoubleObserver();
    private static DoubleObserver networkObserver = new DoubleObserver();
    private static StringObserver targetLabel = new StringObserver();
    private static StringObserver processLabel = new StringObserver();
    private static StringObserver infoLabel = new StringObserver();

    private static LogMonitor logMonitor = new LogMonitor();

    private static StringObserver logObserver = new StringObserver();

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage stage) throws IOException {
        hardwareMonitor = cpuMonitor;
        scene = new Scene(loadFXML("resourcemonitor"));
        stage.setScene(scene);

        stage.show();
        /// Label Bindings
        Label cpuLabel = (Label) scene.lookup("#cpuPer");
        cpuMonitor.setObserver(cpuObserver);
        cpuLabel.textProperty().bind(cpuObserver.valueProperty().asString("%.2f%%"));

        Label ramLabel = (Label) scene.lookup("#ramPer");
        ramMonitor.setObserver(ramObserver);
        ramLabel.textProperty().bind(ramObserver.valueProperty().asString("%.2f%%"));

        Label diskLabel = (Label) scene.lookup("#diskPer");
        diskMonitor.setObserver(diskObserver);
        diskLabel.textProperty().bind(diskObserver.valueProperty().asString("%.2f%%"));

        Label networkLabel = (Label) scene.lookup("#networkPer");
        networkMonitor.setObserver(networkObserver);
        networkLabel.textProperty().bind(networkObserver.valueProperty().asString("%.2f%%"));

        Label targetLabelUI = (Label) scene.lookup("#targetLabel");
        targetLabelUI.textProperty().bind(targetLabel.valueProperty());

        Label processLabelUI = (Label) scene.lookup("#processLabel");
        processMonitor.setObserver(processLabel);
        processLabelUI.textProperty().bind(processLabel.valueProperty());

        Label infoLabelUI = (Label) scene.lookup("#infoLabel");
        infoLabelUI.textProperty().bind(infoLabel.valueProperty());

        TextArea logTextArea = (TextArea) scene.lookup("#logLabel");
        logTextArea.setEditable(false);
        logTextArea.setWrapText(true);
        logMonitor.setObserver(logObserver);
        logTextArea.textProperty().bind(logObserver.valueProperty());

        logTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            logTextArea.positionCaret(logTextArea.getText().length());
        });

        currentChart = (LineChart<Number, Number>) scene.lookup("#graph");

        currentChart.setTitle("CPU Usage");
        currentChart.getXAxis().setAutoRanging(false);
        currentChart.getYAxis().setAutoRanging(false);
        currentChart.setAnimated(false);
        NumberAxis xAxis = (NumberAxis) currentChart.getXAxis();
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(12);
        xAxis.setTickUnit(1);
        xAxis.setLabel("X Axis Label");

        NumberAxis yAxis = (NumberAxis) currentChart.getYAxis();
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);
        yAxis.setLabel("Y Axis Label");

        updateChart();

        // Starts the monitor Loop
        targetLabel.setValue("CPU");
        hardwareMonitor = cpuMonitor;
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

    // @FXML
    // private void buttonClick() throws IOException {
    // System.out.println("Button Clicked");
    // System.out.println(m);
    // m.setValue(CpuLoad.getProcessCpuLoad());
    // CpuLoad.status();
    // }

    private static void tick() {
        double cpuPer = cpuMonitor.getLoadPercent();
        double ramPer = ramMonitor.getLoadPercent();
        double diskPer = diskMonitor.getLoadPercent();
        double netPer = networkMonitor.getLoadPercent();

        String cpuStr = String.format("%.2f%%", cpuPer);
        String ramStr = String.format("%.2f%%", ramPer);
        String diskStr = String.format("%.2f%%", diskPer);
        String netStr = String.format("%.2f%%", netPer);

        logMonitor.log("CPU: " + cpuStr + "RAM: " + ramStr + "Disk: " + diskStr + "Network: " + netStr);
        processMonitor.getProcessList(10);
        updateChart();

    }

    private static void updateChart() {

        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
        // populating the series with data
        ArrayList<Double> cpuLog = hardwareMonitor.getLog();

        for (int i = 0; i < cpuLog.size(); i++) {
            // series.getData().add(new XYChart.Data<Number, Number>(i, i));
            series.getData().add(new XYChart.Data<Number, Number>(i, cpuLog.get(i)));

        }

        currentChart.getData().setAll(series);

    }

    @FXML
    private void setTarget(MouseEvent event) {

        Node sourceNode = (Node) event.getSource();
        String sourceId = sourceNode.getId();
        System.out.println("Source ID: " + sourceId);

        switch (sourceId) {
            case "cpuPane":
                targetLabel.setValue("CPU");
                hardwareMonitor = cpuMonitor;
                break;
            case "ramPane":
                targetLabel.setValue("RAM");
                hardwareMonitor = ramMonitor;
                break;
            case "diskPane":
                targetLabel.setValue("Disk");
                hardwareMonitor = diskMonitor;
                break;
            case "networkPane":
                targetLabel.setValue("Network");
                hardwareMonitor = networkMonitor;
                break;

            default:

                break;
        }

    }

}