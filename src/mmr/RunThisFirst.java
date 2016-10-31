package mmr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
    Precomputing:
    Writes the normalized histograms of each image in the dataset into a text
    file. Also writes localized histograms to a different text file. Also 
    writes LUV matrices per image.

    Only need to run once tho. Once txt files are created, no need to run again.
*/
public class RunThisFirst {
    public static void main(String[] args) throws IOException {
        File dir = new File("images/");
        File[] directoryListing = dir.listFiles();
        BufferedWriter bwNH = new BufferedWriter(new FileWriter("NormalizedHistos.txt"));
        BufferedWriter bwLH = new BufferedWriter(new FileWriter("LocalizedHistos.txt"));
        BufferedWriter bwLUV = new BufferedWriter(new FileWriter("luv.txt"));
        
        for (File child : directoryListing) {
            System.out.println(child.getName());
            if(!child.getName().endsWith("jpg")) continue;
            Image i = new Image("images/"+child.getName());
            String toWrite = child.getName();
            bwNH.write(toWrite+"\r\n");
            bwNH.write(i.getStringArr(i.nh)+"\r\n");
            bwLH.write(toWrite+"\r\n");
            bwLH.write(i.getStringLH()+"\r\n");
            bwLUV.write(toWrite+"\r\n");
            bwLUV.write(i.nRows+" "+i.nCols+"\r\n");
            bwLUV.write(i.getStringLuv()+"\r\n");
        }
        bwNH.close();
        bwLH.close();
        bwLUV.close();
    }
}
