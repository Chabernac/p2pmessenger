package chabernac.webcam;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import chabernac.gui.ImagePanel;

public class JPGWebCamCaptureTest {

  /**
   * @param args
   * @throws WCException 
   * @throws IOException 
   */
  public static void main(String[] args) throws WCException, IOException {
    JPGWebCamCapture theCamCaputure = new JPGWebCamCapture(200, 200, 0.8f);
    byte[] theJPG = theCamCaputure.capture();
    
    BufferedImage theImage = ImageIO.read(new ByteArrayInputStream(theJPG));
    
    JFrame theFrame = new JFrame();
    theFrame.getContentPane().setLayout(new BorderLayout());
    theFrame.getContentPane().add(new ImagePanel(theImage));
    theFrame.setSize(500, 500);
    theFrame.setVisible(true);
    
  }

}
