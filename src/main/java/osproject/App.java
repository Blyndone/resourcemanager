package osproject;

import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import oshi.hardware.HWDiskStore;
import oshi.software.os.OSProcess;
import osproject.hardware.ProcessMonitor;
import osproject.hardware.CpuMonitor;
import osproject.hardware.DiskMonitor;
import osproject.hardware.HardwareMonitor;
import osproject.hardware.NetworkMonitor;
import osproject.hardware.RamMonitor;
import osproject.logging.LogMonitor;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    private static Scene scene;
    private double tickInterval = .5;
    // private static XYChart.Series<Number, Number> series = new
    // XYChart.Series<Number, Number>();
    private static LineChart<Number, Number> currentChart = new LineChart<>(new NumberAxis(),
            new NumberAxis());
    private static HardwareMonitor hardwareMonitor;

    private static DoubleObserver cpuObserver = new DoubleObserver();
    private static CpuMonitor cpuMonitor = new CpuMonitor();
    private static RamMonitor ramMonitor = new RamMonitor();
    private static DiskMonitor diskMonitor = new DiskMonitor();
    private static ArrayList<DiskMonitor> diskMonitors = new ArrayList<>();

    private static NetworkMonitor networkMonitor = new NetworkMonitor();
    private static ProcessMonitor processMonitor = new ProcessMonitor();
    private static DoubleObserver ramObserver = new DoubleObserver();

    // private static DoubleObserver diskObserver = new DoubleObserver();
    // private static ArrayList<DoubleObserver> diskObservers = new ArrayList<>();

    private static DoubleObserver networkObserver = new DoubleObserver();
    private static StringObserver targetLabel = new StringObserver();
    // private static StringObserver processLabel = new StringObserver();
    // private static ArrayList<StringObserver> processObservers = new
    // ArrayList<>();
    // private static StringObserver infoLabel = new StringObserver();
    private static Label infoLabelUI = new Label();
    private static int currentProcessId = 0;

    private static LogMonitor logMonitor = new LogMonitor();

    private static StringObserver logObserver = new StringObserver();

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage stage) throws IOException {
        hardwareMonitor = cpuMonitor;
        scene = new Scene(loadFXML("resourcemonitor"));
        String css = this.getClass().getResource("/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.setTitle("Resource Monitor");
        stage.setResizable(false);
        stage.show();
        /// Label Bindings
        initializeLabels();

        initializeChart();

        // Starts the monitor Loop
        targetLabel.setValue("CPU - " + cpuMonitor.getHardwareName());
        hardwareMonitor = cpuMonitor;

        startMonitor();

    }

    private void initializeLabels() throws IOException {
        Label cpuLabel = (Label) scene.lookup("#cpuPer");
        cpuMonitor.setObserver(cpuObserver);
        cpuLabel.textProperty().bind(cpuObserver.valueProperty().asString("%.2f%%"));
        Label cpuhardLabel = (Label) scene.lookup("#cpuHard");
        cpuhardLabel.textProperty().set(cpuMonitor.getHardwareName());

        Label ramLabel = (Label) scene.lookup("#ramPer");
        ramMonitor.setObserver(ramObserver);
        ramLabel.textProperty().bind(ramObserver.valueProperty().asString("%.2f%%"));
        Label ramHardLabel = (Label) scene.lookup("#ramHard");
        ramHardLabel.textProperty().set(ramMonitor.getHardwareName());

        // Label diskLabel = (Label) scene.lookup("#diskPer");
        List<HWDiskStore> disks = diskMonitor.getDisks();
        /// This will be for multiple disks

        diskMonitor.setScene(scene);
        int i = 0;
        for (HWDiskStore disk : disks) {

            loadDisk(disk, i);

            // System.out.println("Disk: " + disk.getName());
            i++;
        }
        // End

        /// Single Hard Coded Disk
        // diskMonitor = new DiskMonitor(disks.get(0));
        // diskMonitor.setObserver(diskObserver);
        // diskLabel.textProperty().bind(diskObserver.valueProperty().asString("%.2f%%"));

        Label networkLabel = (Label) scene.lookup("#networkPer");
        networkMonitor.setObserver(networkObserver);
        networkLabel.textProperty().bind(networkObserver.valueProperty().asString("%.2f%%"));
        Label netHardLabel = (Label) scene.lookup("#netHard");
        netHardLabel.textProperty().set(networkMonitor.getHardwareName());

        Label targetLabelUI = (Label) scene.lookup("#targetLabel");
        targetLabelUI.textProperty().bind(targetLabel.valueProperty());

        VBox processPane = (VBox) scene.lookup("#processPane");

        processPane.setSpacing(5);

        ArrayList<OSProcess> processList = processMonitor.getProcess();
        processMonitor.setScene(scene);

        i = 0;
        for (OSProcess process : processList) {
            StringObserver observer = new StringObserver();
            // processObservers.add(observer);
            processMonitor.addObserver(observer);
            Label label = new Label(processMonitor.getBasicProcessInfo(process));
            label.setId("process_" + i);
            label.textProperty().bind(observer.valueProperty());
            label.onMouseClickedProperty().set(event -> processEntityClick(event));

            processPane.getChildren().add(label);
            i++;
        }

        infoLabelUI = (Label) scene.lookup("#infoLabel");
        // infoLabelUI.textProperty().bind(infoLabel.valueProperty());

        TextArea logTextArea = (TextArea) scene.lookup(
                "#logLabel");
        logTextArea.setEditable(false);
        logTextArea.setWrapText(true);
        logMonitor.setObserver(logObserver);
        logTextArea.textProperty().bind(logObserver.valueProperty());

        logTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            logTextArea.positionCaret(logTextArea.getText().length());
        });
    }

    private void loadDisk(HWDiskStore disk, int i) throws IOException {

        DiskMonitor diskMonitor = new DiskMonitor(disk);
        DoubleObserver observer = new DoubleObserver();
        System.out.println("Disk: " + disk.getName() + observer.toString());

        VBox hardwarelist = (VBox) scene.lookup("#hardwarelist");
        URL diskpane = getClass().getResource("diskpane.fxml");
        FXMLLoader loader = new FXMLLoader(diskpane);
        Pane thisdisk = (Pane) loader.load();

        diskMonitors.add(diskMonitor);
        diskMonitor.setObserver(observer);

        thisdisk.setId("diskPane" + i);
        Label name = (Label) thisdisk.getChildren().get(0);
        Label hardware = (Label) thisdisk.getChildren().get(2);
        Label percent = (Label) thisdisk.getChildren().get(1);

        name.setId("diskLabel" + i);
        percent.setId("diskPer" + i);
        name.setText("Disk " + i);
        percent.textProperty().bind(observer.valueProperty().asString("%.2f%%"));
        hardware.setText(disk.getModel());

        hardwarelist.getChildren().add(2 + i, thisdisk);

    }

    private void initializeChart() {
        currentChart = (LineChart<Number, Number>) scene.lookup("#graph");

        // currentChart.setTitle("CPU Usage");
        currentChart.getXAxis().setAutoRanging(false);
        currentChart.getYAxis().setAutoRanging(false);
        currentChart.setAnimated(false);
        NumberAxis xAxis = (NumberAxis) currentChart
                .getXAxis();
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(12);
        xAxis.setTickUnit(1);
        // xAxis.setLabel("Time Passed");

        NumberAxis yAxis = (NumberAxis) currentChart
                .getYAxis();
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);
        // yAxis.setLabel("Percent Usage");
        currentChart.setLegendVisible(false);
        xAxis.setTickLabelsVisible(false);

        updateChart();
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

    // private static Pane loadFXMLNode(String fxml) throws IOException {

    // FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml +
    // ".fxml"));

    // return fxmlLoader.load();

    // }

    public static void main(String[] args) {

        launch();
    }

    private static void tick() {
        double cpuPer = cpuMonitor.getLoadPercent();
        double ramPer = ramMonitor.getLoadPercent();
        // double diskPer = diskMonitor.getLoadPercent();
        double diskPer = 0;
        for (DiskMonitor diskMonitor : diskMonitors) {
            diskMonitor.getLoadPercent();

        }
        double netPer = networkMonitor.getLoadPercent();

        String cpuStr = String.format("%.2f%%", cpuPer);
        String ramStr = String.format("%.2f%%", ramPer);
        String diskStr = String.format("%.2f%%", diskPer);
        String netStr = String.format("%.2f%%", netPer);

        logMonitor.log("CPU: " + cpuStr + "RAM: " + ramStr + "Disk: " + diskStr + "Network: " + netStr);
        processMonitor.updateProcesses();
        updateProcessLabel(currentProcessId);

        updateChart();

    }

    private static void updateChart() {

        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();

        ArrayList<Double> hwLog = hardwareMonitor.getLog();

        for (int i = 0; i < hwLog.size(); i++) {

            series.getData().add(new XYChart.Data<Number, Number>(i, hwLog.get(i)));

        }

        NumberAxis yAxis = (NumberAxis) currentChart
                .getYAxis();
        yAxis.setUpperBound((int) hardwareMonitor.getMaxPercent());
        NumberAxis xAxis = (NumberAxis) currentChart
                .getXAxis();
        xAxis.setUpperBound(Math.max(hwLog.size(), 12));
        series.setName("");
        currentChart.getData().setAll(series);

    }

    @FXML
    private void setTarget(MouseEvent event) {

        Node sourceNode = (Node) event.getSource();
        String sourceId = sourceNode.getId();
        System.out.println("Source ID: " + sourceId);

        switch (sourceId) {
            case "cpuPane":
                targetLabel.setValue("CPU - " + cpuMonitor.getHardwareName());
                currentChart.setTitle("Cpu Usage");
                hardwareMonitor = cpuMonitor;
                break;

            case "ramPane":
                targetLabel.setValue("RAM- " + ramMonitor.getHardwareName());
                currentChart.setTitle("Ram Usage");
                hardwareMonitor = ramMonitor;
                break;

            case "diskPane0":
                targetLabel.setValue("Disk- " + diskMonitors.get(0).getHardwareName());
                currentChart.setTitle("Disk Usage");
                hardwareMonitor = diskMonitors.get(0);
                break;

            case "diskPane1":
                targetLabel.setValue("Disk- " + diskMonitors.get(1).getHardwareName());
                currentChart.setTitle("Disk Usage");
                hardwareMonitor = diskMonitors.get(1);
                break;

            case "diskPane2":
                targetLabel.setValue("Disk- " + diskMonitors.get(2).getHardwareName());
                currentChart.setTitle("Disk Usage");
                hardwareMonitor = diskMonitors.get(2);
                break;

            case "networkPane":
                targetLabel.setValue("Network- " + networkMonitor.getHardwareName());
                currentChart.setTitle("Network Usage");
                hardwareMonitor = networkMonitor;
                break;

            default:

                break;
        }

    }

    @FXML
    private void processEntityClick(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        String sourceId = sourceNode.getId();
        System.out.println("Source ID: " + sourceId);

        String[] parts = sourceId.split("_");
        int index = Integer.parseInt(parts[1]);
        OSProcess process = processMonitor.getProcess(index);
        int processID = process.getProcessID();
        if (currentProcessId != processID) {
            currentProcessId = processID;
        }
        updateProcessLabel(currentProcessId);

    }

    private static void updateProcessLabel(int procID) {
        if (procID == 0) {
            return;
        }

        OSProcess process = processMonitor.getProcessByID(procID);
        Map<String, Object> processDetails = processMonitor.getDetailProcessInfo(process);

        int processID = (int) processDetails.get("Process ID");
        String name = (String) processDetails.get("Name");
        String memory = (String) processDetails.get("Memory");
        int priority = (int) processDetails.get("Priority");
        long uptime = (long) processDetails.get("Uptime");
        double cpuLoad = (double) processDetails.get("CPU Load");
        int threadCount = (int) processDetails.get("Thread Count");
        String state = (String) processDetails.get("State");

        TextFlow textFlow = new TextFlow();

        Text nameText = new Text("Name: ");
        nameText.setStyle("-fx-font-weight: bold");
        Text nameValue = new Text(name + "\n");

        Text processIDText = new Text("Process ID: ");
        processIDText.setStyle("-fx-font-weight: bold");
        Text processIDValue = new Text(processID + " \t|\t");

        Text memoryText = new Text("Memory: ");
        memoryText.setStyle("-fx-font-weight: bold");
        Text memoryValue = new Text(memory + "\n");

        Text priorityText = new Text("Priority: ");
        priorityText.setStyle("-fx-font-weight: bold");
        Text priorityValue = new Text(priority + " \t|\t");

        Text uptimeText = new Text("Uptime: ");
        uptimeText.setStyle("-fx-font-weight: bold");
        Text uptimeValue = new Text(uptime + " \t|\t");

        Text cpuLoadText = new Text("CPU Load: ");
        cpuLoadText.setStyle("-fx-font-weight: bold");
        Text cpuLoadValue = new Text(String.format("%.2f", cpuLoad) + "\n");

        Text threadCountText = new Text("Thread Count: ");
        threadCountText.setStyle("-fx-font-weight: bold");
        Text threadCountValue = new Text(threadCount + "\n");

        Text stateText = new Text("State: ");
        stateText.setStyle("-fx-font-weight: bold");
        Text stateValue = new Text(state);

        textFlow.getChildren().addAll(
                nameText, nameValue,
                processIDText, processIDValue,
                memoryText, memoryValue,
                priorityText, priorityValue,
                uptimeText, uptimeValue,
                cpuLoadText, cpuLoadValue,
                threadCountText, threadCountValue,
                stateText, stateValue);

        infoLabelUI.setGraphic(textFlow);
    }

}