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
        Scanner sc = new Scanner(System.in);
        ImageData.init();
        while(true){
            System.out.print("Enter filename of query image: ");
            String q = sc.next();
            if(!q.endsWith(".jpg")) q+=".jpg";
            if(!new File("images/"+q).exists()) 
                throw new RuntimeException("Error, file "+q+" does not exist.");
            System.out.println("Choose your desired algorithm:");
            System.out.println("1- Color Histogram");
            System.out.println("2- Color Histogram with Perceptual Similarity");
            System.out.println("3- Color Histogram with Color Coherence");
            System.out.println("4- Color Histogram with Centering Refinement");
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
                    System.out.print("Enter connectiveness: ");
                    int conn = sc.nextInt();
                    performCCV(q, conn);
                    break;
                case 4: 
                    System.out.print("Enter center percentage: ");
                    double centerPercent = sc.nextDouble();
                    performCentering(q, centerPercent);
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
        Image i1 = new Image(ImageData.getNH(q));
        File dir = new File("images/");
        File[] directoryListing = dir.listFiles();  
        ArrayList<Answer> results = new ArrayList<>();
        for (File child : directoryListing) {
            if(!child.getName().endsWith("jpg")) continue;
            if(child.getName().equals(q)) continue;
            Image i2 = new Image(ImageData.getNH(child.getName()));
            double sim = i1.ch(i2, delta);
            results.add(new Answer(child.getName(), sim));
        }
        Collections.sort(results);
        createFrame("CH, delta="+delta, q, results);
    }
    
    private static void performCentering(String q, double centerPercent) {
        Image i1 = new Image(ImageData.getLuv(q));
        File dir = new File("images/");
        File[] directoryListing = dir.listFiles();  
        ArrayList<Answer> results = new ArrayList<>();
        for (File child : directoryListing) {
            if(!child.getName().endsWith("jpg")) continue;
            if(child.getName().equals(q)) continue;
            Image i2 = new Image(ImageData.getLuv(child.getName()));
            double sim = i1.chWithCentering(i2, centerPercent);
            results.add(new Answer(child.getName(), sim));
        }
        Collections.sort(results);
        createFrame("CH with Centering ("+centerPercent+")", q, results);
    }
    
    private static void performCCV(String q, int connectiveness) {
        Image i1 = new Image(ImageData.getLuv(q));
        File dir = new File("images/");
        File[] directoryListing = dir.listFiles();  
        ArrayList<Answer> results = new ArrayList<>();
        for (File child : directoryListing) {
            if(!child.getName().endsWith("jpg")) continue;
            if(child.getName().equals(q)) continue;
            Image i2 = new Image(ImageData.getLuv(child.getName()));
            double sim = i1.chWithCCV(i2, connectiveness);
            results.add(new Answer(child.getName(), sim));
        }
        Collections.sort(results);
        createFrame("CH with CCV, "+connectiveness+"-conectiveness", q, results);
    }
    
    private static void performLH(String q, double delta) {
        Image i1 = new Image(ImageData.getLH(q));
        File dir = new File("images/");
        File[] directoryListing = dir.listFiles();  
        ArrayList<Answer> results = new ArrayList<>();
        for (File child : directoryListing) {
            if(!child.getName().endsWith("jpg")) continue;
            if(child.getName().equals(q)) continue;
            Image i2 = new Image(ImageData.getLH(child.getName()));
            double sim = i1.bonus(i2, delta);
            results.add(new Answer(child.getName(), sim));
        }
        Collections.sort(results);
        createFrame("LH (25 blocks), delta="+delta, q, results);
    }

    private static void createFrame(String title, String q, ArrayList<Answer> results) {
        JFrame frame = new JFrame();
        frame.setTitle(title);
        frame.getContentPane().setLayout(new GridLayout(2, 1));
        JPanel p1 = new JPanel();
        JLabel srcLabel = new JLabel(new ImageIcon("images/"+q));
        srcLabel.setText(q);
        srcLabel.setHorizontalTextPosition(JLabel.CENTER);
        srcLabel.setVerticalTextPosition(JLabel.BOTTOM);
        p1.add(srcLabel);
        frame.getContentPane().add(p1);
        JPanel p2 = new JPanel();
        for(int i=0; i<10; i++){
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
