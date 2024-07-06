package osproject.hardware;

import java.util.ArrayList;

import osproject.DoubleObserver;

public interface HardwareMonitor {

    Double getLoadPercent();

    String getHardwareName();

    ArrayList<Double> getLog();

    void setObserver(DoubleObserver observer);

}