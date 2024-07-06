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

    @Override
    public Double getLoadPercent() {

        new Thread(() -> {
            percent = processor.getSystemCpuLoad(delay) * 100;
            // System.out.println("CPU Load: " + percent + "%");
        }).start();

        cpuLoadPercent.setValue(percent);
        updateLog();
        return percent;
    }

    @Override
    public String getHardwareName() {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHardwareName'");
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

        if (cpuLog.size() > 12) {
            cpuLog.remove(0);
        }
        cpuLog.add(percent);
    }

}
