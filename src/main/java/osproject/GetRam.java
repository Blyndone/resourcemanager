package osproject;

import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;


import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class GetRam {

    private static Sigar sigar = new Sigar();

    public static void getInformationAboutMemory() {
        System.out.println("**************************************");
        System.out.println("*** Current RAM Status: ***");
        System.out.println("**************************************\n");

        Mem mem = null;
        try {
            mem = sigar.getMem();
        } catch (SigarException se) {
            se.printStackTrace();
        }

        System.out.println("Actual total free system memory: "
                + mem.getActualFree() / 1024 / 1024+ " MB");
        System.out.println("Actual total used system memory: "
                + mem.getActualUsed() / 1024 / 1024 + " MB");
        System.out.println("Total free system memory ......: " + mem.getFree()
                / 1024 / 1024+ " MB");
        System.out.println("System Random Access Memory....: " + mem.getRam()
                + " MB");
        System.out.println("Total system memory............: " + mem.getTotal()
                / 1024 / 1024+ " MB");
        System.out.println("Total used system memory.......: " + mem.getUsed()
                / 1024 / 1024+ " MB");

        System.out.println("\n**************************************\n");


    }

    public static void main(String[] args) throws Exception{
String j2dD3d = System.getProperty("sun.java2d.d3d");
        System.out.println("J2D_D3D is set to: " + j2dD3d);
        
                getInformationAboutMemory();

                }

}