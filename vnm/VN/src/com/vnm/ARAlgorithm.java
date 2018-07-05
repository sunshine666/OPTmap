 /*
Node-Merge Algorithm
*/
package com.vnm;

import com.vnm.Pair;
import com.vnm.Utils;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by vnm on 17-7-18.
 */
public class ARAlgorithm extends Algorithm{
    public Utils utils;
    public double PGFreeCapacity[];
    public double PGFreeBandwidth[][];
    public double CV[];
    public int kill = 0;
    public final double LimitRatio = 0.001;
    public int falsevlinkmapped = 0;
    //public int Indexofhost = 0;

    public ARAlgorithm(Utils utils) {
        this.utils = utils;
        System.out.println("!!!!");
        PGFreeCapacity = new double[utils.PG.Node];
        PGFreeBandwidth = new double[utils.PG.Node][utils.PG.Node];
        for(int i = 0; i < utils.PG.Node; i ++){
           PGFreeCapacity[i] = utils.PG.NodeCapacity[i];
           //System.out.println("PGFreeCapacity["+i+"]="+PGFreeCapacity[i]);
            for(int j = 0; j < utils.PG.Node; j++){
                PGFreeBandwidth[i][j] = utils.PG.EdgeCapacity[i][j];
                //System.out.println("PGFreeBandwidth["+i+"]["+j+"]="+PGFreeBandwidth[i][j]);
            }
                
        }
        }

    // public void func(int v_flag) {
        
    // }
    

