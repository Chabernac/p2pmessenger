package chabernac.web;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import chabernac.gui.ImagePanel;
import chabernac.web.JPGWebCapture;
import chabernac.web.WCException;

public class JPGWebCaptureTest {

  /**
   * @param args
   * @throws WCException 
   * @throws IOException 
   */
  public static void main(String[] args) throws WCException, IOException {
    JPGWebCapture theCamCaputure = new JPGWebCapture();
    byte[] theJPG = theCamCaputure.capture(320, 240, 0.5f);
    
    System.out.println("bytes: " + theJPG.length);
    
    BufferedImage theImage = ImageIO.read(new ByteArrayInputStream(theJPG));
    
    JFrame theFrame = new JFrame();
    theFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); 
    theFrame.getContentPane().setLayout(new BorderLayout());
    theFrame.getContentPane().add(new ImagePanel(theImage));
    theFrame.setSize(10, 10);
    theFrame.setVisible(true);
    
  }

}
