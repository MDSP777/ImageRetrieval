package mmr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    
    //checks whether an int is prime or not.
    public boolean isPrime(int n) {
        //check if n is a multiple of 2
        if (n%2==0) return false;
        //if not, then just check the odds
        for(int i=3;i*i<=n;i+=2) {
            if(n%i==0)
                return false;
        }
        return true;
    }
    
    public ArrayList<Integer> getFactors(int n) {
        ArrayList<Integer> factors = new ArrayList<>();
        
        int factorNumber = 1;
        while(factorNumber <= n){
            if(n % factorNumber == 0){
                System.out.println(factorNumber + " is a factor of " + n);
            }
            factorNumber++;
        }
        
        return factors;
    }
    
    /* CH with Centering Refinement */
    public double algo2(Image i2, double delta, double centerPercent){
        centerPercent = 0.5;
        
        /* image 1 */
        int i1areaCenter = (int) Math.round(this.area*centerPercent);
        while (!isPrime(i1areaCenter)){
            i1areaCenter++;
        }
        ArrayList<Integer> i1factors = getFactors(i1areaCenter);
        int i1nRowsCenter = 0, i1nColsCenter = 0;
        if (i1factors.size() % 0 == 0){
            i1nRowsCenter = i1factors.get((i1factors.size()/2)-1); // if 10, returns 4
            i1nColsCenter = i1factors.get(i1factors.size()/2);
        } else {
            i1nRowsCenter = i1factors.get((i1factors.size()/2)); // if 11, returns 5
            i1nColsCenter = i1factors.get((i1factors.size()/2+1)); // if 11, returns 6
        }
        
        if (this.nRows > this.nCols){
            if (i1nColsCenter > i1nRowsCenter){
                int temp = i1nRowsCenter;
                i1nRowsCenter = i1nColsCenter;
                i1nColsCenter = temp;
            }
        } else if (this.nCols > this.nRows){
            if (i1nRowsCenter > i1nColsCenter){
                int temp = i1nRowsCenter;
                i1nRowsCenter = i1nColsCenter;
                i1nColsCenter = temp;
            }
        }
                               
        int i1nRowsCenterStart = ((this.nRows - i1nRowsCenter) / 2);
        int i1nRowsCenterEnd = i1nRowsCenterStart + i1nRowsCenter - 1;
        int i1nColsCenterStart = ((this.nCols - i1nColsCenter) / 2);
        int i1nColsCenterEnd = i1nColsCenterStart + i1nColsCenter - 1;
        
        double[] i1nhNonCenter = new double[LUV_MAX];
        double[] i1nhCenter = new double[LUV_MAX];
        for (int i = 0; i < luv.length; i++){
            for (int j = 0; j < luv[i].length; j++){
                if (i >= i1nRowsCenterStart && i <= i1nRowsCenterEnd
                    && j >= i1nColsCenterStart && j <= i1nColsCenterEnd){
                    if (i>0)
                        i1nhCenter[i*j] ++;
                    else
                        i1nhCenter[i+j] ++;
                } else {
                    if (i>0)
                        i1nhNonCenter[i*j] ++;
                    else
                        i1nhNonCenter[i+j] ++;
                }
            }
        }
        
        /* image 2 */
        int i2areaCenter = (int) Math.round(i2.area*centerPercent);
        while (!isPrime(i2areaCenter)){
            i2areaCenter++;
        }
        ArrayList<Integer> i2factors = getFactors(i2areaCenter);
        int i2nRowsCenter = 0, i2nColsCenter = 0;
        if (i2factors.size() % 0 == 0){
            i2nRowsCenter = i2factors.get((i2factors.size()/2)-1); // if 10, returns 4
            i2nColsCenter = i2factors.get(i2factors.size()/2);
        } else {
            i2nRowsCenter = i2factors.get((i2factors.size()/2)); // if 11, returns 5
            i2nColsCenter = i2factors.get((i2factors.size()/2+1)); // if 11, returns 6
        }
        
        if (i2.nRows > i2.nCols){
            if (i2nColsCenter > i2nRowsCenter){
                int temp = i2nRowsCenter;
                i2nRowsCenter = i2nColsCenter;
                i2nColsCenter = temp;
            }
        } else if (i2.nCols > i2.nRows){
            if (i2nRowsCenter > i2nColsCenter){
                int temp = i2nRowsCenter;
                i2nRowsCenter = i2nColsCenter;
                i2nColsCenter = temp;
            }
        }
                               
        int i2nRowsCenterStart = ((i2.nRows - i2nRowsCenter) / 2);
        int i2nRowsCenterEnd = i2nRowsCenterStart + i2nRowsCenter - 1;
        int i2nColsCenterStart = ((this.nCols - i2nColsCenter) / 2);
        int i2nColsCenterEnd = i2nColsCenterStart + i2nColsCenter - 1;
        
        double[] i2nhNonCenter = new double[LUV_MAX];
        double[] i2nhCenter = new double[LUV_MAX];
        for (int i = 0; i < luv.length; i++){
            for (int j = 0; j < luv[i].length; j++){
                if (i >= i2nRowsCenterStart && i <= i2nRowsCenterEnd
                    && j >= i2nColsCenterStart && j <= i2nColsCenterEnd){
                    if (i>0)
                        i2nhCenter[i*j] ++;
                    else
                        i2nhCenter[i+j] ++;
                } else {
                    if (i>0)
                        i2nhNonCenter[i*j] ++;
                    else
                        i2nhNonCenter[i+j] ++;
                }
            }
        }
        
        /* compare */
        int nValidColorsCenters = 0, nValidColorsNonCenters = 0;
        double simCenters = 0, simNonCenters = 0;
        for(int i=0; i<LUV_MAX; i++){
            if(i1nhCenter[i]>delta){
                nValidColorsCenters++;
                double cur = 1.0 - Math.abs(i1nhCenter[i]-i2nhCenter[i]) / Math.max(i1nhCenter[i],i2nhCenter[i]);
            }
            if(i1nhNonCenter[i]>delta){
                nValidColorsNonCenters++;
                double cur = 1.0 - Math.abs(i1nhNonCenter[i]-i2nhNonCenter[i])/Math.max(i1nhNonCenter[i], i2nhNonCenter[i]);
                simNonCenters+=cur;
            }
        }
        simCenters/=nValidColorsCenters;
        simNonCenters/=nValidColorsNonCenters;
        
        return simCenters+simNonCenters/2;
    }
    
    public double bonus(Image i2, double delta){
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
