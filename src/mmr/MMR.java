package mmr;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MMR {

    public static void main(String[] args) {
        ImageData.init();
        String q = "100.jpg";
        double[] nh = ImageData.getNH(q);
        Image i1 = new Image(nh);
        File dir = new File("images/");
        File[] directoryListing = dir.listFiles();
        ArrayList<Answer> results = new ArrayList<>();
        for (File child : directoryListing) {
            if(!child.getName().endsWith("jpg")) continue;
            double[] curNH = ImageData.getNH(child.getName());
            Image i2 = new Image(curNH);
            double sim = i1.compareTo(i2, 0.005);
            results.add(new Answer(child.getName(), sim));
        }
        Collections.sort(results);
        for(int i=0; i<10; i++){
            System.out.println(results.get(i));
        }
    }
    
    static  class Answer implements Comparable<Answer>{
        String filename;
        double sim;
        
        public Answer(String f, double s){
            filename = f;
            sim = s;
        }

        @Override
        public int compareTo(Answer o) {
            return Double.compare(o.sim, sim);
        }
        
        public String toString(){
            return filename+": "+sim;
        }
    }
}
