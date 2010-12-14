package chabernac.worlds;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

import chabernac.control.iSynchronizedEvent;
import chabernac.space.LightSource;
import chabernac.space.MouseTranslationManager;
import chabernac.space.RotationManager;
import chabernac.space.ShapeFactory;
import chabernac.space.World;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;
import chabernac.space.geom.Shape;
import chabernac.space.shading.AmbientShading;
import chabernac.space.shading.BumpShader;
import chabernac.space.shading.GouroudShading;
import chabernac.space.shading.PhongShader;
import chabernac.space.shading.TextureShader;
import chabernac.space.shading.iPixelShader;
import chabernac.space.shading.iVertexShader;

public class AXACube extends AbstractWorld implements iSynchronizedEvent{
  private static final long serialVersionUID = 3362645587237603262L;
   
  private RotationManager myRotationManager = null;
  
  protected void buildWorld(World aWorld){
//    myManager.addSyncronizedEvent(this);
    
    aWorld.addLightSource(new LightSource(new Point3D(0,0,-200), 1000));
//    aWorld.addLightSource(new LightSource(new Point3D(200,0,0), 1500));
    
    MouseTranslationManager theMouseTranslationManager = new MouseTranslationManager(myPanel3D.getGraphics3D(), 100, 10);
    myRotationManager = new RotationManager(new Rotation((float)Math.PI / 500,(float)Math.PI / 400,(float)Math.PI / 360));
    RotationManager theRotationManager = new RotationManager(new Rotation(0,0,(float)Math.PI / 180));
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
//    theSphere.setTexture("MoonMap_2500x1250", false, true);r
//    theSphere.setTexture("MoonMap2_2500x1250", false, true);
    
//    theSphere.setTexture("VenusMap_2500x1250", false, true);
    
    //theSphere.setTexture(new TextureImage(ImageFactory.createImage("Guy", new Font("Arial", Font.BOLD, 20), 100, 100, Color.BLUE, Color.WHITE, true)));
//    theSphere.done();
    //theSphere.myPolygons[20].setTexture("guy", false);
//    myWorld.addShape(theSphere);
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
    theShape.setTexture("axa","guy", false, false);
//    theShape.setTexture("leslie", false, false);
//    theShape.myPolygons[0].setTexture("axa", false);
//    theShape.myPolygons[0].setTexture("guy", false, false);
//    theShape.myPolygons[1].setTexture("leslie", false, false);
    myWorld.addShape(theShape);
    theMouseTranslationManager.addTranslatable(theShape);
    myRotationManager.addTranslatable(theShape);
    
    theShape = ShapeFactory.makeCube(new Point3D(300,100,200), 3400,3400,3400);
//    theShape.setColor(new Color(0,0,255,100));
    //theShape.setTexture(new TextureImage(ImageFactory.createImage("AXA", new Font("Arial", Font.BOLD, 40), 100, 100, Color.BLUE, Color.WHITE, true)));
    theShape.setTexture("metal006","metal006", false, false);
//    theShape.setTexture("leslie", false, false);
//    theShape.myPolygons[0].setTexture("axa", false);
//    theShape.myPolygons[0].setTexture("guy", false, false);
//    theShape.myPolygons[1].setTexture("leslie", false, false);
    myWorld.addShape(theShape);
//    theMouseTranslationManager.addTranslatable(theShape);
//    myRotationManager.addTranslatable(theShape);
    
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
    

    myPanel3D.getGraphics3D().setDrawNormals(false);
    myPanel3D.getGraphics3D().setDrawRibs(false);
    myPanel3D.getGraphics3D().setDrawBackFacing(false);
    myPanel3D.getGraphics3D().setDrawPlanes(true);
    myPanel3D.getGraphics3D().setDrawLightSources( false );
    myPanel3D.getGraphics3D().setDrawTextureNormals( false );
    myPanel3D.getGraphics3D().setDrawBumpVectors(false);
    myPanel3D.getGraphics3D().setDrawVertexNormals( false);
    myPanel3D.getGraphics3D().setDrawTextureCoordinates(false);
    myPanel3D.getGraphics3D().setDrawCamZ(false);
    //myPanel3D.getGraphics3D().setBackGroundColor(new Color(100,100,200));
    myPanel3D.getGraphics3D().setBackGroundColor(new Color(0,0,0));
    myPanel3D.getGraphics3D().setShowDrawingAreas( false );
    myPanel3D.getGraphics3D().setUseClipping( true);
    myPanel3D.getGraphics3D().getGraphics3D2D().setUsePartialClearing( true);
    myPanel3D.getGraphics3D().setVertexShaders( getVertexShaders() );
    
  }
  
  public void executeEvent(long anCounter) {
    if(anCounter >> 6 << 6 == anCounter){
      Rotation theRotation = myRotationManager.getRotation();
      Rotation theNewRotation = new Rotation(theRotation.getPitch(), theRotation.getYaw(), theRotation.getRoll());
      myRotationManager.setRotation(theNewRotation);
    }
  }
  
  public static void main(String args[]){
    AXACube theCube = new AXACube();
//    theCube.setVisible( false );
//    theCube.setState( JFrame.MAXIMIZED_BOTH );
//    theCube.setState( JFrame.ICONIFIED );
//    theCube.setVisible( false );
  }

  @Override
  protected Dimension getPanelSize() {
    return new Dimension( 800, 600);
  }
  
  /**
   * override if you want to define other pixel shaders
   * @return
   */
  protected iPixelShader[] getPixelShaders(){
    return new iPixelShader[]{
                              new TextureShader( ), 
//                              new BumpShader( myWorld ),
//                              new PhongShader( myWorld  )
                              };
  }
  
  /**
   * override if you want to define other pixel shaders
   * @return
   */
  protected iVertexShader[] getVertexShaders(){
    return new iVertexShader[]{
//                               new AmbientShading( 0F )
                               new GouroudShading( 0.5F )
                               };
  }
  
  protected int getFPS(){
    return 100;
  }
  

}
