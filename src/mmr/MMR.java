package mmr;

import java.awt.FlowLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MMR {

    public static void main(String[] args) {
        ImageData.init();
        // replace with desired query image
        String q = "12.jpg";
        double[] nh = ImageData.getNH(q);
        double[][][] lh = ImageData.getLH(q);
        Image i1 = new Image(nh, lh);
//        Image i1 = new Image("images/"+q);
        File dir = new File("images/");
        File[] directoryListing = dir.listFiles();
        ArrayList<Answer> results = new ArrayList<>();
        ArrayList<Answer> results2 = new ArrayList<>();
        for (File child : directoryListing) {
            if(!child.getName().endsWith("jpg")) continue;
            double[] curNH = ImageData.getNH(child.getName());
            double[][][] curLH = ImageData.getLH(child.getName());
            Image i2 = new Image(curNH, curLH);
//            Image i2 = new Image("images/"+child.getName());
            double sim = i1.algo1(i2, 0.005);
            double sim2 = i1.bonus(i2, 0.005);
            results.add(new Answer(child.getName(), sim));
            results2.add(new Answer(child.getName(), sim2));
        }
        Collections.sort(results);
        for(int i=0; i<10; i++){
            System.out.println(results.get(i));
        }
        System.out.println("");
        Collections.sort(results2);
        for(int i=0; i<10; i++){
            System.out.println(results2.get(i));
        }
        
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon("images/"+q)));
        frame.getContentPane().add(new JLabel(new ImageIcon("images/"+results.get(1).filename)));
        frame.getContentPane().add(new JLabel(new ImageIcon("images/"+results2.get(1).filename)));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
//        Image i1 = new Image("images/0.jpg");
//        Image i2 = new Image("images/253.jpg");
//        System.out.println(i1.algo1(i2, 0.005));
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
