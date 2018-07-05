/*Link-opt
***
*
*/




package com.vnm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.MonitorInfo;
import java.util.*;

/**
 * Created by vnm on 17-2-22.
 * main Algorithm is based on Link-opt
 */
public class BaseAlgorithm1 extends Algorithm{
    public Utils utils;
    public double PGFreeCapacity[];
    public double PGFreeBandwidth[][];
    
    public BaseAlgorithm1(Utils utils) {
        this.utils = utils;
        PGFreeCapacity = new double[utils.PG.Node];
        PGFreeBandwidth = new double[utils.PG.Node][utils.PG.Node];
        
        
        
        for(int i = 0; i < utils.PG.Node; i ++){
            PGFreeCapacity[i] = utils.PG.NodeCapacity[i];
            for(int j = 0; j < utils.PG.Node; j++)
                PGFreeBandwidth[i][j] = utils.PG.EdgeCapacity[i][j];
        }
    }

    public void Deploy(int vs,String log){
        utils.VG[vs].VEBandwidth = new double[utils.VG[vs].Edge];
        Arrays.fill(utils.VG[vs].VEBandwidth,0);
        utils.VG[vs].VN2PN = new int[utils.VG[vs].Node];
        Arrays.fill(utils.VG[vs].VN2PN,-1);
        utils.VG[vs].VEBandwidth = new double[utils.VG[vs].Edge];
        Arrays.fill(utils.VG[vs].VEBandwidth,0);
        utils.VG[vs].VE2PE = new List[utils.VG[vs].Edge];
        for(int i = 0; i < utils.VG[vs].Edge; i ++){
            utils.VG[vs].VE2PE[i] = new ArrayList<Integer>();
        }


        Queue<Link> Linkqueue = new PriorityQueue<>(Link.comparator);
        for(int i = 0; i < utils.VG[vs].Node; i ++) {
            for (int j = i; j < utils.VG[vs].Node; j++) {
                if (utils.VG[vs].EdgeCapacity[i][j] > 0) {
                    Linkqueue.offer(new Link(i, j, utils.VG[vs].EdgeCapacity[i][j]));
                }
            }
        }
        utils.VG[vs].VEindex = 0;
        int falsevlinkmapped = 0;
        while(Linkqueue.isEmpty() == false){
            Link Linktmp = Linkqueue.poll();
            int i = Linktmp.From;
            int j = Linktmp.To;
            boolean Successed = false;
            List path = new ArrayList<Integer>();
            if(utils.VG[vs].VN2PN[i] != -1 && utils.VG[vs].VN2PN[j] != -1){
                Successed = TwoNodeDeployed(vs,new Link(i,j,utils.VG[vs].EdgeCapacity[i][j]),path);
                if(Successed == false) {
                    BDfailcost += utils.VG[vs].EdgeCapacity[i][j];
                    falsevlinkmapped ++;
                    System.out.println("Type 1 failed  there is not enougn BD from " + utils.VG[vs].VN2PN[i] + "  to  " + utils.VG[vs].VN2PN[j]);
                    continue;
                }
                else{
                    for(int k = 0; k < path.size() - 1; k ++){
                        int from = (Integer)path.get(k);
                        int to = (Integer)path.get(k+1);
                        if (PGFreeBandwidth[from][to] >= utils.VG[vs].EdgeCapacity[i][j]&&PGFreeCapacity[from]>=utils.VG[vs].NodeCapacity[i]&&PGFreeCapacity[to]>=utils.VG[vs].NodeCapacity[j]) {
                            PGFreeBandwidth[from][to] -= utils.VG[vs].EdgeCapacity[i][j];
                            PGFreeBandwidth[to][from] -= utils.VG[vs].EdgeCapacity[i][j];
                            linkcost += utils.VG[vs].EdgeCapacity[i][j];
                            PGFreeCapacity[from]-=utils.VG[vs].NodeCapacity[i];
                            PGFreeCapacity[to]-=utils.VG[vs].NodeCapacity[j];
                        }
                       
                    }
                    utils.VG[vs].VE2PE[utils.VG[vs].VEindex] = new ArrayList(path);
                    utils.VG[vs].VEBandwidth[utils.VG[vs].VEindex] = utils.VG[vs].EdgeCapacity[i][j];
                    BDcost += utils.VG[vs].EdgeCapacity[i][j] * utils.VG[vs].VE2PE[utils.VG[vs].VEindex].size();
                    System.out.println("Deploy Virtual Edge " + i + " " + j + "Successed!!!!!!!!!!!!!!!!!!!! by type 1");
                }
            }
            else if((utils.VG[vs].VN2PN[i] == -1 && utils.VG[vs].VN2PN[j] != -1) || (utils.VG[vs].VN2PN[i] != -1 && utils.VG[vs].VN2PN[j] == -1)){
                if(utils.VG[vs].VN2PN[i] == -1)
                    Successed = OneNodeDeployed(vs,new Link(i,j,utils.VG[vs].EdgeCapacity[i][j]),i,utils.VG[vs].VN2PN[j],path);
                else
                    Successed = OneNodeDeployed(vs,new Link(i,j,utils.VG[vs].EdgeCapacity[i][j]),j,utils.VG[vs].VN2PN[i],path);
                if(Successed == false) {
                    BDfailcost += utils.VG[vs].EdgeCapacity[i][j];
                    falsevlinkmapped ++;
                    System.out.println("Type 2 failed  cant find appropriate path " + i + "  to  " + j);
                    continue;
                }
                else{
                    int pnode = (Integer) path.get(path.size()-1);
                    if(utils.VG[vs].VN2PN[i] == -1) {
                        utils.VG[vs].VN2PN[i] = pnode;
                        if (PGFreeCapacity[pnode] >= utils.VG[vs].NodeCapacity[i]) {
                             PGFreeCapacity[pnode] -= utils.VG[vs].NodeCapacity[i];
                        } 
                    }
                    else {
                        utils.VG[vs].VN2PN[j] = pnode;
                       if (PGFreeCapacity[pnode] >= utils.VG[vs].NodeCapacity[i]) {
                             PGFreeCapacity[pnode] -= utils.VG[vs].NodeCapacity[i];
                        } 
                    }
                    for(int k = 0; k < path.size() - 1; k ++){
                        int from = (Integer)path.get(k);
                        int to = (Integer)path.get(k+1);
                        if (PGFreeBandwidth[from][to] >= utils.VG[vs].EdgeCapacity[i][j]&&PGFreeCapacity[from]>=utils.VG[vs].NodeCapacity[i]&&PGFreeCapacity[to]>=utils.VG[vs].NodeCapacity[j]) {
                            PGFreeBandwidth[from][to] -= utils.VG[vs].EdgeCapacity[i][j];
                            PGFreeBandwidth[to][from] -= utils.VG[vs].EdgeCapacity[i][j];
                            linkcost += utils.VG[vs].EdgeCapacity[i][j];
                            PGFreeCapacity[from]-=utils.VG[vs].NodeCapacity[i];
                            PGFreeCapacity[to]-=utils.VG[vs].NodeCapacity[j];
                        } 
                        
                    }
                    utils.VG[vs].VE2PE[utils.VG[vs].VEindex] = new ArrayList(path);
                    utils.VG[vs].VEBandwidth[utils.VG[vs].VEindex] = utils.VG[vs].EdgeCapacity[i][j];
                    BDcost += utils.VG[vs].EdgeCapacity[i][j] * utils.VG[vs].VE2PE[utils.VG[vs].VEindex].size();
                    System.out.println("Deploy Virtual Edge " + i + " " + j + "Successed!!!!!!!!!!!!!!!!!!!! by type 2");
                }
            }
            else{
                Link Plink[] = new Link[1];
                Plink[0] = new Link(-1,-1,-1);
                Successed = NoneNodeDeployed(vs,new Link(i,j,utils.VG[vs].EdgeCapacity[i][j]),Plink);
                if(Successed == false){
                    // judge if the Pnode Freecapacity is not enough
                    if(Plink[0].From == -1 && Plink[0].To == -1 && Plink[0].Bandwidth == -1){
                        // ReMap VNode i
                        Queue<Pair> queue1 = new PriorityQueue<>(Pair.comparator);
                        for(int k = 0; k < utils.PG.Node; k ++){
                            if(PGFreeCapacity[k] >= utils.VG[vs].NodeCapacity[i])
                                queue1.offer(new Pair(ComputeAllFreeBD(k),k));
                        }
                        if(queue1.isEmpty()){
                            BDfailcost += utils.VG[vs].EdgeCapacity[i][j];
                            falsevlinkmapped ++;
                            System.out.println("Type 3 failed again by fail-tactics find appropriate path " +  i  +"  to  " + j);
                            continue;
                        }
                        Pair pair = queue1.poll();
                        utils.VG[vs].VN2PN[i] = pair.Id;
                        if (PGFreeCapacity[pair.Id] >= utils.VG[vs].NodeCapacity[i]) {
                             PGFreeCapacity[pair.Id] -= utils.VG[vs].NodeCapacity[i];
                        }
                       

                        // ReMap VNode j
                        Queue<Pair> queue2 = new PriorityQueue<>(Pair.comparator);
                        for(int k = 0; k < utils.PG.Node; k ++){
                            if(PGFreeCapacity[k] >= utils.VG[vs].NodeCapacity[j])
                                queue2.offer(new Pair(ComputeAllFreeBD(k),k));
                        }
                        if(queue2.isEmpty()){
                            BDfailcost += utils.VG[vs].EdgeCapacity[i][j];
                            falsevlinkmapped ++;
                            System.out.println("Type 3 failed again by fail-tactics find appropriate path " +  i  +"  to  " + j);
                            continue;
                        }
                        Pair pair1 = queue2.poll();
                        utils.VG[vs].VN2PN[j] = pair1.Id;
                        if (PGFreeCapacity[pair1.Id] >= utils.VG[vs].NodeCapacity[j]) {
                            PGFreeCapacity[pair1.Id] -= utils.VG[vs].NodeCapacity[j];
                        } 

                        //  ReMap Link i->j
                        Link newlink = new Link(i,j,utils.VG[vs].EdgeCapacity[i][j]);
                        List<Integer> remaplist = new ArrayList<>();
                        boolean res = TwoNodeDeployed(vs,newlink,remaplist);
                        if (!res){
                            BDfailcost += utils.VG[vs].EdgeCapacity[i][j];
                            falsevlinkmapped ++;
                            System.out.println("Type 3 failed again by fail-tactics find appropriate path " +  i  +"  to  " + j);
                            continue;
                        }
                        else{
                            for(int k = 0; k < remaplist.size() - 1; k ++){
                                int from = remaplist.get(k);
                                int to = remaplist.get(k+1);
                                if (PGFreeBandwidth[from][to] >= utils.VG[vs].EdgeCapacity[i][j]&&PGFreeCapacity[from]>=utils.VG[vs].NodeCapacity[i]&&PGFreeCapacity[to]>=utils.VG[vs].NodeCapacity[j]) {
                                     PGFreeBandwidth[from][to] -= utils.VG[vs].EdgeCapacity[i][j];
                                     PGFreeBandwidth[to][from] -= utils.VG[vs].EdgeCapacity[i][j];
                                     linkcost += utils.VG[vs].EdgeCapacity[i][j];
                                     PGFreeCapacity[from]-=utils.VG[vs].NodeCapacity[i];
                                     PGFreeCapacity[to]-=utils.VG[vs].NodeCapacity[j];
                                } 
                               
                            }
                            utils.VG[vs].VE2PE[utils.VG[vs].VEindex] = new ArrayList(remaplist);
                            utils.VG[vs].VEBandwidth[utils.VG[vs].VEindex] = utils.VG[vs].EdgeCapacity[i][j];
                            BDcost += utils.VG[vs].EdgeCapacity[i][j] * utils.VG[vs].VE2PE[utils.VG[vs].VEindex].size();
                            System.out.println("Deploy Virtual Edge " + i + " " + j + "Successed!!!!!!!!!!!!!!!!!!!! by type 3  fail-tactics");
                        }
                    }
                    //System.out.println("Type 3 failed  cant find appropriate path " +  i  +"  to  " + j);
                    continue;
                }
                else{
                    int maxid = Plink[0].From;
                    int minid = Plink[0].To;
                    if(PGFreeCapacity[maxid] < PGFreeCapacity[minid]) {
                        int swap = maxid;
                        maxid = minid;
                        minid = swap;
                    }
                    int vmaxid = i;
                    int vminid = j;
                    if(utils.VG[vs].NodeCapacity[i] < utils.VG[vs].NodeCapacity[j]){
                        int swap = vmaxid;
                        vmaxid = vminid;
                        vminid = swap;
                    }
                    utils.VG[vs].VN2PN[vmaxid] = maxid;
                    utils.VG[vs].VN2PN[vminid] = minid;
                    PGFreeCapacity[maxid] -= utils.VG[vs].NodeCapacity[vmaxid];
                    PGFreeCapacity[minid] -= utils.VG[vs].NodeCapacity[vminid];
                    PGFreeBandwidth[maxid][minid] -= utils.VG[vs].EdgeCapacity[i][j];
                    PGFreeBandwidth[minid][maxid] -= utils.VG[vs].EdgeCapacity[i][j];
                    linkcost += utils.VG[vs].EdgeCapacity[i][j];
                    utils.VG[vs].VE2PE[utils.VG[vs].VEindex].add(minid);
                    utils.VG[vs].VE2PE[utils.VG[vs].VEindex].add(maxid);
                    utils.VG[vs].VEBandwidth[utils.VG[vs].VEindex] = utils.VG[vs].EdgeCapacity[i][j];
                    BDcost += utils.VG[vs].EdgeCapacity[i][j] * utils.VG[vs].VE2PE[utils.VG[vs].VEindex].size();
                    System.out.println("Deploy Virtual Edge " + i + " " + j + "Successed!!!!!!!!!!!!!!!!!!!! by type 3");
                }

            }
            utils.VG[vs].VEindex ++;
        }
        if(falsevlinkmapped == 0){
            VNmapped ++;
            Vlinkmapped += utils.VG[vs].Edge;
            Vlinksum += utils.VG[vs].Edge;
        }
        else{
            Vlinkmapped += (utils.VG[vs].Edge - falsevlinkmapped);
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
            //out.println(Pnodeused + "    " +PNodeUsedCapacity+ "   " + VNodeCapacity+ "   " + UseRatio);
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
                //System.out.println("开始释放。。。。。。。");
                //System.out.println("---------"+PGFreeCapacity[utils.VG[v_flag - 20].VN2PN[i]]);
                PGFreeCapacity[utils.VG[v_flag - 20].VN2PN[i]] += utils.VG[v_flag - 20].NodeCapacity[i];
                //System.out.println("+++++++++"+PGFreeCapacity[utils.VG[v_flag - 20].VN2PN[i]]);
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
    // the method when the Vedge's two node didn't deployed;
    public boolean NoneNodeDeployed(int vs,Link Vlink, Link Plink[]){
        Queue queue = new PriorityQueue<Link>(Link.comparator);
        for(int i = 0; i < utils.PG.Node; i ++){
            for(int j = i + 1; j < utils.PG.Node; j ++)
                if(PGFreeBandwidth[i][j] > Vlink.Bandwidth)
                    queue.offer(new Link(i,j,PGFreeBandwidth[i][j]));
        }
        if(queue.isEmpty())
            return false;
        Link top = (Link) queue.poll();
        if(top.Bandwidth < Vlink.Bandwidth)
            return false;
        double BigerPGFreeCapacity = Math.max(PGFreeCapacity[top.From],PGFreeCapacity[top.To]);
        double SmallerPGFreeCapacity = Math.min(PGFreeCapacity[top.From],PGFreeCapacity[top.To]);
        double BigerVNodeCapacity = Math.max(utils.VG[vs].NodeCapacity[Vlink.From],utils.VG[vs].NodeCapacity[Vlink.To]);
        double SmallerVNodeCapacity = Math.min(utils.VG[vs].NodeCapacity[Vlink.From],utils.VG[vs].NodeCapacity[Vlink.To]);
        while(BigerPGFreeCapacity < BigerVNodeCapacity || SmallerPGFreeCapacity < SmallerVNodeCapacity){
            if(queue.isEmpty())
                return false;
            top = (Link) queue.poll();
            if(top.Bandwidth < Vlink.Bandwidth)
                return false;
            BigerPGFreeCapacity = Math.max(PGFreeCapacity[top.From],PGFreeCapacity[top.To]);
            SmallerPGFreeCapacity = Math.min(PGFreeCapacity[top.From],PGFreeCapacity[top.To]);
        }
        Plink[0] = new Link(top.From,top.To,top.Bandwidth);
        return true;
    }

    // the method when Vedge's one node didn't deployed; VNode -> the not deployed; PNode-> where is the deployed Vnode; list -> the path find
    // limit k loop for path
    public boolean OneNodeDeployed(int vs,Link Vlink, int VNode, int PNode, List list){
        boolean Successed = false;
        boolean Selected[] = new boolean[utils.PG.Node];
        Selected[PNode] = true;
        int Path[] = new int[utils.PG.Node];
        Arrays.fill(Path, -1);
        Path[PNode] = 0;
        Queue<Pair> queue = new PriorityQueue<>(Pair.comparator);
        for(int i = 0; i < utils.PG.Node; i ++){
            if(PGFreeBandwidth[PNode][i] >= Vlink.Bandwidth){
                Path[i] = PNode;
                queue.offer(new Pair(PGFreeCapacity[i],i));
            }
        }
        int successednode = -1;
        int k = 0;
        while(queue.isEmpty() == false){
            Queue<Pair> queue1 = new PriorityQueue<>(Pair.comparator);
            while(queue.isEmpty() == false){
                Pair pair = (Pair) queue.poll();
                if(PGFreeCapacity[pair.Id] >= utils.VG[vs].NodeCapacity[VNode]){
                    successednode = pair.Id;
                    Successed = true;
                    break;
                }
                else{
                    Selected[pair.Id] = true;
                    for(int i = 0; i < utils.PG.Node; i ++){
                        if(Selected[i] == false && PGFreeBandwidth[pair.Id][i] >= Vlink.Bandwidth){
                            Path[i] = pair.Id;
                            queue1.offer(new Pair(PGFreeCapacity[i],i));
                        }
                    }
                }
            }
            if (Successed == true)
                break;
            else{
                queue = queue1;
            }
            k++;
        }
        if(Successed == true){
            list.add(successednode);
            int temp = Path[successednode];
            while(temp != 0){
                list.add(temp);
                temp = Path[temp];
            }
            Collections.reverse(list);
        }
        return Successed;
    }

    // the method for two Vnode hava been deployed
    public boolean TwoNodeDeployed(int vs,Link Vlink, List list){
        boolean Successed = true;
        int From = utils.VG[vs].VN2PN[Vlink.From];
        int To = utils.VG[vs].VN2PN[Vlink.To];
        if (From == To)
            return Successed;

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
            if(PGFreeBandwidth[From][i] >= Vlink.Bandwidth)
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
                if(Selected[j] == false && PGFreeBandwidth[minnode][j] >= Vlink.Bandwidth){
                    if(Dist[minnode] + 1 < Dist[j]) {
                        Dist[j] = Dist[minnode] + 1;
                        Path[j] = minnode;
                        //System.out.println("jjjjj   " + j + "   minnode   " + minnode);
                    }
                }
            }
        }

        int temp = To;
        list.add(temp);
        while(Path[temp] != -1){
            temp = Path[temp];
            list.add(temp);
        }
        list.add(From);
        Collections.reverse(list);
        return true;
    }

