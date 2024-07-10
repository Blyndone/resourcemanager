package osproject.hardware;

import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import osproject.DoubleObserver;

import java.util.ArrayList;
import java.util.List;

public class DiskMonitor implements HardwareMonitor {

    DoubleObserver diskLoadPercent;
    SystemInfo systemInfo = new SystemInfo();
    HardwareAbstractionLayer hardware = systemInfo.getHardware();
    HWDiskStore disk;

    private static ArrayList<Double> diskLog = new ArrayList<>();
    static double percent = 0;

    public DiskMonitor() {

    }

    public DiskMonitor(HWDiskStore disk) {
        this.disk = disk;
        System.out.println("Disk: " + disk.getName());
        System.out.println("Model: " + disk.getModel());
        System.out.println("Size: " + disk.getSize());
        System.out.println("Reads: " + disk.getReads());
        System.out.println("Writes: " + disk.getWrites());
        System.out.println("Transfer Time: " + disk.getTransferTime());

        System.out.println("Queue Length: " + disk.getCurrentQueueLength());
        System.out.println("Transfer Rate: " + disk.getTransferTime());
        System.out.println("Serial: " + disk.getSerial());

    }

    private static final double KB_TO_GB = 1.0 / (1024 * 1024); // Conversion factor from KB to GB

    public void monitorDisks() {

        // if (disks != null) {
        // for (HWDiskStore disk : disks) {
        // double usagePercent = getLoadPercent();
        // System.out.format("Disk: %s (%s)\n", disk.getName(), disk.getModel());
        // System.out.format(" Usage: %.1f%%\n", usagePercent);
        // System.out.println();

        // }
        // } else {
        // System.out.println("No disks found.");
        // }
    }

    public List<HWDiskStore> getDisks() {
        return hardware.getDiskStores();
    }

    @Override
    public Double getLoadPercent() {

        Thread diskThread = new Thread(() -> {
            long initialReadBytes = this.disk.getReadBytes();
            long initialWriteBytes = this.disk.getWriteBytes();

            try {

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.disk.updateAttributes();
            long finalReadBytes = this.disk.getReadBytes();
            long finalWriteBytes = this.disk.getWriteBytes();

            long readBytesChange = finalReadBytes - initialReadBytes;
            long writeBytesChange = finalWriteBytes - initialWriteBytes;

            // System.out.println("Disk: " + this.disk.getName());
            // System.out.println("Read Bytes Change: " + readBytesChange);
            // System.out.println("Write Bytes Change: " + writeBytesChange);

            double mbPerSecond = (readBytesChange + writeBytesChange) / 1048576.0; // Convert bytes change to MB
            percent = (mbPerSecond / 100.0) * 100; // Calculate as a percentage of 100 MB/s

        });
        diskThread.start();

        diskLoadPercent.setValue(percent);
        updateLog();

        return percent;

    }

    @Override
    public String getHardwareName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHardwareName'");
    }

    @Override
    public ArrayList<Double> getLog() {
        return diskLog;
    }

    @Override
    public void setObserver(DoubleObserver observer) {
        diskLoadPercent = observer;
    }

    public DoubleObserver getObserver() {
        return diskLoadPercent;
    }

    public void updateLog() {

        if (diskLog.size() > 12) {
            diskLog.remove(0);
        }
        diskLog.add(percent);
    }
}