   public void Deploy(int vs,String log){
        utils.VG[vs].VN2PN = new int[utils.VG[vs].Node];
        Arrays.fill(utils.VG[vs].VN2PN,-1);
        utils.VG[vs].VEBandwidth = new double[utils.VG[vs].Edge];
        Arrays.fill(utils.VG[vs].VEBandwidth,0);
        utils.VG[vs].VE2PE = new List[utils.VG[vs].Edge];
        for(int i = 0; i < utils.VG[vs].Edge; i ++){
            utils.VG[vs].VE2PE[i] = new ArrayList<Integer>();
        }

        ReleaseNodeCapacity(vs,PGFreeCapacity);
        ReleaseEdgeCapacity(vs,PGFreeBandwidth);
        // for(int i = 0; i < utils.PG.Node; i ++){
        //    PGFreeCapacity[i] = utils.PG.NodeCapacity[i];
        //   // System.out.println("PGFreeCapacity["+i+"]="+PGFreeCapacity[i]);
        //     for(int j = 0; j < utils.PG.Node; j++){
        //         PGFreeBandwidth[i][j] = utils.PG.EdgeCapacity[i][j];
        //     //    System.out.println("PGFreeBandwidth["+i+"]["+j+"]="+PGFreeBandwidth[i][j]);
        //     }
                
        // }

        // sort PG Available Resources, with two parameter(cap, id)
        List<Pair> PGLeftCap = new ArrayList<>();
        for(int i = 0; i < utils.PG.Node; i ++){
            double TotalBandwidth = 0;
            double TotalCapacity = PGFreeCapacity[i];
            for(int j = 0; j < utils.PG.Node; j ++){
                if(PGFreeBandwidth[i][j] > 0)
                    TotalBandwidth += PGFreeBandwidth[i][j];
            }
            double AvailableResources = TotalBandwidth * TotalCapacity;
            PGLeftCap.add(new Pair(AvailableResources,i));
        }
        PGLeftCap.sort(Pair.comparator);

        // sort VG Available Resources.
        List<Pair> VGCap = new ArrayList<>();
        for(int i = 0; i < utils.VG[vs].Node; i ++){
            double TotalBandwidth = 0;
            double TotalCapacity = utils.VG[vs].NodeCapacity[i];
            for(int j = 0; j < utils.VG[vs].Node; j ++){
                if(utils.VG[vs].EdgeCapacity[i][j] > 0)
                    TotalBandwidth += utils.VG[vs].EdgeCapacity[i][j];
            }
            double AvailableResources = TotalBandwidth * TotalCapacity;
            VGCap.add(new Pair(AvailableResources,i));
        }
        VGCap.sort(Pair.comparator);

        //high cap V to high cap P
        int Indexofhost = 0;
        for(int i = 0; i < utils.VG[vs].Node; i ++){
            int VNode = VGCap.get(i).Id;
            double VCapacity = utils.VG[vs].NodeCapacity[VNode];
            int PNode = PGLeftCap.get(Indexofhost).Id;
            double PCapacity = PGFreeCapacity[PNode];

            while(!CheckPNodeAR(PNode) || PCapacity - VCapacity < 0) {
                Indexofhost++;
                PNode = PGLeftCap.get(Indexofhost).Id;
                PCapacity = PGFreeCapacity[PNode];
            }
            PGFreeCapacity[PNode] -= VCapacity;
            utils.VG[vs].VN2PN[VNode] = PNode;
        }
        for (int i = 0; i < utils.PG.Node; i++) {
            if (PGFreeCapacity[PGLeftCap.get(i).Id] < 0) {
                return;
            }
        }

        // deploy VEdgesi
        int falsemapped = 0;
        utils.VG[vs].VEindex = 0;
        //int falsevlinkmapped = 0;
        for(int i = 0; i < utils.VG[vs].Node; i ++){
            for(int j = i; j < utils.VG[vs].Node; j ++){
                if (utils.VG[vs].EdgeCapacity[i][j] > 0){
                    boolean res = DeployVPath(vs,utils.VG[vs].VN2PN[i], utils.VG[vs].VN2PN[j],utils.VG[vs].EdgeCapacity[i][j],utils.VG[vs].VEindex);
                    if (res == false){
                        BDfailcost += utils.VG[vs].EdgeCapacity[i][j];
                        falsevlinkmapped ++;
                        falsemapped ++;
                        System.out.println("Deploy Virtual Edge " + i + " " + j + "Not Successed!");
                    }
                    else {
                        BDcost += utils.VG[vs].EdgeCapacity[i][j] * utils.VG[vs].VE2PE[utils.VG[vs].VEindex].size();
                        //System.out.println("Deploy Virtual Edge " + i + " " + j + "Successed!!!!!!!!!!!!!!!!!!!!");
                    }
                    utils.VG[vs].VEindex ++;
                }
            }
        }
        if(falsemapped == 0){
            VNmapped ++;
            Vlinkmapped += utils.VG[vs].Edge;
            Vlinksum += utils.VG[vs].Edge;
        }
        else{
            Vlinkmapped += (utils.VG[vs].Edge - falsemapped);
            Vlinksum += utils.VG[vs].Edge;
        }

        // log the cost of deploy
        int Pnodeused = PNodeUsed();
        double PNodeUsedCapacity = PNodeUsedCapacity();
        double VNodeCapacity = VNodeCapacity(vs);
        double UseRatio = VNodeCapacity / PNodeUsedCapacity;
        String ResultFile = log;
        PrintWriter out = null;

        FileWriter file = null;
        try {
            file = new FileWriter(ResultFile,true);
            out = new PrintWriter(file);
            //out.println(Pnodeused + "    " +PNodeUsedCapacity+ "   " + VNodeCapacity + "   " + UseRatio);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            out.close();
            try {
                file.close();
            } catch (IOException e) {
                System.out.println("close file error in ADD Pnode cost to result");
                e.printStackTrace();
            }
        }
    }

    public void ReleaseNodeCapacity(int v_flag,double PGFreeCapacity[]){
        if (v_flag > 20) {
            for (int i = 0; i < utils.VG[v_flag - 20].Node; i ++) {
                PGFreeCapacity[utils.VG[v_flag - 20].VN2PN[i]] += utils.VG[v_flag - 20].NodeCapacity[i];
            }
        }
        
    }

