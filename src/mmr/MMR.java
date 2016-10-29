package mmr;

import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MMR {

    public static void main(String[] args) {
        ImageData.init();
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("Enter filename of query image: ");
            String q = sc.next();
            if(!new File("images/"+q).exists()) 
                throw new RuntimeException("Error, file "+q+" does not exist.");
            System.out.println("Choose your desired algorithm:");
            System.out.println("1- Color Histogram");
            System.out.println("2- Color Histogram with Perceptual Similarity");
            System.out.println("3- Color Histogram with Centering Refinement");
            System.out.println("4- Color Histogram with Color Coherence");
            System.out.println("5- Localized Color Histograms");
            int choice = sc.nextInt();
            double delta;
            switch(choice){
                case 1:
                    System.out.print("Enter delta value: ");
                    delta = sc.nextDouble();
                    performCH(q, delta);
                    break;
                case 2:
                    System.out.println("Not yet implemented. Lol");
                    break;
                case 3: 
                    System.out.println("Select refinement level: ");
                    System.out.println("1- 50%");
                    System.out.println("2- 75%");
                    int refinement = sc.nextInt();
                    System.out.print("Enter delta value: ");
                    delta = sc.nextDouble();
                    performCentering(q, refinement, delta);
                    break;
                case 4:
                    System.out.println("Not yet implemented. Lol");
                    break;
                case 5:
                    System.out.print("Enter delta value: ");
                    delta = sc.nextDouble();
                    performLH(q, delta);
                    break;
                default:
                    break;
            }
        }
    }

    private static void performCH(String q, double delta) {
        double[] nh = ImageData.getNH(q);
        Image i1 = new Image(nh);
        File dir = new File("images/");
        File[] directoryListing = dir.listFiles();  
        ArrayList<Answer> results = new ArrayList<>();
        for (File child : directoryListing) {
            if(!child.getName().endsWith("jpg")) continue;
            double[] curNH = ImageData.getNH(child.getName());
            Image i2 = new Image(curNH);
            double sim = i1.ch(i2, 0.005);
            results.add(new Answer(child.getName(), sim));
        }
        Collections.sort(results);
        createFrame(q, results);
    }
    
    private static void performCentering(String q, int refinement, double delta) {
        double[] center;
        double[] noncenter;
        double centerPercent;
        if(refinement==1){
            center = ImageData.getCenter50(q);
            noncenter = ImageData.getNonCenter50(q);
            centerPercent = 0.5;
        } else {
            center = ImageData.getCenter75(q);
            noncenter = ImageData.getNonCenter75(q);
            centerPercent = 0.75;
        }
        Image i1 = new Image(center, noncenter);
        File dir = new File("images/");
        File[] directoryListing = dir.listFiles();  
        ArrayList<Answer> results = new ArrayList<>();
        for (File child : directoryListing) {
            if(!child.getName().endsWith("jpg")) continue;
            double[] curCenter;
            double[] curNoncenter;
            if(refinement==1){
                curCenter = ImageData.getCenter50(child.getName());
                curNoncenter = ImageData.getNonCenter50(child.getName());
            } else {
                curCenter = ImageData.getCenter75(child.getName());
                curNoncenter = ImageData.getNonCenter75(child.getName());
            }
            Image i2 = new Image(curCenter, curNoncenter);
            double sim = i1.chWithCentering(i2, delta, centerPercent);
            results.add(new Answer(child.getName(), sim));
        }
        Collections.sort(results);
        createFrame(q, results);
    }
    
    private static void performLH(String q, double delta) {
        double[][][] lh = ImageData.getLH(q);
        Image i1 = new Image(lh);
        File dir = new File("images/");
        File[] directoryListing = dir.listFiles();  
        ArrayList<Answer> results = new ArrayList<>();
        for (File child : directoryListing) {
            if(!child.getName().endsWith("jpg")) continue;
            double[][][] curLH = ImageData.getLH(child.getName());
            Image i2 = new Image(curLH);
            double sim = i1.bonus(i2, 0.005);
            results.add(new Answer(child.getName(), sim));
        }
        Collections.sort(results);
        createFrame(q, results);
    }

    private static void createFrame(String q, ArrayList<Answer> results) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new GridLayout(2, 1));
        JPanel p1 = new JPanel();
        JLabel srcLabel = new JLabel(new ImageIcon("images/"+q));
        srcLabel.setText(q);
        srcLabel.setHorizontalTextPosition(JLabel.CENTER);
        srcLabel.setVerticalTextPosition(JLabel.BOTTOM);
        p1.add(srcLabel);
        frame.getContentPane().add(p1);
        JPanel p2 = new JPanel();
        for(int i=1; i<=10; i++){
            JLabel ansLabel = new JLabel(new ImageIcon("images/"+results.get(i).filename));
            ansLabel.setText(results.get(i).filename);
            ansLabel.setHorizontalTextPosition(JLabel.CENTER);
            ansLabel.setVerticalTextPosition(JLabel.BOTTOM);
            p2.add(ansLabel);
        }
        frame.getContentPane().add(p2);
        frame.setSize(1400, 350);
        frame.setResizable(false);
        frame.setVisible(true);
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
