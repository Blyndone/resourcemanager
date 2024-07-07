package osproject.hardware;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import osproject.DoubleObserver;
import osproject.StringObserver;

public class ProcessMonitor {

    StringObserver processList;

    SystemInfo si = new SystemInfo();
    OperatingSystem os = si.getOperatingSystem();

    private static ArrayList<String> processLog = new ArrayList<>();

    public ArrayList<String> getProcessList(int size) {
        List<OSProcess> list = os.getProcesses();

        List<OSProcess> largestProcesses = list.stream()
                .sorted(Comparator.comparingLong(OSProcess::getResidentSetSize).reversed())
                .limit(size)
                .collect(Collectors.toList());
        // Clear previous entries in processLog
        processLog.clear();

        // Step 3: Format and store the process information
        for (OSProcess osProcess : largestProcesses) {
            String processInfo = osProcess.getProcessID() + " " + osProcess.getName() + " "
                    + formatSize(osProcess.getResidentSetSize());

            processLog.add(processInfo);
        }

        processList.setValue(String.join("\n", processLog));

        return processLog;
    }

    // public static void main(String[] args) {
    // ProcessMonitor pm = new ProcessMonitor();
    // pm.getProcessList(10);
    // }

    private String formatSize(long size) {
        if (size <= 0)
            return "0 B";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB", "PB", "EB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    // public Double getLoadPercent() {

    // new Thread(() -> {
    // percent = processor.getSystemCpuLoad(delay) * 100;
    // // System.out.println("CPU Load: " + percent + "%");
    // }).start();

    // cpuLoadPercent.setValue(percent);
    // updateLog();
    // return percent;
    // }

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

    // public ArrayList<Double> getLog() {
    // return cpuLog;
    // }

    // public void updateLog() {

    // if (cpuLog.size() > 12) {
    // cpuLog.remove(0);
    // }
    // cpuLog.add(percent);
    // }

}
