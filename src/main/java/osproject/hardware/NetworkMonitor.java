package osproject.hardware;

import java.util.ArrayList;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;
import osproject.DoubleObserver;

public class NetworkMonitor implements HardwareMonitor {

    @Override
    public Double getLoadPercent() {
        SystemInfo systemInfo = new SystemInfo();
        NetworkIF[] networkIFs = systemInfo.getHardware().getNetworkIFs();

        if (networkIFs != null) {
            for (NetworkIF net : networkIFs) {
                System.out.println("Name: " + net.getName());
                System.out.println("  MAC Address: " + net.getMacaddr());
                System.out.println("  IPv4 Address: " + net.getIPv4addr());
                System.out.println("  IPv6 Address: " + net.getIPv6addr());
                System.out.println("  Bytes Sent: " + net.getBytesSent());
                System.out.println("  Bytes Received: " + net.getBytesRecv());
                System.out.println("  Packets Sent: " + net.getPacketsSent());
                System.out.println("  Packets Received: " + net.getPacketsRecv());
                System.out.println();
            }
        } else {
            System.out.println("No network interfaces found.");
        }
    }

    @Override
    public String getHardwareName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHardwareName'");
    }

    @Override
    public ArrayList<Double> getLog() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLog'");
    }

    @Override
    public void setObserver(DoubleObserver observer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setObserver'");
    }

}