    public void ReleaseEdgeCapacity(int v_flag,double PGFreeBandwidth[][]){
        if (v_flag > 20) {
            for (int i = 0; i < utils.VG[v_flag - 20].VEindex ; i ++) {
                for (int j = 0; j < utils.VG[v_flag - 20].VE2PE[i].size() - 1; j ++) {
                    int a = (Integer) utils.VG[v_flag - 20].VE2PE[i].get(j);
                    int b = (Integer) utils.VG[v_flag - 20].VE2PE[i].get(j + 1);
                    PGFreeBandwidth[a][b] += utils.VG[v_flag-20].VEBandwidth[i];
                    PGFreeBandwidth[b][a] += utils.VG[v_flag-20].VEBandwidth[i];
                }
            }
        }
    }

// public void ReleaseNodeCapacity(int v_flag,double PGFreeCapacity[]){

//         if (v_flag ==1) {
//             for (int i = 0; i < utils.VG[0].Node; i ++) {
//                 //System.out.println("开始释放。。。。。。。");
//                 //System.out.println("---------"+PGFreeCapacity[utils.VG[v_flag - 20].VN2PN[i]]);
//                 PGFreeCapacity[utils.VG[0].VN2PN[i]] = PGFreeCapacity[utils.VG[0].VN2PN[i]]+utils.VG[0].NodeCapacity[i];
//                 //System.out.println("+++++++++"+PGFreeCapacity[utils.VG[v_flag - 20].VN2PN[i]]);
//             }
//         }
//     }

//     public void ReleaseEdgeCapacity(int v_flag,double PGFreeBandwidth[][]){
//         if (v_flag ==1) {
//             for (int i = 0; i < utils.VG[0].VEindex ; i ++) {
//                 for (int j = 0; j < utils.VG[0].VE2PE[i].size() - 1; j ++) {
//                     int a = (Integer) utils.VG[0].VE2PE[i].get(j);
//                     int b = (Integer) utils.VG[0].VE2PE[i].get(j + 1);
                    
//                     PGFreeBandwidth[a][b] += utils.VG[0].VEBandwidth[i];
//                     PGFreeBandwidth[b][a] += utils.VG[0].VEBandwidth[i];
//                 }
//             }
//         }
//     }
    public boolean DeployVPath(int v_flag,int From, int To, double Bandwidth, int VEindex){
        if(From == To)
            return true;

        boolean Successed = true;
        int infi = 9999;
        int Dist[] = new int[utils.PG.Node];
        int Path[] = new int[utils.PG.Node];
        Arrays.fill(Path,-1);

        boolean Selected[] = new boolean[utils.PG.Node];
        Dist[From] = 0;
        Selected[From] = true;
        for(int i = 0; i < utils.PG.Node; i++){
            if (i == From)
                continue;
            if(PGFreeBandwidth[From][i] >= Bandwidth)
                Dist[i] = 1;
            else
                Dist[i] = infi;
        }
        for(int i = 0; i < utils.PG.Node - 1; i ++){
            int minnode = -1;
            double mindistance = infi;
            if(Dist[To] < infi){
                break;
            }
            for(int j = 0; j < utils.PG.Node; j ++){
                if(Selected[j] == false && Dist[j] < mindistance){
                    minnode = j;
                    mindistance = Dist[j];
                }
            }
            if(minnode == -1){
                System.out.println("Have Search " + i + "  i, cant find path");
                return false;
            }
            Selected[minnode] = true;
            for(int j = 0; j < utils.PG.Node; j ++){
                if(Selected[j] == false && PGFreeBandwidth[minnode][j] >= Bandwidth){
                    if(Dist[minnode] + 1 < Dist[j]) {
                        Dist[j] = Dist[minnode] + 1;
                        Path[j] = minnode;
                    }
                }
            }
        }

        List pathnode = new ArrayList<Integer>();
        int temp = To;
        while(Path[temp] != -1){
            temp = Path[temp];
            pathnode.add(temp);
        }

        utils.VG[v_flag].VEBandwidth[VEindex] = Bandwidth;
        utils.VG[v_flag].VE2PE[VEindex].add(From);
        for(int i = 0; i < pathnode.size(); i ++){
            utils.VG[v_flag].VE2PE[VEindex].add(pathnode.get(pathnode.size()-i-1));
        }
        utils.VG[v_flag].VE2PE[VEindex].add(To);
        if (!CheckPathBD(utils.VG[v_flag].VE2PE[VEindex],Bandwidth)) {
            utils.VG[v_flag].VE2PE[VEindex].clear();
            return false;
        }
        for(int i = 0; i < utils.VG[v_flag].VE2PE[VEindex].size() - 1; i ++){
            int sour = (Integer) utils.VG[v_flag].VE2PE[VEindex].get(i);
            int des = (Integer) utils.VG[v_flag].VE2PE[VEindex].get(i + 1);
            PGFreeBandwidth[sour][des] -= Bandwidth;
            PGFreeBandwidth[des][sour] -= Bandwidth;
            linkcost += Bandwidth;
        }
        return Successed;
    }

