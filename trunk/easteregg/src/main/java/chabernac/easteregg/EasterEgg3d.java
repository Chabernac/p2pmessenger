package chabernac.easteregg;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import chabernac.space.Camera;
import chabernac.space.Graphics3D;
import chabernac.space.LightSource;
import chabernac.space.OneLocationTranslateManager;
import chabernac.space.RotationManager;
import chabernac.space.ScreenFrustrum;
import chabernac.space.Shape;
import chabernac.space.ShapeFactory;
import chabernac.space.World;
import chabernac.space.buffer.ZBuffer;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;
import chabernac.space.shading.GouroudShading;

public class EasterEgg3d extends DefaultEasterEggPaintable {
  private World myWorld = null;
  private Graphics3D myGraphics = null;
  private Point3D myEyePoint = null;
  private Camera myCamera = null;
  private Rectangle myBounds = null;
  private RotationManager myRotationManager = null;
  private int myCounter = 0;

  public EasterEgg3d(JFrame aRootFrame){
    super(aRootFrame);
    setResolution(new Dimension(1024,768));
  }

  private void buildWorld(){
    myWorld = new World(1);

    MouseTranslationManager theMouseTranslationManager = new MouseTranslationManager(100, 10);
    ((Component)myWindow).addMouseMotionListener(theMouseTranslationManager);
    ((Component)myWindow).addMouseWheelListener(theMouseTranslationManager);

    myRotationManager = new RotationManager(new Rotation(Math.PI / 180,Math.PI / 120,Math.PI / 80));
//  TranslateManager theManager2 = new RotationManager(new Rotation(Math.PI / 180,Math.PI / 60,Math.PI / 40));
//  TranslateManager theManager3 = new PathTranslateManager(new Point3D[]{new Point3D(-300,100,100), new Point3D(-300,-200,200), new Point3D(300,-200,100)}, 10);

    myWorld.getTranslateManagerContainer().addTranslateManager(myRotationManager);
//  myWorld.getTranslateManagerContainer().addTranslateManager(theManager2);
//  myWorld.getTranslateManagerContainer().addTranslateManager(theManager3);
    myWorld.getTranslateManagerContainer().addTranslateManager(theMouseTranslationManager);

    Shape theShape = ShapeFactory.makeCube(new Point3D(0,0,1000), 94,94,94);
    theShape.setTexture("axa", "axa", false, false);
//    theShape.myPolygons[0].setTexture("guy", null, false, false);
//    theShape.myPolygons[1].setTexture("leslie", null, false, false);
    myWorld.addShape(theShape);
    myRotationManager.addTranslatable(theShape);
//  theManager3.addTranslatable(theShape);
    theMouseTranslationManager.addTranslatable(theShape);

    /*
    theShape = new Shape(6);
    ShapeFactory.makeCube(theShape, new Point3D(-100,50,50), 94,94,94);
    theShape.setTexture("axa");
    theShape.myPolygons[0].setTexture("guy");
    theShape.myPolygons[3].setTexture("guy");
    myWorld.addShape(theShape);
    theManager2.addTranslatable(theShape);
     */

    //myWorld.addLightSource(new LightSource(new Point3D(0,0,-50), 100));
    myWorld.addLightSource(new LightSource(new Point3D(0,200,0), 500));

    myCamera = new Camera();

  }

  public void paint(Graphics aG, Rectangle aBounds, BufferedImage anBackGround) {
    if(myGraphics == null || myBounds == null || !myBounds.equals(aBounds)){
      myBounds = aBounds;
      buildWorld();
      initGraphics(aBounds);
    }

    myCounter++;

    if(myCounter % 100 == 0){
      Rotation theRotation = myRotationManager.getRotation();
      Rotation theNewRotation = new Rotation(theRotation.getPitch(), theRotation.getYaw(), theRotation.getRoll());
      myRotationManager.setRotation(theNewRotation);
    }

    myGraphics.drawWorld(aG);
  }

  private void initGraphics(Rectangle aBounds){
    ZBuffer theBuffer = new ZBuffer(myWorld, aBounds.width, aBounds.height);
    theBuffer.setBufferEnabled(true);
    myEyePoint = new Point3D(aBounds.width/2,aBounds.height/2,(aBounds.width + aBounds.height)/2);
    myGraphics = new Graphics3D(new ScreenFrustrum(myEyePoint, new Dimension(aBounds.width, aBounds.height)),
        myEyePoint,
        myCamera,
        myWorld,
        theBuffer
        );
    myGraphics.setLightManager(new GouroudShading(0.2));
    myGraphics.setBackGroundColor( Color.black );
  }

  private class MouseTranslationManager extends OneLocationTranslateManager implements MouseMotionListener, MouseWheelListener{
    private int myDepth;
    private int myCurrentDepth;

    public MouseTranslationManager(int aDepth, int aSpeed){
      super(new Point3D(0,0,aDepth), aSpeed);
      myDepth = aDepth;
      myCurrentDepth = aDepth;
    }


    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
      if(myEyePoint != null){
        setDestination(new Point3D(e.getX() - myEyePoint.x, myEyePoint.y - e.getY(), myCurrentDepth));
      }
    }


    public void mouseWheelMoved(MouseWheelEvent e) {
      if(myEyePoint != null){
        myCurrentDepth += 50 * (e.getWheelRotation() * e.getScrollAmount());
        if(myCurrentDepth < myDepth){
          myCurrentDepth = myDepth;
        }
        setDestination(new Point3D(e.getX() - myEyePoint.x, myEyePoint.y - e.getY(), myCurrentDepth));
      }
    }

  }

}
