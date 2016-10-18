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
    public static void main(String[] args) throws IOException{
        File dir = new File("images/");
        File[] directoryListing = dir.listFiles();
        BufferedWriter bw = new BufferedWriter(new FileWriter("NormalizedHistos.txt"));
        BufferedWriter bw2 = new BufferedWriter(new FileWriter("LocalizedHistos.txt"));
        for (File child : directoryListing) {
            System.out.println(child.getName());
            if(!child.getName().endsWith("jpg")) continue;
            Image i = new Image("images/"+child.getName());
            String toWrite = child.getName();
            bw.write(toWrite+"\r\n");
            bw.write(i.getStringNH()+"\r\n");
            bw2.write(toWrite+"\r\n");
            bw2.write(i.getStringLH()+"\r\n");
        }
        bw.close();
        bw2.close();
    }
}
