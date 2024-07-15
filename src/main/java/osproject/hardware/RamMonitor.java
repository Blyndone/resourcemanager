package osproject.hardware;

import java.util.ArrayList;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import osproject.DoubleObserver;

public class RamMonitor implements HardwareMonitor {
    DoubleObserver ramLoadPercent;
    SystemInfo systemInfo = new SystemInfo();
    HardwareAbstractionLayer hardware = systemInfo.getHardware();
    GlobalMemory memory = hardware.getMemory();

    long totalMemory = memory.getTotal();
    long availableMemory = memory.getAvailable();
    long usedMemory = totalMemory - availableMemory;
    private static ArrayList<Double> ramLog = new ArrayList<>();
    static double percent = 0;
    double maxPercent = 100;

    // Helper method to format bytes into a human-readable format
    private static String formatBytes(long bytes) {
        int unit = 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    @Override
    public Double getLoadPercent() {

        new Thread(() -> {
            availableMemory = memory.getAvailable();
            usedMemory = totalMemory - availableMemory;
            percent = (double) usedMemory / totalMemory * 100;
            // System.out.println("RAM Load: " + percent + "%");
        }).start();
        ramLoadPercent.setValue(percent);
        updateLog();
        return percent;

    }

    @Override
    public String getHardwareName() {

        throw new UnsupportedOperationException("Unimplemented method 'getHardwareName'");
    }

    @Override
    public ArrayList<Double> getLog() {

        return ramLog;
    }

    @Override
    public void setObserver(DoubleObserver observer) {

        ramLoadPercent = observer;
    }

    public void updateLog() {

        if (ramLog.size() > 12) {
            ramLog.remove(0);
        }
        ramLog.add(percent);
    }

    public double getMaxPercent() {
        return maxPercent;
    }
}
