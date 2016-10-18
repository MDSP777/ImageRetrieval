package mmr;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ImageData {
    static HashMap<String, double[]> nh;
    static HashMap<String, double[][][]> lh;
    
    static void init(){
        nh = new HashMap<>();
        lh = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("NormalizedHistos.txt"));
            BufferedReader br2 = new BufferedReader(new FileReader("LocalizedHistos.txt"));
            while(true){
                String in = br.readLine();
                if("".equals(in) || in==null) break;
                String[] nhRaw = br.readLine().split(" ");
                double[] nhData = new double[Image.LUV_MAX];
                for(int i=0; i<Image.LUV_MAX; i++){
                    nhData[i] = Double.parseDouble(nhRaw[i]);
                }
                nh.put(in, nhData);
            }
            while(true){
                String in = br2.readLine();
                if("".equals(in) || in==null) break;
                double[][][] curLH = new double[Image.BLOCKS_PER_ROW][Image.BLOCKS_PER_COL][];
                for(int i=0; i<Image.BLOCKS_PER_ROW; i++)
                    for(int j=0; j<Image.BLOCKS_PER_COL; j++){
                        String[] lhRaw = br2.readLine().split(" ");
                        double[] lhData = new double[Image.LUV_MAX];
                        for(int k=0; k<Image.LUV_MAX; k++){
                            lhData[k] = Double.parseDouble(lhRaw[k]);
                        }
                        curLH[i][j] = lhData;
                    }
                lh.put(in, curLH);
            }
        } catch (FileNotFoundException ex) {} 
        catch (IOException ex) {}
        System.out.println("Initialization finished");
    }
    
    static double[] getNH(String file){
        return nh.get(file);
    }
    
    static double[][][] getLH(String file){
        return lh.get(file);
    }
}