    // judge if a Pnode freeresource is satisfied
    public boolean CheckPNodeAR(int PNode){
        boolean Successed = true;
        double FreeBD = 0;
        double BD = 0;
        if(PGFreeCapacity[PNode]/utils.PG.NodeCapacity[PNode] < LimitRatio)
            return false;
        for(int i = 0; i < utils.PG.Node; i ++){
            if(utils.PG.EdgeCapacity[PNode][i] > 0){
                BD += utils.PG.EdgeCapacity[PNode][i];
                FreeBD += PGFreeBandwidth[PNode][i];
            }
        }
        if(FreeBD/BD < LimitRatio)
            Successed = false;
        return Successed;
    }

    // check if  a  path sufficate bandwidth limit
    public boolean CheckPathBD(List Path, double bandwidth){
        if(Path.size() == 0)
            return true;
        for(int i = 0; i < Path.size() - 1; i ++){
            int from = (Integer) Path.get(i);
            int to = (Integer) Path.get(i+1);
            if(PGFreeBandwidth[from][to] < bandwidth)
                return false;
        }
        return true;
    }

    public void RestructVN(int v_flag,String path){
        utils.setVGPath(path);
        utils.ConstructVirtualGraph();
        utils.VG[v_flag].VN2PN = new int[utils.VG[v_flag].Node];
        Arrays.fill(utils.VG[v_flag].VN2PN,-1);
        utils.VG[v_flag].VEBandwidth = new double[utils.VG[v_flag].Edge];
        Arrays.fill(utils.VG[v_flag].VEBandwidth,0);
        utils.VG[v_flag].VE2PE = new List[utils.VG[v_flag].Edge];
        for(int i = 0; i < utils.VG[v_flag].Edge; i ++){
            utils.VG[v_flag].VE2PE[i] = new ArrayList<Integer>();
        }
    }

