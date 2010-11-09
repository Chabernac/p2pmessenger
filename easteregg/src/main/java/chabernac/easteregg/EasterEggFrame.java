package chabernac.easteregg;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

public class EasterEggFrame extends JFrame implements Runnable, iEasterEggWindow{
  private static Logger LOGGER = Logger.getLogger(EasterEggFrame.class);

  private iPaintable myPaintable = null;
  private boolean stop = false;
  private Graphics myGraphics = null;
  private BufferedImage myScreenshot = null;
  private Rectangle myRect = null;
  private boolean isRunning = false;
  private Dimension myResolution = null;
  private GraphicsDevice myDevice = null;
  private iEasterEggWindowListener myListener = null;

  public EasterEggFrame(iPaintable aPaintable){
    this(aPaintable, null);
  }

  public EasterEggFrame(iPaintable aPaintable, Dimension aResolution){
    try{
      myPaintable = aPaintable;
      myResolution = aResolution;
      initFullScreen();
    }catch(Exception e){

    }
  }


  private void initFullScreen() throws AWTException{
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    myDevice = ge.getScreenDevices()[0];
    Robot theRobot = new Robot(myDevice);

    myRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    myScreenshot = theRobot.createScreenCapture(myRect);

    setUndecorated(true);

    myDevice.setFullScreenWindow(this);

    if(myResolution != null){
      System.out.println("Using resolution: " + myResolution);
      DisplayMode theCurrentMode = myDevice.getDisplayMode();
      DisplayMode newDisplayMode = new DisplayMode(myResolution.width,myResolution.height,32, theCurrentMode.getRefreshRate());
      myDevice.setDisplayMode(newDisplayMode);
      myRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    }

    addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent anE) {
        stop = true;
      }
    });
    setFocusable(true);
    requestFocus();
    createBufferStrategy(3);
    myGraphics = getGraphics();
    myGraphics.setClip(0,0,myRect.width, myRect.height);
    System.out.println("Bounds: " + myGraphics.getClipBounds());

  }

  public void run() {
    isRunning = true;
    BufferedImage theImage = new BufferedImage(myRect.width, myRect.height, BufferedImage.TYPE_INT_RGB);
    theImage.getGraphics().drawImage(myScreenshot, 0,0, null);
    while(!stop){
      try{
//				System.out.println("widht: " + myRect.width + " height:"   + myRect.height);
        //BufferedImage theImage = new BufferedImage(myRect.width, myRect.height, BufferedImage.TYPE_INT_RGB);
        //theImage.getGraphics().setClip(0,0,myRect.width, myRect.height);
        //theImage.getGraphics().drawImage(myScreenshot, 0,0, null);
        myPaintable.paint(theImage.getGraphics(), myRect, myScreenshot);
//				myPaintable.paint(myGraphics);
        myGraphics.drawImage(theImage, 0,0, null);
        if(!hasFocus()){
          requestFocus();
        }
        Thread.sleep(30);
      }catch(Exception e){
        LOGGER.error("An error occured while sleeping",e); 
      }
    }
    myDevice.setFullScreenWindow(null);
    dispose();
    
    isRunning = false;
    myScreenshot = null;
    myListener.easterEggWindowClosed();
  }


  public boolean isRunning() {
    return isRunning;
  }


  public void start() {
    stop = false;
    new Thread(this).start();
  }


  public void stop() {
    stop = true;
  }


  public Dimension getMyResolution() {
    return myResolution;
  }


  public void setMyResolution(Dimension myResolution) {
    this.myResolution = myResolution;
  }

  public void setEasterEggWindowListener( iEasterEggWindowListener aListener ) {
    myListener = aListener;
  }



}
