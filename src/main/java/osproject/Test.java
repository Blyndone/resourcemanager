package osproject;

import osproject.hardware.CpuMonitor;

public class Test {
    public static void main(String[] args) {

        CpuMonitor cpuMonitor = new CpuMonitor();
        cpuMonitor.getLoadPercent();
    }
}
