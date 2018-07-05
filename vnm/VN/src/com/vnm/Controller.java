package com.vnm;

import java.io.*;
import java.util.Scanner;

/**
 * Created by vnm on 17-3-1.
 */
public class Controller {
    public ReqGenerator RG;
    public double revenue = 0;
    public String ResultParent = "/home/sun/vnm/VN/Result/";
    public Algorithm algorithm;
    public Controller(ReqGenerator RG) {
        this.RG = RG;
    }
   
    public void func(Utils utils){
        utils.setPGPath((RG.PGfolderpath+"/" + RG.PGfile));
        utils.ConstructPhysicalGraph();
        utils.setVGPath((RG.VGfolderpath+"/" + RG.VGfile));
        utils.ConstructVirtualGraph1();
        this.algorithm = new NMAlgorithm1(utils);
    }

    public double[] Mapping(Utils utils,int op, String Resutlt) throws FileNotFoundException {
        String ResultPath = Resutlt;
        //double revenue = 0;
        double capacity1 = 0;
        File file = new File(RG.OutputRGresult);
        Scanner scanner = new Scanner(file);
        String line;
        if(scanner.hasNextLine())
            line = scanner.nextLine();
        else {
            System.out.println("when read from outputRGresult, it is null");
            return new double[4];
        }
        String[] linearray = line.split(" ");
        
        //utils.setVGPath((RG.VGfolderpath+"/" + RG.VGfile));
        //utils.ConstructVirtualGraph1();
        this.algorithm.Deploy(op,ResultPath);
        revenue +=this.algorithm.AddVNlog(op);
        for (int i = 0; i < utils.VG[op].Node; i ++) {
            capacity1 += utils.VG[op].NodeCapacity[i];
        }
        double res[] = new double[4];
        res[0] += (algorithm.BDcost + algorithm.BDfailcost * 10) + capacity1 * (utils.VGBandwidthMean / utils.VGCapacityMean);
        res[1] += revenue;
        res[2] = algorithm.VNmapped;
        res[3] = algorithm.linkcost;
        return res;
    }

    public static void main(String[] args){
        int PGpara = Integer.parseInt(args[0]);
        int aCase = Integer.parseInt(args[1]);
        int maxVnetNum = Integer.parseInt(args[2]);
        int LimitVnet = 2;
        String PGfile = PGpara + ".brite";
        String VGfile = aCase + ".brite";
        String PGfolderpath = "/home/sun/vnm/BRITE/PNet";
        String VGfolderpath = "/home/sun/vnm/BRITE/VNet";
        String outputRGresult = "/home/sun/vnm/VN/RGoutput/RGresult1.txt";
	    double res[] = new double[2];
        ReqGenerator RG = new ReqGenerator(aCase,maxVnetNum,LimitVnet, VGfile,PGfile,PGfolderpath,VGfolderpath,outputRGresult);
        //ReqGenerator RG = new ReqGenerator(aCase,maxVnetNum,LimitVnet,PGfile,PGfolderpath,VGfolderpath,outputRGresult);
        RG.Generate();
        Controller controller = new Controller(RG);

       Utils utils = new Utils();

        controller.func(utils);
        for(int i = 0; i <20; i ++){
            String ResultFile;
            PrintWriter out = null;

            long StartTime = System.nanoTime();
            ResultFile = controller.ResultParent + "ResultWithOp1.txt";
            FileWriter file = null;
            try {
                file = new FileWriter(ResultFile,true);
                out = new PrintWriter(file);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                out.close();
                try {
                    file.close();
                } catch (IOException e) {
                    System.out.println("close file error in ADD title to result");
                    e.printStackTrace();
                }
            }
            try {
                System.out.println("times:"+i);
                res = controller.Mapping(utils,i,ResultFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            long EndTime = System.nanoTime();
            double ExecuteTime = (EndTime - StartTime) / Math.pow(10,9);
            try {
                file = new FileWriter(ResultFile,true);
                out = new PrintWriter(file);
                out.println();
            	out.println(PGpara + " ？ "+ maxVnetNum + " ？  " + res[1] + "  ？ " + res[0] + " ？ "+ res[3] + " * " + res[2] + " ？ " + (res[1]/res[0]));
	    }catch (Exception e){
                System.out.println("Add ExecutionTime occur a Exception");
                e.printStackTrace();
            }finally {
                out.close();
                try {
                    file.close();
                } catch (IOException e) {
                    System.out.println("close file error in ADD ExecuteTime, revenue to result");
                    e.printStackTrace();
                }
            }
        }

    }
}
