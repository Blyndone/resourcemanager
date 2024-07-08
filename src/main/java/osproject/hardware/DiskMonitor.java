package osproject.hardware;

import java.util.ArrayList;

import oshi.SystemInfo;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import osproject.DoubleObserver;


public class DiskMonitor implements HardwareMonitor {

    DoubleObserver diskLoadPercent;
    SystemInfo si = new SystemInfo();
    private static ArrayList<Double> diskLog = new ArrayList<>();
    static double percent = 0;
    private final OSFileStore fileStore;

    public DiskMonitor() {
        OperatingSystem os = si.getOperatingSystem();
        this.fileStore = os.getFileSystem().getFileStores().get(0);
    }

    @Override
    public Double getLoadPercent() {
        long totalSpace = fileStore.getTotalSpace();
        long usableSpace = fileStore.getUsableSpace();
        double usedSpace = totalSpace - usableSpace;

        percent = (double) usedSpace / totalSpace * 100;
        updateLog();
        diskLoadPercent.setValue(percent);
        return percent;
    }

    @Override
    public String getHardwareName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHardwareName'");
    }

    public DoubleObserver getObserver() {
        return diskLoadPercent;
    }

    public void setObserver(DoubleObserver observer) {
        diskLoadPercent = observer;
    }

    public ArrayList<Double> getLog() {
        return diskLog;
    }

    public void updateLog() {

        if (diskLog.size() > 12) {
            diskLog.remove(0);
        }
        diskLog.add(percent);
    }

}
