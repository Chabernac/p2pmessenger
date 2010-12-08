package chabernac.space;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import chabernac.control.SynchronizedTimer;
import chabernac.control.iSynchronizedEvent;
import chabernac.space.buffer.ZBuffer;
import chabernac.space.geom.Point3D;
import chabernac.utils.Debug;


public class Panel3D extends JPanel implements  iSynchronizedEvent, MouseListener{
  private World myWorld = null;
  private Graphics3D myGraphics = null;
  private Camera myCamera = null;
  private SynchronizedTimer myTimer = null;
  private Graphics2D myG = null;

  public Panel3D(World aWorld, Camera aCamera, Dimension aDimension, SynchronizedTimer aTimer){
    super(true);
    super.setSize(aDimension);
    super.setPreferredSize(aDimension);
    //You should be able to focus this component, only than it will be able to
    //receive KeyEvents
    setFocusable(true);
    setDoubleBuffered(true);
    myWorld = aWorld;
    myCamera = aCamera;
    myTimer = aTimer;
    setupGraphics3d();
    getGraphicsObject();
    addMouseListener(this);
  }

  private void getGraphicsObject(){
    myG = (Graphics2D)getGraphics();
  }


  private void setupGraphics3d(){
    Point3D theEyePoint = new Point3D(getWidth()/2,getHeight()/2,(getWidth() + getHeight())/2);
    myGraphics = new Graphics3D(new ScreenFrustrum(theEyePoint, new Dimension(getWidth(),getHeight())),
        theEyePoint,
        myCamera,
        myWorld,
        new ZBuffer(getWidth(), getHeight()));
  }

  /*
  public void paint(Graphics g){
    try{
      g.clearRect(0,0,getWidth(),getHeight());
      super.paint(g);
      //long theTime1 = System.currentTimeMillis();
      myGraphics.drawWorld(g);
      //long theTime2 = System.currentTimeMillis();
      //Debug.log(this,"World drawn in: " + (theTime2 - theTime1));
    }catch(Exception e){
      Debug.log(this,"Error occured while drawing world",e);
    }
  }
  */

  //You should not use this method to change the size of the window
  //The size of the panel has to be given in the constructor
  public final void setSize(Dimension d){}

  public void mouseClicked(MouseEvent e){ requestFocus(); }
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}
  public void mousePressed(MouseEvent e){}
  public void mouseReleased(MouseEvent e){}
  /**
   * @return
   */
  public Camera getCamera() {
    return myCamera;
  }

  /**
   * @return
   */
  public Graphics3D getGraphics3D() {
    return myGraphics;
  }

  /**
   * @return
   */
  public SynchronizedTimer getTimer() {
    return myTimer;
  }

  /**
   * @return
   */
  public World getWorld() {
    return myWorld;
  }

  /**
   * @param camera
   */
  public void setCamera(Camera camera) {
    myCamera = camera;
  }

  /**
   * @param graphics3D
   */
  public void setGraphics3D(Graphics3D graphics3D) {
    myGraphics = graphics3D;
  }

  /**
   * @param timer
   */
  public void setTimer(SynchronizedTimer timer) {
    myTimer = timer;
  }

  /**
   * @param world
   */
  public void setWorld(World world) {
    myWorld = world;
  }

  public void executeEvent(long anCounter) {
    try{
      myG.clearRect(0,0,getWidth(),getHeight());
      super.paint(myG);
      myGraphics.drawWorld(myG);
    }catch(Exception e){
      Debug.log(this,"Error occured while drawing world",e);
    }
  }

}