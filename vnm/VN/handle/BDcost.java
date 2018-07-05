/**
 * Created by 李雄 on 2017/5/3.
 */

import java.io.*;
import java.util.*;

public class BDcost{
    public static void main(String[] args) {
        String filename = "/Users/apple/Desktop/VNM/VN/handle/BDcost.txt";
        File file;
        PrintWriter out = null;
        try{
            file = new File(filename);
            if(!file.exists()){
                try {
                    file.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            out = new PrintWriter(file);
            for(int i = 1; i < 7; i ++ ){
                String resfile = "/Users/apple/Desktop/VNM/VN/Result/ResultWithOp" + i + ".txt";
                out.println(resfile);
                InputStreamReader reader = new InputStreamReader(new FileInputStream(resfile)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = "";
                line = br.readLine();
                while (line != null) {
                    String[] array = line.split(" +");
                    if(array.length == 7){
                        out.print(array[3] + " ");
                    }
                    line = br.readLine(); // 一次读入一行数据
                }
                out.println();
            }

        }catch (Exception e){
            System.out.println("in addVNlog there is a exception");
            e.printStackTrace();
        }finally {
            out.close();
        }
    }
}

