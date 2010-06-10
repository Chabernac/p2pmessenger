package chabernac.chat.gui;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;

import org.apache.log4j.Logger;

import chabernac.util.Tools;

public class TrueDialog extends JDialog {
  private static Logger LOGGER = Logger.getLogger(TrueDialog.class);
  private static final int NR_OF_PIXELS = 40;
  private static final int PIXEL_TRESHOLD = 80;

  private KeepInFrontListener myInFrontListener = null;
  private KeepInFrontRunnable myKeepInFrontRunnable = null;
  private boolean isKeepInFront = false;
  private Object myPaintLock = new Object();
  private boolean isInFront = false;
  private boolean isDebugEnabled = false;
  private BufferedImage myImage = null;

  public TrueDialog() throws HeadlessException {
  }

  public TrueDialog(Dialog anOwner) throws HeadlessException {
    super(anOwner);
  }

  public TrueDialog(Frame anOwner) throws HeadlessException {
    super(anOwner);
  }

  public TrueDialog(Dialog anOwner, boolean anModal) throws HeadlessException {
    super(anOwner, anModal);
  }

  public TrueDialog(Frame anOwner, boolean anModal) throws HeadlessException {
    super(anOwner, anModal);
  }

  public TrueDialog(Dialog anOwner, String anTitle) throws HeadlessException {
    super(anOwner, anTitle);
  }

  public TrueDialog(Frame anOwner, String anTitle) throws HeadlessException {
    super(anOwner, anTitle);
  }

  public TrueDialog(Dialog anOwner, String anTitle, boolean anModal)
  throws HeadlessException {
    super(anOwner, anTitle, anModal);
  }

  public TrueDialog(Frame anOwner, String anTitle, boolean anModal)
  throws HeadlessException {
    super(anOwner, anTitle, anModal);
  }

  public TrueDialog(Dialog anOwner, String anTitle, boolean anModal,
      GraphicsConfiguration anGc) throws HeadlessException {
    super(anOwner, anTitle, anModal, anGc);
  }

  public TrueDialog(Frame anOwner, String anTitle, boolean anModal,
      GraphicsConfiguration anGc) {
    super(anOwner, anTitle, anModal, anGc);
  }

  protected void dialogInit(){
    super.dialogInit();
    setResizable(false);
  }

  public boolean isInFront(){
    return isInFront;
  }

  private void testInFront(){
    try{
      if(!isVisible() || !isShowing()){
        isInFront = false;
      } else {
        BufferedImage theImage = new BufferedImage(myImage.getWidth(), myImage.getHeight(), myImage.getType());
        theImage.getGraphics().drawImage(myImage, 0, 0, null);
        
        Robot theRobot = new Robot();
        Rectangle theRectangle = new Rectangle(getX(), getY(), theImage.getWidth(), theImage.getHeight());
        BufferedImage theScreenShot = theRobot.createScreenCapture(theRectangle);

        boolean theIsInFront = equalImages(theImage, theScreenShot);
        if(isInFront && !theIsInFront){
          System.out.println("Verifying again");
          theScreenShot = theRobot.createScreenCapture(theRectangle);
          theIsInFront = equalImages(theImage, theScreenShot);
        }
        isInFront = theIsInFront;

      }
    }catch(Exception e){
      LOGGER.error("Error occured", e);
      isInFront = false;
    }
  }

  private boolean isValidScreenShot(BufferedImage anImage){
    int[] theRaster = anImage.getData().getPixels(0, 0, anImage.getWidth(), anImage.getHeight(), (int[])null);
    for(int i=0;i<theRaster.length;i += 500){
      System.out.println(new Color(theRaster[i]).toString());
    }
    return true;
  }

  public boolean forceToFront(){
    while(!isInFront){
      setVisible(true);
      Tools.requestFocus(this);
      toFront();
    }
    return true;
  }

