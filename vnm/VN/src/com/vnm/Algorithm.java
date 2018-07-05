package com.vnm;

import java.io.*;

/**
 * Created by lx on 17-3-1.
 */
public class Algorithm {
    public double BDcost;               // the successed mapped link cost * dist
    public double BDfailcost;           // the unsuccessed mapped link cost

    public double linkcost;
    public double VNmapped;
    public double Vlinkmapped;
    public double Vlinksum;

    public Algorithm(){
        BDcost = 0;
        BDfailcost = 0;
        linkcost = 0;
        VNmapped = 0;
        Vlinkmapped = 0;
        Vlinksum = 0;
    }

    public void Deploy(int v_flag, String log){
        
    }

    public void RestructVN(int v_flag, String path){

    }

    public double AddVNlog(int v_flag){
        return -1;
    }

    public void KillLiveVN(String path){

    }
    public int PNodeUsed(){
        int res = 0;
        return res;
    }

    public int MaxPathLength(int v_flag){
        int res = 0;
        return res;
    }

    // for test
    public static void main(String[] args) throws IOException {
        
    }
}
