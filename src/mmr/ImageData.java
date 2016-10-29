package mmr;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ImageData {
    static HashMap<String, double[]> nh;
    static HashMap<String, double[]> nhCenter50;
    static HashMap<String, double[]> nhNonCenter50;
    static HashMap<String, double[]> nhCenter75;
    static HashMap<String, double[]> nhNonCenter75;
    static HashMap<String, double[][][]> lh;
    
    static void init(){
        nh = new HashMap<>();
        lh = new HashMap<>();
        nhCenter50 = new HashMap<>();
        nhNonCenter50 = new HashMap<>();
        nhCenter75 = new HashMap<>();
        nhNonCenter75 = new HashMap<>();
        try {
            BufferedReader brNH = new BufferedReader(new FileReader("NormalizedHistos.txt"));
            BufferedReader br50 = new BufferedReader(new FileReader("CenterHistos50.txt"));
            BufferedReader br75 = new BufferedReader(new FileReader("CenterHistos75.txt"));
            BufferedReader brLH = new BufferedReader(new FileReader("LocalizedHistos.txt"));
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
                String in = br50.readLine();
                if("".equals(in) || in==null) break;
                String[] nhCenterRaw = br50.readLine().split(" ");
                double[] nhCenterData = new double[Image.LUV_MAX];
                for(int i=0; i<Image.LUV_MAX; i++){
                    nhCenterData[i] = Double.parseDouble(nhCenterRaw[i]);
                }
                nhCenter50.put(in, nhCenterData);
                String[] nhNonCenterRaw = br50.readLine().split(" ");
                double[] nhNonCenterData = new double[Image.LUV_MAX];
                for(int i=0; i<Image.LUV_MAX; i++){
                    nhNonCenterData[i] = Double.parseDouble(nhNonCenterRaw[i]);
                }
                nhNonCenter50.put(in, nhNonCenterData);
            }
            while(true){
                String in = br75.readLine();
                if("".equals(in) || in==null) break;
                String[] nhCenterRaw = br75.readLine().split(" ");
                double[] nhCenterData = new double[Image.LUV_MAX];
                for(int i=0; i<Image.LUV_MAX; i++){
                    nhCenterData[i] = Double.parseDouble(nhCenterRaw[i]);
                }
                nhCenter75.put(in, nhCenterData);
                String[] nhNonCenterRaw = br75.readLine().split(" ");
                double[] nhNonCenterData = new double[Image.LUV_MAX];
                for(int i=0; i<Image.LUV_MAX; i++){
                    nhNonCenterData[i] = Double.parseDouble(nhNonCenterRaw[i]);
                }
                nhNonCenter75.put(in, nhNonCenterData);
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
        } catch (FileNotFoundException ex) {} 
        catch (IOException ex) {}
        System.out.println("Initialization finished");
    }
    
    static double[] getNH(String file){
        return nh.get(file);
    }
    
    static double[] getCenter50(String file){
        return nh.get(file);
    }
    
    static double[] getNonCenter50(String file){
        return nh.get(file);
    }
    
    static double[] getCenter75(String file){
        return nh.get(file);
    }
    
    static double[] getNonCenter75(String file){
        return nh.get(file);
    }
    
    static double[][][] getLH(String file){
        return lh.get(file);
    }
}
