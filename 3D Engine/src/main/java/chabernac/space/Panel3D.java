package chabernac.space;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import chabernac.control.iSynchronizedEvent;
import chabernac.space.buffer.Graphics3D2D;
import chabernac.space.geom.Point3D;
import chabernac.space.shading.GouroudShading;
import chabernac.space.shading.iVertexShader;


public class Panel3D extends JPanel implements  iSynchronizedEvent, MouseListener, ComponentListener{
  private static final long serialVersionUID = 7762476402438200268L;
  private static final Logger LOGGER = Logger.getLogger(Panel3D.class);
  private static final float FAR_CLIPPING_PLANE = 10000F;
  private static final float NEAR_CLIPPING_PLANE = 1F;
  
  private World myWorld = null;
  private Graphics3D myGraphics = null;
  private Camera myCamera = null;
  private Graphics2D myG = null;

  public Panel3D(World aWorld, Camera aCamera, Dimension aDimension){
    super(true);
    super.setSize(aDimension);
    super.setPreferredSize(aDimension);
    myWorld = aWorld;
    myCamera = aCamera;
    init();
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
    if(myG == null){
      myG = (Graphics2D)getGraphics();
    }
  }


  protected void setupGraphics3d(){
    Point3D theEyePoint = new Point3D(getWidth()/2,getHeight()/2,(getWidth() + getHeight())/2);
    myGraphics = new Graphics3D(new ScreenFrustrum(theEyePoint, new Dimension(getWidth(),getHeight()), NEAR_CLIPPING_PLANE, FAR_CLIPPING_PLANE),
        theEyePoint,
        myCamera,
        myWorld,
        new Graphics3D2D(myWorld, getWidth(), getHeight()));
    myGraphics.setVertexShaders(new iVertexShader[]{new GouroudShading((float)0.4)});
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

  public boolean executeEvent(long anCounter) {
    try{
      Graphics g = getGraphics();
      if(g != null){
        if(!hasFocus()){
          requestFocus();
        }
        
        //theGraphics.clearRect(0,0,getWidth(),getHeight());
        //super.paint(theGraphics);
        myGraphics.drawWorld(g, anCounter);
        
      }
    }catch(Exception e){
      LOGGER.error("Error occured while drawing world",e);
    }
    return true;
  }
  
  public boolean isRecordable(){
    return false;
  }

  public void componentHidden(ComponentEvent anE) {
  }

  public void componentMoved(ComponentEvent anE) {
  }

  public void componentResized(ComponentEvent anE) {
	  Point3D theEyePoint = new Point3D(getWidth()/2,getHeight()/2,(getWidth() + getHeight())/2);
	  myGraphics.setEyePoint(theEyePoint);
	  myGraphics.setFrustrum(new ScreenFrustrum(theEyePoint, new Dimension(getWidth(),getHeight()), NEAR_CLIPPING_PLANE, FAR_CLIPPING_PLANE));
	  myGraphics.getGraphics3D2D().setDimensions( getWidth(), getHeight() );
	  myGraphics.setSingleFullRepaint(true);
  }

  public void componentShown(ComponentEvent anE) {
  }
}