package chabernac.space;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import chabernac.control.iSynchronizedEvent;
import chabernac.space.buffer.ZBuffer;
import chabernac.space.geom.Point3D;
import chabernac.space.shading.GouroudShading;
import chabernac.utils.Debug;


public class Panel3D extends JPanel implements  iSynchronizedEvent, MouseListener, ComponentListener{
  private World myWorld = null;
  private Graphics3D myGraphics = null;
  private Camera myCamera = null;
  private Graphics myG = null;

  public Panel3D(World aWorld, Camera aCamera, Dimension aDimension){
    super(true);
    super.setSize(aDimension);
    super.setPreferredSize(aDimension);
    myWorld = aWorld;
    myCamera = aCamera;
    init();
    myG = getGraphics();
  }
  
  private void init(){
    //You should be able to focus this component, only than it will be able to
    //receive KeyEvents
    setFocusable(true);
    requestFocus();
    setDoubleBuffered(true);
    getGraphicsObject();
    setupGraphics3d();
    addMouseListener(this);
    addComponentListener(this);
  }

  private void getGraphicsObject(){
    myG = (Graphics2D)getGraphics();
  }


  protected void setupGraphics3d(){
    Point3D theEyePoint = new Point3D(getWidth()/2,getHeight()/2,(getWidth() + getHeight())/2);
    myGraphics = new Graphics3D(new ScreenFrustrum(theEyePoint, new Dimension(getWidth(),getHeight())),
        theEyePoint,
        myCamera,
        myWorld,
        new ZBuffer(myWorld, getWidth(), getHeight()));
    myGraphics.setLightManager(new GouroudShading(0.4));
  }


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
   * @param world
   */
  public void setWorld(World world) {
    myWorld = world;
  }
  
  public void paint(Graphics g){
  }

  public void executeEvent(long anCounter) {
    try{
      Graphics theGraphics = getGraphics();
      if(theGraphics != null){
        if(!hasFocus()){
          requestFocus();
        }
        
        //theGraphics.clearRect(0,0,getWidth(),getHeight());
        //super.paint(theGraphics);
        myGraphics.drawWorld(theGraphics);
        
      }
    }catch(Exception e){
      Debug.log(this,"Error occured while drawing world",e);
    }
  }

  public void componentHidden(ComponentEvent anE) {
  }

  public void componentMoved(ComponentEvent anE) {
  }

  public void componentResized(ComponentEvent anE) {
	  Point3D theEyePoint = new Point3D(getWidth()/2,getHeight()/2,(getWidth() + getHeight())/2);
	  myGraphics.setEyePoint(theEyePoint);
	  myGraphics.setFrustrum(new ScreenFrustrum(theEyePoint, new Dimension(getWidth(),getHeight())));
	  myGraphics.setBufferStrategy(new ZBuffer(myWorld, getWidth(), getHeight()));
  }

  public void componentShown(ComponentEvent anE) {
  }
}