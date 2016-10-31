package mmr;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ImageData {
    static HashMap<String, double[]> nh;
    static HashMap<String, double[][][]> lh;
    static HashMap<String, int[][]> luv;
    
    static void init(){
        nh = new HashMap<>();
        lh = new HashMap<>();
        luv = new HashMap<>();
        try {
            BufferedReader brNH = new BufferedReader(new FileReader("NormalizedHistos.txt"));
            BufferedReader brLH = new BufferedReader(new FileReader("LocalizedHistos.txt"));
            BufferedReader brLUV = new BufferedReader(new FileReader("luv.txt"));
            while(true){
                String in = brNH.readLine();
                if("".equals(in) || in==null) break;
                String[] nhRaw = brNH.readLine().split(" ");
                double[] nhData = new double[Image.LUV_MAX];
                for(int i=0; i<Image.LUV_MAX; i++){
                    nhData[i] = Double.parseDouble(nhRaw[i]);
                }
                nh.put(in, nhData);
            }
            while(true){
                String in = brLH.readLine();
                if("".equals(in) || in==null) break;
                double[][][] curLH = new double[Image.BLOCKS_PER_ROW][Image.BLOCKS_PER_COL][];
                for(int i=0; i<Image.BLOCKS_PER_ROW; i++)
                    for(int j=0; j<Image.BLOCKS_PER_COL; j++){
                        String[] lhRaw = brLH.readLine().split(" ");
                        double[] lhData = new double[Image.LUV_MAX];
                        for(int k=0; k<Image.LUV_MAX; k++){
                            lhData[k] = Double.parseDouble(lhRaw[k]);
                        }
                        curLH[i][j] = lhData;
                    }
                lh.put(in, curLH);
            }
            while(true){
                String in = brLUV.readLine();
                if("".equals(in) || in==null) break;
                String[] dim = brLUV.readLine().split(" ");
                int nRows = Integer.parseInt(dim[0]);
                int nCols =Integer.parseInt(dim[1]);
                int[][] curLuv = new int[nRows][nCols];
                for(int i=0; i<nRows; i++){
                        String[] luvRaw = brLUV.readLine().split(" ");
                        for(int j=0; j<nCols; j++) 
                            curLuv[i][j] = Integer.parseInt(luvRaw[j]);
                }
                luv.put(in, curLuv);
                
            }
        } catch (FileNotFoundException ex) {} 
        catch (IOException ex) {}
        System.out.println("Initialization finished");
    }
    
    static double[] getNH(String file){
        return nh.get(file);
    }
    
    static int[][] getLuv(String file){
        return luv.get(file);
    }
    
//    static double[] getCenter50(String file){
//        return nh.get(file);
//    }
//    
//    static double[] getNonCenter50(String file){
//        return nh.get(file);
//    }
//    
//    static double[] getCenter75(String file){
//        return nh.get(file);
//    }
//    
//    static double[] getNonCenter75(String file){
//        return nh.get(file);
//    }
    
    static double[][][] getLH(String file){
        return lh.get(file);
    }
}