  public void waitForBack(){
    while(isInFront && (isShowing() || isVisible())){
      repaint();
      synchronized (myPaintLock) {
        try {
          myPaintLock.wait(50);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    isInFront = false;
  }

  public void setVisible(boolean isVisible){
    super.setVisible(isVisible);
    synchronized(myPaintLock){
      myPaintLock.notifyAll();
    }
  }

  public void waitForDeactivated(){
    WindowListener theListener = new WindowListener();
    addWindowListener(theListener);
    addWindowStateListener(theListener);
    while(isActive()){
      synchronized(theListener){
        try{
          System.out.println("Waiting for deactivate");
          theListener.wait();
        }catch(Exception e){
          LOGGER.error("Waiting interrupted", e);
        }
      }
    }
    System.out.println("Removing listeners");
    removeWindowListener(theListener);
    removeWindowStateListener(theListener);
  }

  public void setKeepInFront(boolean isKeepInFront){
    this.isKeepInFront = isKeepInFront; 
    if(isKeepInFront){
      if(myInFrontListener == null){
        myInFrontListener = new KeepInFrontListener();
        addWindowListener(myInFrontListener);
        addWindowStateListener(myInFrontListener);
      }
      if(myKeepInFrontRunnable == null){
        myKeepInFrontRunnable = new KeepInFrontRunnable();
      }
      myKeepInFrontRunnable.startThread();
    } else {
      if(myInFrontListener != null){
        removeWindowListener(myInFrontListener);
        removeWindowStateListener(myInFrontListener);
      }
    }
  }

  private boolean equalImages(BufferedImage i1, BufferedImage i2){
    if(i1 == null ){
      LOGGER.debug("i1 is null");
      return false;
    }

    if(i2 == null ){
      LOGGER.debug("i2 is null");
      return false;
    }


    if(i1.getWidth() != i2.getWidth()) {
      System.out.println("width not equal: " + i1.getWidth() + "!=" + i2.getWidth());
      return false;
    }
    if(i1.getHeight() != i2.getHeight()) {
      System.out.println("height not equal: " + i1.getHeight() + "!=" + i2.getHeight());
      return false;
    }

    //for some reason the title border and some pixels left and right are never equal, so we ignore them
    i1 = i1.getSubimage(5, 30, i1.getWidth() - 10, i1.getHeight() - 35);
    i2 = i2.getSubimage(5, 30, i2.getWidth() - 10, i2.getHeight() - 35);


    int[] theRaster1 = i1.getData().getPixels(0, 0, i1.getWidth(), i1.getHeight(), (int[])null);
    int[] theRaster2 = i2.getData().getPixels(0, 0, i2.getWidth(), i2.getHeight(), (int[])null);

    int theStep = theRaster1.length / NR_OF_PIXELS;

    int counter = 0;
    int total = 0;

    for(int i=0;i<theRaster1.length;i+=theStep){
      int theRGB1 = 0x00FFFFFF & theRaster1[i];
      int theRGB2 = 0x00FFFFFF & theRaster2[i];

      if(theRGB1 == theRGB2){
        counter ++;
      }
      total++;
    }

    int percentage =  100 * counter / total;

    if(percentage < 100){
      System.out.println("Percentage: " +  percentage);
      if(isDebugEnabled){
        new ImageWindow(i1,i2, percentage);
      }
    }

    return percentage >= PIXEL_TRESHOLD;

  }



  public boolean isDebugEnabled() {
    return isDebugEnabled;
  }

  public void setDebugEnabled(boolean isDebugEnabled) {
    this.isDebugEnabled = isDebugEnabled;
  }

  public void paint(Graphics g){
    myImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    super.paint(myImage.getGraphics());
    g.drawImage(myImage, 0,0, null);
    testInFront();


    synchronized(myPaintLock){
      myPaintLock.notifyAll();
    }
    
  }

  private class WindowListener extends WindowAdapter{
    private Object myLock = null;

    public WindowListener(){
      this(null);
      myLock = this;
    }

    public WindowListener(Object aLock){
      myLock = aLock;
    }

    public void windowOpened(WindowEvent e) {
//    doNotify();
    }

    public void windowClosing(WindowEvent e) {
//    doNotify();
    }

    public void windowClosed(WindowEvent e) {
//    doNotify();
    }

    public void windowIconified(WindowEvent e) {
//    doNotify();
    }

    public void windowDeiconified(WindowEvent e) {
//    doNotify();
    }

    public void windowActivated(WindowEvent e) {
//    doNotify();
    }

    public void windowDeactivated(WindowEvent e) {
      System.out.println("Window deactivated: " + e);
      doNotify();
    }

    public void windowStateChanged(WindowEvent e) {
//    doNotify();
    }

    public void windowGainedFocus(WindowEvent e) {
//    doNotify();
    }

    public void windowLostFocus(WindowEvent e) {
//    doNotify();
    }

    private void doNotify(){
      synchronized(myLock){
        myLock.notifyAll();
      }
    }
  }

  private class KeepInFrontListener extends WindowAdapter{

    public KeepInFrontListener(){
    }


    public void windowOpened(WindowEvent e) {
      toFront();
    }

    public void windowClosing(WindowEvent e) {
      toFront();
    }

    public void windowClosed(WindowEvent e) {
      toFront();
    }

    public void windowIconified(WindowEvent e) {
      toFront();
    }

    public void windowDeiconified(WindowEvent e) {
      toFront();
    }

    public void windowActivated(WindowEvent e) {
      toFront();
    }

    public void windowDeactivated(WindowEvent e) {
      toFront();
    }

    public void windowStateChanged(WindowEvent e) {
      toFront();
    }

    public void windowGainedFocus(WindowEvent e) {
      toFront();
    }

    public void windowLostFocus(WindowEvent e) {
      //toFront();
    }

    private void toFront(){
      new Thread((new Runnable(){
        public void run(){
          if(isVisible()){
            forceToFront();
          }
        }
      })).start();
    }
  }

  private class KeepInFrontRunnable implements Runnable{
    private boolean isRunning = false;

    public void startThread(){
      if(!isRunning){
        new Thread(this).start();
      }
    }

    public void run() {
      isRunning = true;
      while(isKeepInFront){
        if(isVisible()){
          forceToFront();
        }
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          LOGGER.error("Could not sleep", e);
        }
      }
      isRunning = false;
    }
  }
}
