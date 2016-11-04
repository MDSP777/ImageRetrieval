package mmr;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
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
    double[] nh, nhNonCenter, nhCenter;
    double[][][] lh;
    
    double[] coherent;
    double[] noncoherent;
    SimilarityMatrix simMatrix = SimilarityMatrix.getInstance();
    
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
        
        nhNonCenter = new double[LUV_MAX];
        nhCenter = new double[LUV_MAX];
        
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
    
    public Image(int[][] luv){
        this.luv = luv;
        this.nRows = luv.length;
        this.nCols = luv[0].length;
        this.area = nRows*nCols;
    }
    
    public Image(double[] nh, double[][][] lh){
        this.nh = nh;
        this.lh = lh;
    }
    
    public double ch(Image i2, double delta){
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
    
    public double chWithCentering(Image i2, double centerPercent){
        if(centerPercent>=1 || centerPercent<=0)
            throw new RuntimeException("Invalid center percentage value, "
                    + "expected: 0<center<1, actual: "+centerPercent);
        
        this.computeNHCenterNonCenter(centerPercent);
        i2.computeNHCenterNonCenter(centerPercent);   
        
        double sim = 0;
        for(int i=0; i<LUV_MAX; i++){
            sim+=Math.abs(this.nhCenter[i]-i2.nhCenter[i])
                    +Math.abs(this.nhNonCenter[i]-i2.nhNonCenter[i]);
        }
        return sim*-1;
    }
    
    public double chWithCCV(Image i2, int connectiveness){
        int thresh = (this.area+i2.area)/2/100;
        this.calcCCV(thresh, connectiveness);
        i2.calcCCV(thresh, connectiveness);
        double sim = 0;
        for(int i=0; i<LUV_MAX; i++)
            sim+=Math.abs(this.coherent[i]-i2.coherent[i])
                    +Math.abs(this.noncoherent[i]-i2.noncoherent[i]);
        return sim*-1;
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
            }
        sim/=NUM_BLOCKS;
        return sim;
    }
    public boolean isPrime(int n) {
        for(int i=2;i*i<=n;i++) 
            if(n%i==0)
                return false;
        return true;
    }
    
    public ArrayList<Integer> getFactors(int n) {
        ArrayList<Integer> factors = new ArrayList<>();
        int factorNumber = (int) Math.round(Math.sqrt(n));
        while(factorNumber>=1){
            if(n % factorNumber == 0){
                factors.add(factorNumber);
                factors.add(n/factorNumber);
                break;
            }
            factorNumber--;
        }
        return factors;
    }

    public void computeNHCenterNonCenter(double centerPercent){
        nhNonCenter = new double[LUV_MAX];
        nhCenter = new double[LUV_MAX];
        
        int areaCenter = (int) Math.round(this.area * centerPercent);
        while (isPrime(areaCenter)){
            areaCenter++;
        }

        ArrayList<Integer> factors = getFactors(areaCenter);
        int nRowsCenter = factors.get(0);
        int nColsCenter = factors.get(1);

        if (this.nRows > this.nCols){
            if (nColsCenter > nRowsCenter){
                int temp = nRowsCenter;
                nRowsCenter = nColsCenter;
                nColsCenter = temp;
            }
        } else if (this.nCols > this.nRows){
            if (nRowsCenter > nColsCenter){
                int temp = nRowsCenter;
                nRowsCenter = nColsCenter;
                nColsCenter = temp;
            }
        }

        int nRowsCenterStart = ((this.nRows - nRowsCenter) / 2);
        int nRowsCenterEnd = nRowsCenterStart + nRowsCenter - 1;
        int nColsCenterStart = ((this.nCols - nColsCenter) / 2);
        int nColsCenterEnd = nColsCenterStart + nColsCenter - 1;

        for (int i = 0; i < luv.length; i++){
            for (int j = 0; j < luv[i].length; j++){
                if (i >= nRowsCenterStart && i <= nRowsCenterEnd
                    && j >= nColsCenterStart && j <= nColsCenterEnd){
                    nhCenter[luv[i][j]]++;
                } else {
                    nhNonCenter[luv[i][j]]++;
                }
            }
        }
        for(int i=0; i<LUV_MAX; i++){
            nhCenter[i]/=area;
            nhNonCenter[i]/=area;
        }
    }
    
    
    public void calcCCV(int thresh, int connectiveness){
        coherent = new double[LUV_MAX];
        noncoherent = new double[LUV_MAX];
        if(connectiveness!=4 && connectiveness!=8) 
            throw new RuntimeException("Invalid connectiveness value, "
                    + "expected: 4 or 8, actual: "+connectiveness);
        int[][] luvCopy = new int[nRows][];
        for(int i=0; i<nRows; i++) luvCopy[i] = luv[i].clone();
        for(int i=0; i<nRows; i++)
            for(int j=0; j<nCols; j++){
                int val = luvCopy[i][j];
                if(val!=-1){
                    int inc = floodfill(luvCopy, i, j, val, connectiveness);
                    if(inc>=thresh) coherent[val] += inc;
                    else noncoherent[val] += inc;
                }
            }
        for(int i=0; i<LUV_MAX; i++){
            coherent[i]/=area;
            noncoherent[i]/=area;
        }
    }
    
    private int floodfill(int[][] luv, int i, int j, int val, int connectiveness){
        Queue<Node> q = new LinkedList<>();
        int totalPixels = 0;
        q.add(new Node(i,j));
        while(!q.isEmpty()){
            Node cur = q.poll();
            i = cur.i;
            j = cur.j;
            if(luv[i][j]!=val) continue;
            totalPixels++;
            luv[i][j] = -1;
            if(i>0) q.add(new Node(i-1, j));
            if(i<luv.length-1) q.add(new Node(i+1, j));
            if(j>0) q.add(new Node(i, j-1));
            if(j<luv[0].length-1) q.add(new Node(i, j+1));
            if(connectiveness==8){
                if(i>0 && j>0) q.add(new Node(i-1, j-1));
                if(i>0 && j<luv[0].length-1) q.add(new Node(i-1, j+1));
                if(i<luv.length-1 && j>0) q.add(new Node(i+1, j-1));
                if(i<luv.length-1 && j<luv[0].length-1) q.add(new Node(i+1, j+1));
            }
        }
        return totalPixels;
    }

    public double ps(Image i2) {

        double simColor = 0;
        for(int i = 0; i < LUV_MAX; i++) {
            if(this.nh[i]>0) simColor += (getSimColor(i2, i) * this.nh[i]);
        }

        return simColor;
    }

    public double getSimColor(Image i2, int i) {

        return (1 + getPerColor(i2, i)) * getExactColor(i2, i);

    }

    public double getPerColor(Image i2, int i) {

        double sim = 0;
        //SimilarityMatrix simMatrix = new SimilarityMatrix();

        for(int j = 0; j < LUV_MAX; j++) {

            if(simMatrix.getColorMatrix()[i][j] != 0){
                sim += 1.0 - Math.abs((this.nh[i] - i2.nh[j]) / Math.max(this.nh[i], i2.nh[j])) * simMatrix.getColorMatrix()[i][j];
            }

        }

        return sim;

    }

    public double getExactColor(Image i2, int i) {

        return 1.0 - Math.abs((this.nh[i] - i2.nh[i]) / Math.max(this.nh[i], i2.nh[i]));

    }


    String getStringArr(double[] arr) {
        StringBuilder sb = new StringBuilder(arr[0]+"");
        for(int i=1; i<arr.length; i++) sb.append(" ").append(arr[i]);
        return sb.toString();
    }
    
    String getStringLuv(){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<nRows; i++){    
            sb.append(luv[i][0]);
            for(int j=0; j<nCols; j++) sb.append(" ").append(luv[i][j]);
            if(!(i==nRows-1)) sb.append("\n");
        }
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
    
    private class Node {
        int i;
        int j;
        
        public Node(int i, int j){
            this.i = i;
            this.j = j;
        }
    }
}
