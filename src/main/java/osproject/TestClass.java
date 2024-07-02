package osproject;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

public class TestClass {

    private static OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();

    public static void status() {

        System.out.println("Current Process load is: " + bean.getProcessCpuLoad());
        System.out.println("Current CPU load is: " + bean.getSystemCpuLoad());
        System.out.println("CPU Processing time is: " + bean.getProcessCpuTime());
        System.out.println("Process CPU Load is: " + bean.getProcessCpuLoad());
        // System.out.println("Total machine ram is: "+bean.getTotalMemorySize()+" or about 8 GB");
        // System.out.println("Current free memory load is: "+bean.getFreeMemorySize());

    }
    
    public String getProcessCpuLoad(){
        Double load = bean.getProcessCpuLoad();
        load = load * 100;

        return String.format("%.1f%%", load);
    }
}