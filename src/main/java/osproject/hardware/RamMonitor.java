package osproject.hardware;

import java.util.ArrayList;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;
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
    int logSize = 30;
    String chartColor = "5be24e";

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
        long totalCapacity = 0;
        for (PhysicalMemory memory : hardware.getMemory().getPhysicalMemory()) {
            totalCapacity += memory.getCapacity();
        }
        return hardware.getMemory().getPhysicalMemory().get(0).getMemoryType() + " " + formatBytes(totalCapacity);
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

        if (ramLog.size() > logSize) {
            ramLog.remove(0);
        }
        ramLog.add(percent);
    }

    public double getMaxPercent() {
        return maxPercent;
    }

    public String getChartColor() {
        return chartColor;

    }

    public void setChartColor(String color) {
        this.chartColor = color;
    }
}
