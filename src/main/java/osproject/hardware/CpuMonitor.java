package osproject.hardware;

import java.util.ArrayList;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import osproject.DoubleObserver;

public class CpuMonitor implements HardwareMonitor {

    DoubleObserver cpuLoadPercent;

    SystemInfo si = new SystemInfo();

    HardwareAbstractionLayer hal = si.getHardware();

    CentralProcessor processor = hal.getProcessor();

    private static ArrayList<Double> cpuLog = new ArrayList<>();

    static double percent = 0;
    static int delay = 1000;
    double maxPercent = 100;
    int logSize = 30;
    String chartColor = "ff4500";

    @Override
    public Double getLoadPercent() {

        new Thread(() -> {
            percent = processor.getSystemCpuLoad(delay) * 100;
        }).start();

        cpuLoadPercent.setValue(percent);
        updateLog();
        return percent;
    }

    @Override
    public String getHardwareName() {

        return processor.getProcessorIdentifier().getName();
    }

    public DoubleObserver getObserver() {
        return cpuLoadPercent;
    }

    public void setObserver(DoubleObserver observer) {
        cpuLoadPercent = observer;
    }

    public ArrayList<Double> getLog() {
        return cpuLog;
    }

    public void updateLog() {

        if (cpuLog.size() > logSize) {
            cpuLog.remove(0);
        }
        cpuLog.add(percent);
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
