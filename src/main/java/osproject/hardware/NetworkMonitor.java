package osproject.hardware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import oshi.SystemInfo;

import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import osproject.DoubleObserver;

public class NetworkMonitor implements HardwareMonitor {

    DoubleObserver networkLoadPercent;

    SystemInfo si = new SystemInfo();
    HardwareAbstractionLayer hal = si.getHardware();
    List<NetworkIF> networkIF = hal.getNetworkIFs();

    private static ArrayList<Double> networkLog = new ArrayList<>();

    static double percent = 0;
    double maxPercent = 100;
    int logSize = 30;
    String chartColor = "00bfff";

    @Override
    public Double getLoadPercent() {

        for (NetworkIF net : networkIF) {
            if (net.getIfOperStatus().toString().equals("UP")) {
                // Initial bytes sent and received

                new Thread(() -> {
                    long initialBytesSent = net.getBytesSent();
                    long initialBytesRecv = net.getBytesRecv();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    long newBytesSent = net.getBytesSent();
                    long newBytesRecv = net.getBytesRecv();

                    long sentDiff = newBytesSent - initialBytesSent;
                    long recvDiff = newBytesRecv - initialBytesRecv;

                    long totalBytes = sentDiff + recvDiff;
                    long speed = net.getSpeed();
                    percent = ((double) totalBytes * 8 / speed) * 100;
                    maxPercent = Math.max((Collections.max(networkLog) + 10), 100);
                }).start();

                net.updateAttributes();

            }
        }
        // System.out.println("Network Load: " + percent + "%");
        networkLoadPercent.setValue(percent);
        updateLog();
        return percent;

    }

    @Override
    public String getHardwareName() {
        StringBuilder hardwareNames = new StringBuilder();
        for (NetworkIF net : networkIF) {
            hardwareNames.append(net.getDisplayName()).append(", ");
        }
        // Remove the trailing comma and space
        if (hardwareNames.length() > 0) {
            hardwareNames.setLength(hardwareNames.length() - 2);
        }
        return hardwareNames.toString();
    }

    public DoubleObserver getObserver() {
        return networkLoadPercent;
    }

    public void setObserver(DoubleObserver observer) {
        networkLoadPercent = observer;
    }

    public ArrayList<Double> getLog() {
        return networkLog;
    }

    public void updateLog() {

        if (networkLog.size() > logSize) {
            networkLog.remove(0);
        }
        networkLog.add(percent);
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
