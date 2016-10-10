//package mmr;
//
//import java.awt.*;
//import java.awt.event.*;
//import com.sun.image.codec.jpeg.*;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import javax.swing.*;
//import java.awt.image.ColorModel;
//
//class ImageFrame extends JFrame {
//
//    JTextField jTextArea1 = new JTextField();
//    JTextField jTextArea2 = new JTextField();
//
//    //shows a JPEG on the screen on the screen at x, y
//    public void showJPEG(int x, int y, Graphics2D g2, String filename ) {
//        BufferedImage bi = null;        
//        String outputFileName =  filename;
//
//        try {
//            File file = new File(outputFileName);
//            FileInputStream in = new FileInputStream(file);
//
//            // decodes the JPEG data stream into a BufferedImage
//            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
//            bi = decoder.decodeAsBufferedImage();
//        } catch (Exception ex) {
//            System.out.println("shit");
//        }
//
//        if (bi == null) {
//            return;
//        }
//        g2.drawImage(bi, x, y, this);
//        this.repaint();
//    }
//	
//    public ImageFrame() {
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                dispose();
//                System.exit(0);
//            }
//        });
//    }
//
//    public void init(){
//        Graphics g = this.getGraphics();
//        Graphics2D g2 = (Graphics2D) g;
//        showJPEG(1,50,g2,"images/1.jpg");
//    }
//    
//    public void getRGB(int x, int y, String Filename){     
//       //gets the RGB and Luv value at x, y    	
//       BufferedImage bi1 = null;
//       int RGB1;
//       int i,j;
//       int totalPixels;
//
//       try {
//            File file = new File(Filename);
//            FileInputStream in = new FileInputStream(file);
//
//            // decodes the JPEG data stream into a BufferedImage
//            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(in);
//            bi1 = decoder.decodeAsBufferedImage();
//        } catch (Exception ex) {
//        }
//
//        if (bi1 == null) {
//            /*null file*/
//            return;
//        }
//
//        totalPixels = bi1.getHeight() * bi1.getWidth();
//
//        ColorModel CM = bi1.getColorModel();
// 
//        RGB1 = bi1.getRGB(x, y); 
//        
//        double R = CM.getRed(RGB1);
//        double G = CM.getGreen(RGB1);
//        double B = CM.getBlue(RGB1);	
//        CieConvert ColorCIE = new CieConvert();
//        ColorCIE.setValues(R/255.0, G/255.0, B/255.0);
//        
//        jTextArea2.setText( "RGB:(" +
//                                    Double.toString(R) + "," +
//                                    Double.toString(G) + "," +
//                                    Double.toString(B) + ")" +
//                                    "-> "+Integer.toString(ColorCIE.IndexOf()));
//        jTextArea1.setText("  = LUV:(\n" +
//                                    Double.toString(ColorCIE.L) + "," +
//                                    Double.toString(ColorCIE.u) + "," +
//                                    Double.toString(ColorCIE.v) + ")" );    				    
//        this.repaint();
//    }
//    
//        
//    public static void main(String args[]) {
//        JPanel panelCenter = new JPanel();
//        System.out.println("Starting Image...");
//        ImageFrame mainFrame = new ImageFrame();
//
//        panelCenter.setSize(100,100);
//        mainFrame.getContentPane().add(panelCenter, BorderLayout.NORTH);
//
//        mainFrame.jTextArea1.setLocation(20,230);
//        mainFrame.jTextArea1.setSize(400,100);
//        mainFrame.getContentPane().add(mainFrame.jTextArea1);
//
//        mainFrame.jTextArea2.setLocation(20,331);
//        mainFrame.jTextArea2.setSize(200,100);
//        mainFrame.getContentPane().add(mainFrame.jTextArea2);
//
//        mainFrame.setSize(600, 400);
//        mainFrame.setTitle("Image");
//        mainFrame.setVisible(true);
//
//        mainFrame.getRGB(50,50,"images/1.jpg");		
//
//        mainFrame.init();
//    }
//}
//
