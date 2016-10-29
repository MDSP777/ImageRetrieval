package mmr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/*
    Precomputing:
    Writes the normalized histograms of each image in the dataset into a text
    file. Also writes localized histograms to a different text file.  
    TODO: Modify this to precompute necessary info for the other three
    algorithms as well.

    Only need to run once tho. Once txt files are created, no need to run again.
*/
public class RunThisFirst {
    public static void main(String[] args) throws IOException {
        File dir = new File("images/");
        File[] directoryListing = dir.listFiles();
        BufferedWriter bwNH = new BufferedWriter(new FileWriter("NormalizedHistos.txt"));
        BufferedWriter bw50 = new BufferedWriter(new FileWriter("CenterHistos50.txt"));
        BufferedWriter bw75 = new BufferedWriter(new FileWriter("CenterHistos75.txt"));
        BufferedWriter bwLH = new BufferedWriter(new FileWriter("LocalizedHistos.txt"));
        for (File child : directoryListing) {
            System.out.println(child.getName());
            if(!child.getName().endsWith("jpg")) continue;
            Image i = new Image("images/"+child.getName());
            String toWrite = child.getName();
            bwNH.write(toWrite+"\r\n");
            bwNH.write(i.getStringArr(i.nh)+"\r\n");
            i.computeNHCenterNonCenter(0.5);
            bw50.write(toWrite+"\r\n");
            bw50.write(i.getStringArr(i.nhCenter)+"\r\n");
            bw50.write(i.getStringArr(i.nhNonCenter)+"\r\n");
            i.computeNHCenterNonCenter(0.75);
            bw75.write(toWrite+"\r\n");
            bw75.write(i.getStringArr(i.nhCenter)+"\r\n");
            bw75.write(i.getStringArr(i.nhNonCenter)+"\r\n");
            bwLH.write(toWrite+"\r\n");
            bwLH.write(i.getStringLH()+"\r\n");
        }
        bwNH.close();
        bw50.close();
        bw75.close();
        bwLH.close();
    }
}
