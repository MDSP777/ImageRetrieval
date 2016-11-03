package mmr;

/**
 * Created by raynefathom on 11/3/2016.
 */
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import javax.imageio.ImageIO;

public class SimilarityMatrix {

    public static final int LUV_MAX = 159;
    public static final double P = 0.2;

    public double colorMatrix[][] = new double[LUV_MAX][LUV_MAX];
    public double distanceMatrix[][];
    public double max;
    public double threshold;


    public SimilarityMatrix() {

        if(colorMatrix == null) {

            File file = new File("colSim.txt");

            if (file.exists()) {
                initColorMatrix2();
            }
            else {
                initColorMatrix();
            }

        }


    }

    public void writeFile(){

        BufferedWriter bw = null;
        String sw = "";


        for(int i = 0; i < LUV_MAX; i++) {
            for(int j = 0; j < LUV_MAX; j++){
                if(j != 0){
                    sw += " ";
                }
                sw += colorMatrix[i][j];
            }
            sw += "\n";
        }
        try{
            File file = new File("colSim.txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(sw);
            System.out.println("File written Successfully");
            bw.close();
        }
        catch(IOException io){
            System.out.println("WIW");
            io.printStackTrace();
        }
    }

    public int readFile() {

        int flag = 1;
        BufferedReader in;

        try{
            File file = new File("colSim.txt");
            in = new BufferedReader(new FileReader(file));
            String[] s;

            int i = 0;
            while (true) {

                if("".equals(in) || in==null) break;
                s = in.readLine().split(" ");
                //System.out.println(s.length);
                for(int j = 0; j < LUV_MAX; j++) {
                    colorMatrix[i][j] = Double.parseDouble(s[j]);
                }
                i++;
            }

            in.close();
        }
        catch(IOException io) {
            System.out.println("WIW2");
            io.printStackTrace();
            flag = 0;
        }

        return flag;

    }

    public void computeDistance() {

        CieConvert cieColor = new CieConvert();
        cieColor.initLuvIndex();

        distanceMatrix = new double[LUV_MAX][LUV_MAX];
        for(int i = 0; i < LUV_MAX; i++){
            for(int j = 0; j < LUV_MAX; j++){

               distanceMatrix[i][j] =
                          Math.sqrt((cieColor.LuvIndex[i].L-cieColor.LuvIndex[j].L)*(cieColor.LuvIndex[i].L-cieColor.LuvIndex[j].L)+
                          (cieColor.LuvIndex[i].u-cieColor.LuvIndex[j].u)*(cieColor.LuvIndex[i].u-cieColor.LuvIndex[j].u)+
                          (cieColor.LuvIndex[i].v-cieColor.LuvIndex[j].v)*(cieColor.LuvIndex[i].v-cieColor.LuvIndex[j].v));

                if(i == 0 && j == 0) {
                    max = distanceMatrix[i][j];
                }
                else {
                    if (distanceMatrix[i][j] > max) {
                        max = distanceMatrix[i][j];
                    }
                }
            }
        }
    }

    public void computeThreshold() {

        threshold = P * max;

    }

    public void computeColorMatrix() {


        for(int i = 0; i < LUV_MAX; i++) {
            for(int j = 0; j < LUV_MAX; j++) {

                if(distanceMatrix[i][j] > threshold) {
                    colorMatrix[i][j] = 0;
                }
                else {
                    colorMatrix[i][j] = 1 - (distanceMatrix[i][j]/threshold);
                }

            }
        }

    }

    public void initColorMatrix() {

        computeDistance();
        computeThreshold();
        computeColorMatrix();
        writeFile();

    }

    public void initColorMatrix2() {

        readFile();

    }

    public double[][] getColorMatrix() {
        return colorMatrix;
    }

}
