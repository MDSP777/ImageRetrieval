package mmr;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ImageData {
    static HashMap<String, double[]> nh;
    
    static void init(){
        nh = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("NormalizedHistos.txt"));
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
        } catch (FileNotFoundException ex) {} 
        catch (IOException ex) {}
    }
    
    static double[] getNH(String file){
        return nh.get(file);
    }
}