    public int PNodeUsed(){
        int res = 0;
        for(int i = 0; i < utils.PG.Node; i ++){
            if(Math.abs(PGFreeCapacity[i] - utils.PG.NodeCapacity[i]) > 0.000001)
                res ++;
        }
        return res;
    }

    public double VNodeCapacity(int vs){
        double vnodecapacity = 0;
        for (int i = 0; i < utils.VG[vs].Node; i ++) {
            vnodecapacity += utils.VG[vs].NodeCapacity[i];
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

    public int MaxPathLength(int vs){
        int res = 0;
        for(int i = 0; i <  utils.VG[vs].VE2PE.length; i ++)
            if(utils.VG[vs].VE2PE[i].size() > res)
                res = utils.VG[vs].VE2PE[i].size();
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

    //Compute a Pnode，s all free bd
    public double ComputeAllFreeBD(int PNode){
        double res = 0;
        for(int i = 0; i < utils.PG.Node; i ++){
            if (PGFreeBandwidth[PNode][i] > 0)
                res += PGFreeBandwidth[PNode][i];
        }
        return res;
    }

    public void RestructVN(int vs,String path){
        utils.setVGPath(path);
        utils.ConstructVirtualGraph();
        utils.VG[vs].VN2PN = new int[utils.VG[vs].Node];
        Arrays.fill(utils.VG[vs].VN2PN,-1);
        utils.VG[vs].VEBandwidth = new double[utils.VG[vs].Edge];
        Arrays.fill(utils.VG[vs].VEBandwidth,0);
        utils.VG[vs].VE2PE = new List[utils.VG[vs].Edge];
        for(int i = 0; i < utils.VG[vs].Edge; i ++){
            utils.VG[vs].VE2PE[i] = new ArrayList<Integer>();
        }
    }

    public double AddVNlog(int vs){
        double RevenueRatio = utils.VGBandwidthMean / utils.VGCapacityMean;
        double Capacityrevenue = 0;
        double Bandwidthrevenue = 0;
        //String path = "/home/vnm/VN/VNlog/"+utils.VG.Node+".brite";
        String path = "/Users/apple/Desktop/VNM12/VN/VNlog/"+utils.VG[vs].Node+".brite";
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
            for(int i = 0; i < utils.VG[vs].Node; i ++){
                // VN i didn't map
                if(utils.VG[vs].VN2PN[i] == -1){
                    //continue;
                    Capacityrevenue = 0;
                    Bandwidthrevenue = 0;
                    break;

                }
                    
                Capacityrevenue += utils.VG[vs].NodeCapacity[i];
                out.println(i + " " + utils.VG[vs].VN2PN[i] + " " + utils.VG[vs].NodeCapacity[i]);
            }
            out.println("VE2PE from to Vbandwidth");
            for(int i = 0; i < utils.VG[vs].VE2PE.length; i++){
                // VEdge i didn't map
                if(utils.VG[vs].VE2PE[i].size() == 0){
                    Bandwidthrevenue = 0;
                    Capacityrevenue = 0;
                    //continue;
                    break;
                }
                    
                for(int j = 0; j < utils.VG[vs].VE2PE[i].size(); j++){
                    out.print(utils.VG[vs].VE2PE[i].get(j)+" ");
                }
                Bandwidthrevenue += utils.VG[vs].VEBandwidth[i] ;
                out.println(utils.VG[vs].VEBandwidth[i]);
            }
        }catch (Exception e){
            System.out.println("in addVNlog there is a exception");
            e.printStackTrace();
        }finally {
            out.close();
        }
        
            Bandwidthrevenue += RevenueHideinPnode(vs);
        
        return Capacityrevenue * RevenueRatio + Bandwidthrevenue;
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
    public double RevenueHideinPnode(int vs){
        double HidenRevenue = 0;
        for(int i = 0; i < utils.VG[vs].Node; i ++){
            for(int j = i+1; j < utils.VG[vs].Node; j ++){
                if(utils.VG[vs].EdgeCapacity[i][j] > 0 && utils.VG[vs].VN2PN[i] == utils.VG[vs].VN2PN[j] && utils.VG[vs].VN2PN[i] != -1)
                    HidenRevenue += utils.VG[vs].EdgeCapacity[i][j] * 1.4;
            }
        }
        return HidenRevenue;
    }

    public static void main(String[] args){
        TreeSet<Integer> queue = new TreeSet<>();
        queue.add(1);
        queue.add(3);
        queue.add(-2);



        for(Integer i : queue)
            System.out.println(i);
    }
}
