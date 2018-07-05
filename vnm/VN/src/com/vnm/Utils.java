package com.vnm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by vnm on 17-2-14.
 */
public class Utils {
    public PhysicalGraph PG;
    public VirtualGraph []VG = new VirtualGraph[30];

    public String PGPath;
    public String VGPath;

    public double PGFreeCapacity[];
    public double PGFreeBandwidth[][];

    public double PGBandwidthMean = 50;//10w 250 w 50 k 10 
    public double PGBandwidthSquare = 1;

    public double PGCapacityMean = 5;//10w 25 w 5 k 1
    public double PGCapacitySquare = 0.1;

    public double VGCapacityMean = 0.01;
    public double VGCapacitySquare = 0.001;

    public double VGBandwidthMean = 0.1;
    public double VGBandwidthSquare = 0.01;

    public String getPGPath() {
        return PGPath;
    }

    public void setPGPath(String PGPath) {
        this.PGPath = PGPath;
    }

    public String getVGPath() {
        return VGPath;
    }

    public void setVGPath(String VGPath) {
        this.VGPath = VGPath;
    }

    public Utils(){
        PG = new PhysicalGraph();
        for (int i = 0; i < 20; i ++) {
             if(i==0)
                 VG[i] = new VirtualGraph();
             else
                 VG[i] = VG[0];
        }
    }
    public void ConstructPhysicalGraph(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(PGPath));
            String temp = reader.readLine();
            while(temp.contains("Nodes:") == false)
                temp = reader.readLine();
            String Nodestr[] = temp.split(" ");
            PG.Node = Integer.parseInt(Nodestr[2]);
            PG.NodeCapacity = new double[PG.Node];
            for (int i = 0; i < PG.Node; i ++){
                temp = reader.readLine();
                PG.NodeCapacity[i] = NormRandom(i,PGCapacityMean,PGCapacitySquare);
            }
            while(temp.contains("Edges:") == false)
                temp = reader.readLine();
            String Edgestr[] = temp.split(" ");
            PG.Edge = Integer.parseInt(Edgestr[2]);
            PG.EdgeCapacity = new double[PG.Node][PG.Node];
            for(int i = 0; i < PG.Node; i ++)
                Arrays.fill(PG.EdgeCapacity[i],-1);
            for(int i = 0; i < PG.Edge; i ++){
                temp = reader.readLine();
                String line[] = temp.split("\\t");
                int from = Integer.parseInt(line[1]);
                int to = Integer.parseInt(line[2]);
                double capcity = NormRandom(i,PGBandwidthMean,PGBandwidthSquare);
                PG.EdgeCapacity[from][to] = capcity;
                PG.EdgeCapacity[to][from] = capcity;
            }
            reader.close();
            //System.out.println(""+PG.Node);
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("read from brite error!");
        }
        finally {
            if(reader != null) {
                try{
                    reader.close();
                }catch (IOException e1){

                }
            }
        }
    }

    public void ConstructVirtualGraph(){
      for (int v = 0; v < 20; v ++) { 
        
        if(v!=0)
        { 
                System.out.println(" "+v);
                break;
        }else{
        BufferedReader reader = null;
        try {
            
            
            reader = new BufferedReader(new FileReader(VGPath));
            String temp = reader.readLine();
            while(temp.contains("Nodes:") == false)
                temp = reader.readLine();
            String Nodestr[] = temp.split(" ");
            VG[v].Node = Integer.parseInt(Nodestr[2]);
            VG[v].NodeCapacity = new double[VG[v].Node];
            for (int i = 0; i < VG[v].Node; i ++){
                temp = reader.readLine();
                VG[v].NodeCapacity[i] = NormRandom(i,VGCapacityMean,VGCapacitySquare);
            }
            while(temp.contains("Edges:") == false)
                temp = reader.readLine();
            String Edgestr[] = temp.split(" ");
            VG[v].Edge = Integer.parseInt(Edgestr[2]);
            VG[v].EdgeCapacity = new double[VG[v].Node][VG[v].Node];
            for(int i = 0; i < VG[v].Node; i ++)
                Arrays.fill(VG[v].EdgeCapacity[i],-1);
            for(int i = 0; i < VG[v].Edge; i ++){
                temp = reader.readLine();
                String line[] = temp.split("\\t");
                int from = Integer.parseInt(line[1]);
                int to = Integer.parseInt(line[2]);
                double capcity = NormRandom(i,VGBandwidthMean,VGBandwidthSquare);
                VG[v].EdgeCapacity[from][to] = capcity;
                VG[v].EdgeCapacity[to][from] = capcity;
            }
            reader.close();
            //System.out.println(""+PG.Node);
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("read from brite error!");
        }
        finally {
            if(reader != null) {
                try{
                    reader.close();
                }catch (IOException e1){

                }
            }
        }
        }
        }
    }

    public void ConstructVirtualGraph1(){
      for (int v = 0; v < 20; v ++) { 
        
        if(v!=0)
        { 
                System.out.println(" "+v);
                break;
        }else{
        BufferedReader reader = null;
        try {
            
            int ct = 0;
            reader = new BufferedReader(new FileReader("/home/sun/vnm/BRITE/VNet/dns-520.rs"));
            String temp = reader.readLine();
            //while(temp.contains("Nodes:") == false)
                //temp = reader.readLine();
            String Nodestr[] = temp.split(" ");
            System.out.println("node"+Nodestr[0]);
            VG[v].Node = Integer.parseInt(Nodestr[0]);
            VG[v].NodeCapacity = new double[VG[v].Node];
            VG[v].EdgeCapacity = new double[VG[v].Node][VG[v].Node];
            for(int i = 0; i < VG[v].Node; i ++)
                Arrays.fill(VG[v].EdgeCapacity[i],-1);
            for (int i = 0; i < VG[v].Node; i ++){
                temp = reader.readLine();
                String line[] = temp.split(" ");
                //System.out.println(Integer.parseInt(line[0]));
                VG[v].NodeCapacity[i] = Integer.parseInt(line[0]);
                VG[v].NodeCapacity[i] = VG[v].NodeCapacity[i]/1000;
                for (int k=1; k < line.length; k=k+2)
                {
                    //System.out.println(Integer.parseInt(line[k]));
                    VG[v].EdgeCapacity[Integer.parseInt(line[k])-1][i]=Integer.parseInt(line[k+1]);
                    VG[v].EdgeCapacity[i][Integer.parseInt(line[k])-1]=Integer.parseInt(line[k+1]);
                    VG[v].EdgeCapacity[Integer.parseInt(line[k])-1][i]=VG[v].EdgeCapacity[Integer.parseInt(line[k])-1][i]/100;
                    VG[v].EdgeCapacity[i][Integer.parseInt(line[k])-1]=VG[v].EdgeCapacity[i][Integer.parseInt(line[k])-1]/100;
                    ct=ct+1;
                }
            }
            //while(temp.contains("Edges:") == false)
                //temp = reader.readLine();
            //String Edgestr[] = temp.split(" ");
            VG[v].Edge = ct/2;
            //System.out.println(VG[v].Edge);
            
            //for(int i = 0; i < VG[v].Edge; i ++){
                //temp = reader.readLine();
                //String line[] = temp.split("\\t");
                //int from = Integer.parseInt(line[1]);
                //int to = Integer.parseInt(line[2]);
                //double capcity = NormRandom(i,VGBandwidthMean,VGBandwidthSquare);
                //VG[v].EdgeCapacity[from][to] = capcity;
                //VG[v].EdgeCapacity[to][from] = capcity;
            //}
            reader.close();
            //System.out.println(""+PG.Node);
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("read from brite error!");
        }
        finally {
            if(reader != null) {
                try{
                    reader.close();
                }catch (IOException e1){

                }
            }
        }
        }
        }
    }

    public double NormRandom(int i, double mean, double square){
        // Random random = new Random();
        // double rand = Math.sqrt(square)* random.nextGaussian()+mean;
        // while(rand <= 0 || rand < mean - 2 * Math.sqrt(square) || rand > mean + 2 * Math.sqrt(square))
        //     rand = Math.sqrt(square)* random.nextGaussian()+mean;
        double rand = 0;
        if (i % 10 == 0) {
            rand = mean;
        }
        if (i % 10 == 1) {
            rand = mean + Math.sqrt(square);
        }
        if (i % 10 == 2) {
            rand = mean - Math.sqrt(square);
        }
        if (i % 10 == 3) {
            rand = mean + 2 * Math.sqrt(square);
        }
        if (i % 10 == 4) {
            rand = mean - 2 * Math.sqrt(square);
        }
        if (i % 10 == 5) {
            rand = mean - 3 * Math.sqrt(square);
        }
        if (i % 10 == 6) {
            rand = mean + 3 * Math.sqrt(square);
        }
        if (i % 10 == 7) {
            rand = mean - 2.5 * Math.sqrt(square);
        }
        if (i % 10 == 8) {
            rand = mean + 2.5 * Math.sqrt(square);
        }
        if (i % 10 == 9) {
            rand = mean - 1.5 * Math.sqrt(square);
        }
        return rand;
    }

    public static void main(String[] args) {
        Utils U = new Utils();
        U.VGPath = "/home/sun/t1.part.3.rs";
        U.ConstructVirtualGraph1();
    }
}
