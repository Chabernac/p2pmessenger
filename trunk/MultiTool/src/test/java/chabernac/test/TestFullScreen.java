package chabernac.test;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class TestFullScreen {

  /**
   * @param args
   */
  public static void main(String[] args) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] theDevices = ge.getScreenDevices();
    for(int i=0;i<theDevices.length;i++){
      System.out.println(i + ": " + theDevices[i]);
    }
    JFrame theFrame = new JFrame();
    theFrame.setUndecorated(true);
    theDevices[0].setFullScreenWindow(theFrame);
    
    theFrame.addKeyListener(new KeyAdapter(){

      public void keyPressed(KeyEvent anE) {
        System.exit(0);
      }
    });

  }

}
