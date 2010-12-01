package chabernac.worlds;

import java.awt.Color;
import java.awt.Dimension;

import chabernac.control.iSynchronizedEvent;
import chabernac.space.LightSource;
import chabernac.space.MouseTranslationManager;
import chabernac.space.RotationManager;
import chabernac.space.Shape;
import chabernac.space.ShapeFactory;
import chabernac.space.World;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;

public class AXACube extends AbstractWorld implements iSynchronizedEvent{
  private static final long serialVersionUID = 3362645587237603262L;
   
  private RotationManager myRotationManager = null;
  
  protected void buildWorld(World aWorld){
//    myManager.addSyncronizedEvent(this);
    
    aWorld.addLightSource(new LightSource(new Point3D(0,2000,-5000), 5000));
    
    MouseTranslationManager theMouseTranslationManager = new MouseTranslationManager(myPanel3D.getGraphics3D(), 100, 10);
    myRotationManager = new RotationManager(new Rotation(Math.PI / 180,Math.PI / 120,Math.PI / 60));
    RotationManager theRotationManager = new RotationManager(new Rotation(0,0,Math.PI / 180));
    myWorld.getTranslateManagerContainer().addTranslateManager(theMouseTranslationManager);
    myWorld.getTranslateManagerContainer().addTranslateManager(myRotationManager);
    myWorld.getTranslateManagerContainer().addTranslateManager(theRotationManager);
    
    
//    Shape theSphere = ShapeFactory.makeSphere(new Point3D(100,100,4800), 2000,10);
//    theSphere.setColor(Color.blue);
//    theSphere.setTexture("gengrid", false, true);
//    theSphere.setTexture("EarthMap_2500x1250", false, true);
//    theSphere.setTexture("Threadplate0069_1_S", false, true);
//    theSphere.setTexture("S_S_Board12", false, true);
    
    
//    theSphere.setTexture("MarsMap_2500x1250", false, true);
//    theSphere.setTexture("MoonMap_2500x1250", false, true);
//    theSphere.setTexture("MoonMap2_2500x1250", false, true);
    
//    theSphere.setTexture("VenusMap_2500x1250", false, true);
    
    //theSphere.setTexture(new TextureImage(ImageFactory.createImage("Guy", new Font("Arial", Font.BOLD, 20), 100, 100, Color.BLUE, Color.WHITE, true)));
//    theSphere.done();
    //theSphere.myPolygons[20].setTexture("guy", false);
    //myWorld.addShape(theSphere);
    //theMouseTranslationManager.addTranslatable(theSphere);
    //theRotationManager.addTranslatable(theSphere);
    
    
    
//    Shape theCosmos = ShapeFactory.makeSphere(new Point3D(0,0,0), 100000,40);
//    theCosmos.setColor(Color.blue);
//    theCosmos.setRoom(true);
    //theCosmos.setTexture("StarsMap_2500x1250", false, true);
//    theCosmos.setTexture("star_map_small", false, true);
    
    //theCosmos.setTexture("Threadplate0069_1_S", false, true);
    
//    theCosmos.setTexture("EarthMap_2500x1250", false, true);
//    theCosmos.done();
    //myWorld.addShape(theCosmos);
    
    
    
    
    
    
    
    /*
    BufferedImage theImage = ImageFactory.createImage("AXA", new Font("Arial", Font.BOLD, 80), 1, 1, Color.white, Color.black, true);
    Shape theSShape = ShapeFactory.makeSinglePolygonShape(new Point3D(50,50,200), theImage.getWidth(),theImage.getHeight());
    theSShape.setTexture(new TextureImage(theImage));
    myWorld.addShape(theSShape);
    //theMouseTranslationManager.addTranslatable(theSShape);
    myRotationManager.addTranslatable(theSShape);
    
    
    theSShape = ShapeFactory.makeSinglePolygonShape(new Point3D(50,50,250), theImage.getWidth(),theImage.getHeight());
    theSShape.setTexture(new TextureImage(theImage));
    myWorld.addShape(theSShape);
    //theMouseTranslationManager.addTranslatable(theSShape);
    myRotationManager.addTranslatable(theSShape);
    
    theSShape = ShapeFactory.makeSinglePolygonShape(new Point3D(50,50,300), theImage.getWidth(),theImage.getHeight());
    theSShape.setTexture(new TextureImage(theImage));
    myWorld.addShape(theSShape);
    //theMouseTranslationManager.addTranslatable(theSShape);
    myRotationManager.addTranslatable(theSShape);
    */
    
    
    
    Shape theShape = ShapeFactory.makeCube(new Point3D(5,0,400), 94,94,94);
    theShape.setColor(new Color(0,0,255,100));
    //theShape.setTexture(new TextureImage(ImageFactory.createImage("AXA", new Font("Arial", Font.BOLD, 40), 100, 100, Color.BLUE, Color.WHITE, true)));
    theShape.setTexture("axa","axa", false, false);
//    theShape.setTexture("leslie", false, false);
//    theShape.myPolygons[0].setTexture("axa", false);
//    theShape.myPolygons[0].setTexture("guy", false, false);
//    theShape.myPolygons[1].setTexture("leslie", false, false);
    myWorld.addShape(theShape);
    theMouseTranslationManager.addTranslatable(theShape);
    myRotationManager.addTranslatable(theShape);
    
    theShape = ShapeFactory.makeCube(new Point3D(300,100,200), 94,94,94);
    theShape.setColor(new Color(0,0,255,100));
    //theShape.setTexture(new TextureImage(ImageFactory.createImage("AXA", new Font("Arial", Font.BOLD, 40), 100, 100, Color.BLUE, Color.WHITE, true)));
    theShape.setTexture("leslie","leslie", false, false);
//    theShape.setTexture("leslie", false, false);
//    theShape.myPolygons[0].setTexture("axa", false);
//    theShape.myPolygons[0].setTexture("guy", false, false);
//    theShape.myPolygons[1].setTexture("leslie", false, false);
    myWorld.addShape(theShape);
//    theMouseTranslationManager.addTranslatable(theShape);
    myRotationManager.addTranslatable(theShape);
    
//    Shape theWindow = ShapeFactory.makeSinglePolygonShape(new Point3D(0,0,300), 100, 100);
//    theWindow.setColor(new Color(255,100,100, 200));
//    myWorld.addShape(theWindow);
//    
    
    
    
    
    
//    MouseTranslationManager theMouseTranslationManager = new MouseTranslationManager(myPanel3D.getGraphics3D(), 100, 10);
//    theMouseTranslationManager.addTranslatable(theSphere);
//    theMouseTranslationManager.addTranslatable(theShape);
    
    myPanel3D.addMouseWheelListener(theMouseTranslationManager);
    myPanel3D.addMouseMotionListener(theMouseTranslationManager);
    
    
    
//    myRotationManager.addTranslatable(theShape);
    
//    myRotationManager.addTranslatable(theSphere);
    
    
//    theShape = ShapeFactory.makeCube(new Point3D(-100,50,1000), 94,94,94);
//    theShape.setColor(new Color(255,0,0));
//    theShape.setTexture("axa");
//    myWorld.addShape(theShape);
//    
//    AxisRotationManager theManager = new AxisRotationManager(new Line3D(new Point3D(0,0,0), new GVector(0,1,0)), Math.PI/180);
//    theManager.addTranslatable(theShape);
//    myWorld.getTranslateManagerContainer().addTranslateManager(theManager);
//    RotationManager theRManager = new RotationManager(new Rotation(Math.PI/90,0,0));
//    theRManager.addTranslatable(theShape);
//    myWorld.getTranslateManagerContainer().addTranslateManager(theRManager);
    

    
  }
  
  public void executeEvent(long anCounter) {
    if(anCounter >> 6 << 6 == anCounter){
      Rotation theRotation = myRotationManager.getRotation();
      Rotation theNewRotation = new Rotation(theRotation.getPitch(), theRotation.getYaw(), theRotation.getRoll());
      myRotationManager.setRotation(theNewRotation);
    }
  }
  
  public static void main(String args[]){
    new AXACube();
  }

  @Override
  protected int getNrOfObjectsInWorld() {
    return 2;
  }

  @Override
  protected Dimension getPanelSize() {
    return new Dimension( 400, 400 );
  }
  

}
