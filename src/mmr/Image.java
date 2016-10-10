package mmr;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Image {
    String filename;
    BufferedImage bi;
    int nRows;
    int nCols;
    int area;
    int[][] luv;
    double[] nh;
    public static final int LUV_MAX = 159;
    
    public Image(String filename){
        this.filename = filename;
        try {
            File file = new File(filename);
            bi = ImageIO.read(file);
        } catch (IOException ex) {}
        nRows = bi.getHeight();
        nCols = bi.getWidth();
        area = nRows*nCols;
        
        ColorModel CM = bi.getColorModel();
        luv = new int[nRows][nCols];
        nh = new double[LUV_MAX];
        for(int i=0; i<nRows; i++)
            for(int j=0; j<nCols; j++){
                int RGB1 = bi.getRGB(j, i);
                double R = CM.getRed(RGB1);
                double G = CM.getGreen(RGB1);
                double B = CM.getBlue(RGB1);	
                CieConvert colorCIE = new CieConvert();
                colorCIE.setValues(R/255.0, G/255.0, B/255.0);
                luv[i][j] = colorCIE.IndexOf();
                nh[colorCIE.IndexOf()]++;
            }
        for(int i=0; i<LUV_MAX; i++){
            nh[i]/=area;
        }
    }
    
    public Image(double[] nh){
        this.nh = nh;
    }
    
    public double compareTo(Image i2, double delta){
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

    String getStringNH() {
        StringBuilder sb = new StringBuilder(nh[0]+"");
        for(int i=1; i<LUV_MAX; i++) sb.append(" ").append(nh[i]);
        return sb.toString();
    }
}
