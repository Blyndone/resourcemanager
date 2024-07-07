package osproject.hardware;

import java.util.ArrayList;
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

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHardwareName'");
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

        if (networkLog.size() > 12) {
            networkLog.remove(0);
        }
        networkLog.add(percent);
    }

}
