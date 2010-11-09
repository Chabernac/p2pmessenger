/*
 * Created on 14-jun-2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.MemoryImageSource;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import chabernac.control.iSynchronizedEvent;
import chabernac.space.buffer.ZBuffer;
import chabernac.space.geom.Point3D;
import chabernac.space.shading.GouroudShading;



/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Frame3D extends JFrame implements iSynchronizedEvent{
  private static Logger LOGGER = Logger.getLogger(Frame3D.class);

  private static int RADIUS = 20;
  private static int AIMLINE = 4;
  private World myWorld = null;
  private Graphics3D myGraphics = null;
  private Camera myCamera = null;
  //private boolean stop = false;
  //private Thread myThread = null;
  //private SynchronizedTimer myTimer = null;
  private Graphics2D myG = null;
  private BufferStrategy myStrategy = null;

  public Frame3D(World aWorld, Camera aCamera, Dimension aResolution){
    super();
    setUndecorated(true);	
    setResizable(false);
    setIgnoreRepaint(true);
    setBackground(Color.black);
    myWorld = aWorld;
    myCamera = aCamera;
    //myTimer = aTimer;
    //startFullScreen();
    setSize(aResolution);
    setVisible(true);
    getGraphicsObject();
    setupGraphics3d();
    hideMouse();
    //startRendering();
  }

  private void getGraphicsObject(){
    //createBufferStrategy(2);
    //myStrategy =  getBufferStrategy();
    //myG = myStrategy.getDrawGraphics();
    myG = (Graphics2D)getGraphics();
    //myG.addRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ));

  }

  private void hideMouse(){
    int[] pixels = new int[16 * 16];
    pixels[128] = 255;
    Image image = Toolkit.getDefaultToolkit().createImage( new MemoryImageSource(16, 16, pixels, 0, 16));
    Cursor transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisiblecursor");
    setCursor(transparentCursor);
  }

  private void startFullScreen(){
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gs = ge.getDefaultScreenDevice();
    DisplayMode theCurrentMode = gs.getDisplayMode();
    LOGGER.debug( "Width: " + theCurrentMode.getWidth()+  " Height: " + theCurrentMode.getHeight()+  " depth: " + theCurrentMode.getBitDepth() + " refresh rate: " + theCurrentMode.getRefreshRate());
    gs.setFullScreenWindow(this);
    DisplayMode newDisplayMode = new DisplayMode(800,600,32, theCurrentMode.getRefreshRate());
    gs.setDisplayMode(newDisplayMode);

  }

  private void setupGraphics3d(){
    Point3D theEyePoint = new Point3D(getWidth()/2,getHeight()/2,(getWidth() + getHeight())/2);
    myGraphics = new Graphics3D(new ScreenFrustrum(theEyePoint, new Dimension(getWidth() - 1,getHeight() - 1)),
                                theEyePoint,
                                myCamera,
                                myWorld,
                                new ZBuffer(myWorld, getWidth(), getHeight()));
    myGraphics.setLightManager(new GouroudShading(0.3));
  }

  /*
  public void startRendering(){
    stop = false;
    new Thread(this).start();
  }

  public void stopRendering(){
    stop = true;
  }
   */

  /*
  public void run(){
  	createBufferStrategy(3);
  	BufferStrategy theStrategy =  getBufferStrategy();
  	long prevTime = 0 ;
  	long currentTime = System.currentTimeMillis() ;
  	int i=0;
  	int loops = 10;
  	while (!stop) {

  		if(i++ >= loops){
  			i = 0;
  			prevTime = currentTime;
  	  		currentTime = System.currentTimeMillis();
  	  		long diff = (long)((currentTime - prevTime) / loops);
  	  		int frameRate = (int)(1000 / diff);
  	  		Debug.log(Frame3D.class, "Drawing a frame every: " + diff + " milliseconds");
  	  		Debug.log(Frame3D.class, "Frame rate: " + frameRate); 
  		}

  		synchronized(myCamera){
	  		Graphics g = null;
	  		try {
	  			g = theStrategy.getDrawGraphics();
	  			g.clearRect(0,0,getWidth(),getHeight());
		        myGraphics.drawWorld(g);


		        g.setColor(Color.black);
		        g.setColor(Color.GRAY);
		        g.drawLine(getWidth() / 2 - 5, getHeight() / 2, getWidth() /  2 + 5, getHeight() / 2);
		        g.drawLine(getWidth() / 2 , getHeight() / 2 - 5, getWidth() /  2, getHeight() / 2 + 5);
		        g.setColor(new Color(150,150,255));

		        g.drawLine(getWidth() / 2 - RADIUS, getHeight() / 2, getWidth() /  2 - RADIUS + AIMLINE, getHeight() / 2);
		        g.drawLine(getWidth() / 2 + RADIUS, getHeight() / 2, getWidth() /  2 + RADIUS - AIMLINE, getHeight() / 2);
		        g.drawLine(getWidth() / 2 , getHeight() / 2 - RADIUS, getWidth() /  2, getHeight() / 2 - RADIUS + AIMLINE);
		        g.drawLine(getWidth() / 2 , getHeight() / 2 + RADIUS, getWidth() /  2, getHeight() / 2 + RADIUS - AIMLINE);
		        g.drawOval(getWidth() / 2 - RADIUS, getHeight() / 2 - RADIUS, RADIUS * 2,RADIUS * 2);

	  		} catch(Exception e){
	  			Debug.log(this,"Could not paint world",e);
	  		} finally {
	  			if(g!=null) g.dispose();
	  		}
	  		theStrategy.show();
  		}
  		//Thread.yield();
  		myTimer.waitForSynch();
  	}
}
   */

  public void executeEvent(long aCounter) {
    myGraphics.drawWorld(myG);
    //myStrategy.show();
  }

  public Graphics3D getGraphics3D(){
    return myGraphics;
  }

}