    public double AddVNlog(int v_flag){
        double RevenueRatio = utils.VGBandwidthMean / utils.VGCapacityMean;
        double Capacityrevenue = 0;
        double Bandwidthrevenue = 0;
        String path = "/home/sun/vnm/VN/VNlog/"+utils.VG[v_flag].Node+".brite";
        File file;
        PrintWriter out = null;
        try{
            file = new File(path);
            if(!file.exists()){
                try {
                    file.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            out = new PrintWriter(file);
            out.println("VN2PN  VN  PN  VNcapacity");
            for(int i = 0; i < utils.VG[v_flag].Node; i ++){
                // VN i didn't map
                if(utils.VG[v_flag].VN2PN[i] == -1)
                    continue;
                Capacityrevenue += utils.VG[v_flag].NodeCapacity[i];
                out.println(i + " " + utils.VG[v_flag].VN2PN[i] + " " + utils.VG[v_flag].NodeCapacity[i]);
            }
            out.println("VE2PE from to Vbandwidth");
            for(int i = 0; i < utils.VG[v_flag].VE2PE.length; i++){
                // VEdge i didn't map
                if(utils.VG[v_flag].VE2PE[i].size() == 0)
                    continue;
                for(int j = 0; j < utils.VG[v_flag].VE2PE[i].size(); j++){
                    out.print(utils.VG[v_flag].VE2PE[i].get(j)+" ");
                }
                Bandwidthrevenue += utils.VG[v_flag].VEBandwidth[i] ;
                out.println(utils.VG[v_flag].VEBandwidth[i]);
            }
        }catch (Exception e){
            System.out.println("in addVNlog there is a exception");
            e.printStackTrace();
        }finally {
            out.close();
        }
        Bandwidthrevenue += RevenueHideinPnode(v_flag);
        if(falsevlinkmapped == 0)
            return Capacityrevenue * RevenueRatio + Bandwidthrevenue;
        else
            return 0;
    }

    public void KillLiveVN(String path){
        File file;
        Scanner scanner = null;
        try {
            file = new File(path);
            scanner = new Scanner(file);
            if(!scanner.hasNextLine()){
                System.out.println("kill Vn read a empty log  file!");
                return;
            }
            String line = scanner.nextLine();
            String linearray[] = line.split(" +");
            while(scanner.hasNextLine()){
                line = scanner.nextLine();
                linearray = line.split(" +");
                if(linearray[0].equals("VE2PE"))
                    break;
                int pnode = Integer.parseInt(linearray[1]);
                double freebandwidth = Double.parseDouble(linearray[2]);
                PGFreeCapacity[pnode] += freebandwidth;
            }
            while(scanner.hasNextLine()){
                line = scanner.nextLine();
                linearray = line.split(" +");
                double freebandwidth = Double.parseDouble(linearray[linearray.length-1]);
                for(int i = 0; i < linearray.length - 2; i ++){
                    int from = Integer.parseInt(linearray[i]);
                    int to = Integer.parseInt(linearray[i + 1]);
                    PGFreeBandwidth[from][to] += freebandwidth;
                    PGFreeBandwidth[to][from] += freebandwidth;
                }
            }

        }catch (Exception e){
            System.out.println("int kill live Vn, there is a exception");
            e.printStackTrace();
        }finally {
            scanner.close();
        }
    }

    // add  bandwidth revenue from same node
    public double RevenueHideinPnode(int v_flag){
        double HidenRevenue = 0;
        for(int i = 0; i < utils.VG[v_flag].Node; i ++){
            for(int j = i+1; j < utils.VG[v_flag].Node; j ++){
                if(utils.VG[v_flag].EdgeCapacity[i][j] > 0 && utils.VG[v_flag].VN2PN[i] == utils.VG[v_flag].VN2PN[j] && utils.VG[v_flag].VN2PN[i] != -1)
                    HidenRevenue += utils.VG[v_flag].EdgeCapacity[i][j] * 1.4;
            }
        }
        return HidenRevenue;
    }

    public int PNodeUsed(){
        int res = 0;
        for(int i = 0; i < utils.PG.Node; i ++){
            if(Math.abs(PGFreeCapacity[i] - utils.PG.NodeCapacity[i]) > 0.000001)
                res ++;
        }
        return res;
    }
    public double VNodeCapacity(int v_flag){
        double vnodecapacity = 0;
        for (int i = 0; i < utils.VG[v_flag].Node; i ++) {
            vnodecapacity += utils.VG[v_flag].NodeCapacity[i];
        }
        return vnodecapacity;
    }

    public double PNodeUsedCapacity(){
        int res = 0;
        double pnodeusedcapacity = 0;
        for(int i = 0; i < utils.PG.Node; i ++){
            if(Math.abs(PGFreeCapacity[i] - utils.PG.NodeCapacity[i]) > 0.000001)
                {res ++;
                pnodeusedcapacity += utils.PG.NodeCapacity[i];}
        }
        return pnodeusedcapacity;
    }

    public int MaxPathLength(int v_flag){
        int res = 0;
        for(int i = 0; i < utils.VG[v_flag].VE2PE.length; i ++)
            if(utils.VG[v_flag].VE2PE[i].size() > res)
                res = utils.VG[v_flag].VE2PE[i].size();
        return res;
    }

    //used for compute balanceratio
    public double ComputePnodeBalanceRatio(){
        int count = 0;
        double max = 0;
        double sum = 0;
        for(int i = 0 ; i < utils.PG.Node; i ++){
            if(utils.PG.NodeCapacity[i] - PGFreeCapacity[i] > 0.000001){
                sum += PGFreeCapacity[i];
                count ++;
                if(PGFreeCapacity[i] > max)
                    max = PGFreeCapacity[i];
            }
        }
        return max/(sum/count);
    }

    public double ComputePLinkBalanceRatio(){
        int count = 0;
        double max = 0;
        double sum = 0;
        for(int i = 0; i < utils.PG.Node; i++){
            for(int j = i + 1; j < utils.PG.Node; j ++){
                if(utils.PG.EdgeCapacity[i][j] > 0){
                    if(utils.PG.EdgeCapacity[i][j] - PGFreeBandwidth[i][j] > 0.000001){
                        sum += PGFreeBandwidth[i][j];
                        count ++;
                        if(PGFreeBandwidth[i][j] > max)
                            max = PGFreeBandwidth[i][j];
                    }
                }
            }
        }
        return max/(sum/count);
    }

    public static void main(String[] args){
        // for test
    }

}
