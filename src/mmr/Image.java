package mmr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Image {
    public static final int LUV_MAX = 159;
    public static final int BLOCKS_PER_ROW = 5;
    public static final int BLOCKS_PER_COL = 5;
    public static final int NUM_BLOCKS = BLOCKS_PER_ROW*BLOCKS_PER_COL;
    
    String filename;
    BufferedImage bi;
    int nRows;
    int nCols;
    int area;
    int[][] luv;
    double[] nh;
    double[][][] lh;
    
    double[] coherent;
    double[] noncoherent;
    
    public Image(String filename){
        this.filename = filename;
        try {
            File file = new File(filename);
            bi = ImageIO.read(file);
        } catch (IOException ex) {}
        nRows = bi.getHeight();
        nCols = bi.getWidth();
        area = nRows*nCols;
        
        luv = new int[nRows][nCols];
        nh = new double[LUV_MAX];
        
        coherent = new double[LUV_MAX];
        noncoherent = new double[LUV_MAX];
        
        lh = new double[BLOCKS_PER_ROW][BLOCKS_PER_COL][LUV_MAX];
        for(int i=0; i<nRows; i++)
            for(int j=0; j<nCols; j++){
                Color CM = new Color(bi.getRGB(j, i));
                double R = CM.getRed();
                double G = CM.getGreen();
                double B = CM.getBlue();	
                CieConvert colorCIE = new CieConvert();
                colorCIE.setValues(R/255.0, G/255.0, B/255.0);
                luv[i][j] = colorCIE.IndexOf();
                nh[colorCIE.IndexOf()]++;
                
                int blockRow = 0;
                int blockCol = 0;
                double rowPercentile = (double)(i)/nRows-0.2;
                double colPercentile = (double)(j)/nCols-0.2;
                while(rowPercentile>0){
                    blockRow++;
                    rowPercentile-=0.2;
                }
                while(colPercentile>0){
                    blockCol++;
                    colPercentile-=0.2;
                }
//                System.out.println(blockRow+" "+blockCol+" "+colorCIE.IndexOf());
                lh[blockRow][blockCol][colorCIE.IndexOf()]++;
            }
        for(int i=0; i<LUV_MAX; i++){
            nh[i]/=area;
        }
        for(int i=0; i<BLOCKS_PER_ROW; i++)
            for(int j=0; j<BLOCKS_PER_COL; j++)
                for(int k=0; k<LUV_MAX; k++)
                    lh[i][j][k]/=area;
    }
    
    public Image(double[] nh){
        this.nh = nh;
    }
    
    public Image(double[][][] lh){
        this.lh = lh;
    }
    
    public Image(double[] nh, double[][][] lh){
        this.nh = nh;
        this.lh = lh;
    }
    
    public double algo1(Image i2, double delta){
        int nValidColors = 0;
        double sim = 0;
        for(int i=0; i<LUV_MAX; i++){
            if(this.nh[i]>delta){
                nValidColors++;
                double cur = 1.0 - Math.abs(this.nh[i]-i2.nh[i])/Math.max(this.nh[i], i2.nh[i]);
                sim+=cur;
            }
        }
        sim/=nValidColors;
        return sim;
    }
    
    public double bonus(Image i2, double delta){
        delta/=NUM_BLOCKS;
        double sim = 0;
        for(int i=0; i<BLOCKS_PER_ROW; i++)
            for(int j=0; j<BLOCKS_PER_COL; j++){
                int nValidColors = 0;
                double curSim = 0;
                for(int k=0; k<LUV_MAX; k++){
                    if(this.lh[i][j][k]>delta){
                        nValidColors++;
                        double cur = 1.0 - Math.abs(this.lh[i][j][k]-i2.lh[i][j][k])/
                                Math.max(this.lh[i][j][k], i2.lh[i][j][k]);
                        curSim+=cur;
                    }
                }
                if(nValidColors>0) curSim/=nValidColors;
                sim+=curSim;
//                System.out.println(sim+" "+curSim+" "+nValidColors+" "+i+" "+j);
            }
        sim/=NUM_BLOCKS;
        return sim;
    }

    String getStringNH() {
        StringBuilder sb = new StringBuilder(nh[0]+"");
        for(int i=1; i<LUV_MAX; i++) sb.append(" ").append(nh[i]);
        return sb.toString();
    }
    
    String getStringLH(){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<BLOCKS_PER_ROW; i++)
            for(int j=0; j<BLOCKS_PER_COL; j++){
                sb.append(lh[i][j][0]);
                for(int k=1; k<LUV_MAX; k++) sb.append(" ").append(lh[i][j][k]);
                if(!(i==BLOCKS_PER_ROW-1 && j==BLOCKS_PER_COL-1)) sb.append("\n");
            }
        return sb.toString();
    }
    
}
