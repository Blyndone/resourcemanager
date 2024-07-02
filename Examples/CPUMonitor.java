import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class CPUMonitor {

    public static void main(String[] args) {
        Sigar sigar = new Sigar();
        
        try {
            CpuInfo[] cpuInfoList = sigar.getCpuInfoList();
            
            for (CpuInfo info : cpuInfoList) {
                System.out.println("CPU Information:");
                System.out.println("Vendor: " + info.getVendor());
                System.out.println("Model: " + info.getModel());
                System.out.println("Mhz: " + info.getMhz());
                System.out.println("Total CPUs: " + info.getTotalCores());
                System.out.println("Idle time: " + sigar.getCpu().getIdle());
                System.out.println("User time: " + sigar.getCpu().getUser());
                System.out.println("Sys time: " + sigar.getCpu().getSys());
                System.out.println();
            }
            
        } catch (SigarException e) {
            e.printStackTrace();
        }
    }
}