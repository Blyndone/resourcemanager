package osproject.hardware;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import osproject.StringObserver;

public class ProcessMonitor {
    Scene scene;

    StringObserver processList;

    SystemInfo si = new SystemInfo();
    OperatingSystem os = si.getOperatingSystem();

    private static ArrayList<String> processLog = new ArrayList<>();
    private static ArrayList<OSProcess> currentProcesses = new ArrayList<>();
    private static ArrayList<StringObserver> observers = new ArrayList<>(10);

    public ProcessMonitor() {
        getProcessList(10);
    }

    public ArrayList<String> getProcessList(int size) {
        List<OSProcess> list = os.getProcesses();

        List<OSProcess> largestProcesses = list.stream()
                .sorted(Comparator.comparingLong(OSProcess::getResidentSetSize).reversed())
                .limit(size)
                .collect(Collectors.toList());

        processLog.clear();
        currentProcesses.clear();

        for (OSProcess osProcess : largestProcesses) {
            currentProcesses.add(osProcess);
            String processInfo = osProcess.getProcessID() + " " + osProcess.getName() + " "
                    + formatSize(osProcess.getResidentSetSize());

            processLog.add(processInfo);

        }

        return processLog;
    }

    private String formatSize(long size) {
        if (size <= 0)
            return "0 B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB", "PB", "EB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public String getHardwareName() {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHardwareName'");
    }

    public StringObserver getObserver() {
        return processList;
    }

    public void setObserver(StringObserver observer) {
        processList = observer;
    }

    public OSProcess getProcess(int index) {
        return currentProcesses.get(index);
    }

    public ArrayList<OSProcess> getProcess() {
        // getProcessList(10);

        return currentProcesses;
    }

    public Map<String, Object> getDetailProcessInfo(OSProcess process) {
        Map<String, Object> processDetails = new HashMap<String, Object>();
        processDetails.put("Process ID", process.getProcessID());
        processDetails.put("Name", process.getName());
        processDetails.put("Memory", formatSize(process.getResidentSetSize()));
        processDetails.put("Priority", process.getPriority());
        processDetails.put("Uptime", process.getUpTime());
        processDetails.put("CPU Load", process.getProcessCpuLoadCumulative());
        processDetails.put("Thread Count", process.getThreadCount());
        processDetails.put("State", process.getState().toString());

        return processDetails;
    }

    public String getBasicProcessInfo(OSProcess process) {
        return process.getProcessID() + " " + process.getName() + " " + formatSize(process.getResidentSetSize());
    }

    public void setObservers(ArrayList<StringObserver> observers) {
        this.observers = observers;

    }

    public void updateProcesses() {
        ArrayList<String> processLog = getProcessList(10);
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).setValue(processLog.get(i));
            Label targetLabelUI = (Label) scene.lookup("process_" + i);
            // targetLabelUI.textProperty().bind(observers.get(i).valueProperty());
        }

    }

    public void addObserver(StringObserver observer) {
        observers.add(observer);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
