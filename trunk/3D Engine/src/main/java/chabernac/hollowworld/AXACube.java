package chabernac.hollowworld;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import chabernac.control.KeyCommandListener;
import chabernac.control.KeyMap;
import chabernac.control.KeyMapContainer;
import chabernac.control.SynchronizedEventManager;
import chabernac.control.iSynchronizedEvent;
import chabernac.space.Camera;
import chabernac.space.Command3dFactory;
import chabernac.space.LightSource;
import chabernac.space.MouseTranslationManager;
import chabernac.space.Panel3D;
import chabernac.space.RotationManager;
import chabernac.space.Shape;
import chabernac.space.ShapeFactory;
import chabernac.space.World;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;

public class AXACube extends JFrame implements iSynchronizedEvent{
  private static final long serialVersionUID = 3362645587237603262L;
  private World myWorld = null;
  private Camera myCamera = null;
  private Panel3D myPanel3D = null;
  private RotationManager myRotationManager = null;
  private KeyMapContainer myKeyMapContainer = null;
  private SynchronizedEventManager myManager = null;
  
  public AXACube(){
    init();
    buildGUI();
    buildWorld();
    setupRendering();
    buildKeyMapContainer();
  }
  
  private void init(){
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(new Dimension(400,400));
    setVisible(true);
    myWorld = new World(1);
    myCamera = new Camera();
  }
  
  private void buildKeyMapContainer(){
    myKeyMapContainer = new KeyMapContainer();

    /*
    theContainer.addKeyMap(new KeyMap(KeyEvent.VK_D, Command3dFactory.strafeDown(mySynchronizedTimer, aCamera, 1000000),2));
    theContainer.addKeyMap(new KeyMap(KeyEvent.VK_E, Command3dFactory.strafeUp(mySynchronizedTimer, aCamera, 1000000),2)); 
    theContainer.addKeyMap(new KeyMap(KeyEvent.VK_S, Command3dFactory.strafeLeft(mySynchronizedTimer, aCamera, 1000000),2));
    theContainer.addKeyMap(new KeyMap(KeyEvent.VK_F, Command3dFactory.strafeRight(mySynchronizedTimer, aCamera, 1000000),2));
     */

    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_D, Command3dFactory.strafeDown(myManager, myCamera, 20),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_E, Command3dFactory.strafeUp(myManager, myCamera, 20),2)); 
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_S, Command3dFactory.strafeLeft(myManager, myCamera, 20),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_F, Command3dFactory.strafeRight(myManager, myCamera, 20),2));


    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_SPACE, Command3dFactory.forward(myManager, myCamera, 20),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_ALT, Command3dFactory.backward(myManager, myCamera, 20),2));

    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_LEFT, Command3dFactory.left(myManager, myCamera, (float)Math.PI/144),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_RIGHT, Command3dFactory.right(myManager, myCamera, (float)Math.PI/144),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_DOWN, Command3dFactory.down(myManager, myCamera, (float)Math.PI/144),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_UP, Command3dFactory.up(myManager, myCamera, (float)Math.PI/144),2));

    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_NUMPAD7, Command3dFactory.rollLeft(myManager, myCamera, (float)Math.PI/144),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_NUMPAD9, Command3dFactory.rollRight(myManager, myCamera, (float)Math.PI/144),2));
    
    myPanel3D.addKeyListener(new KeyCommandListener(myKeyMapContainer));
    myPanel3D.setFocusable(true);
    myPanel3D.requestFocus();
  }
  
  private void buildGUI(){
    myPanel3D = new Panel3D(myWorld, myCamera, new Dimension(getWidth(), getHeight()));
    myPanel3D.getGraphics3D().setDrawNormals(false);
    myPanel3D.getGraphics3D().setDrawRibs(false);
    myPanel3D.getGraphics3D().setDrawBackFacing(false);
    myPanel3D.getGraphics3D().setDrawPlanes(true);
    myPanel3D.getGraphics3D().setDrawTextureNormals(false);
    myPanel3D.getGraphics3D().setDrawVertexNormals(false);
    myPanel3D.getGraphics3D().setDrawTextureCoordinates(false);
    myPanel3D.getGraphics3D().setDrawCamZ(false);
    //myPanel3D.getGraphics3D().setBackGroundColor(new Color(100,100,200));
    myPanel3D.getGraphics3D().setBackGroundColor(new Color(0,0,0));
    //myPanel3D.setBorder(new TitledBorder("hallo"));
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(myPanel3D, BorderLayout.CENTER);
    
  }
  
  private void buildWorld(){
    myWorld.addLightSource(new LightSource(new Point3D(0,2000,-5000), 5000));
    
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
  
  private void setupRendering(){
    myManager = new SynchronizedEventManager(50);
    myManager.addSyncronizedEvent(myPanel3D);
    myManager.addSyncronizedEvent(this);
    myManager.startManager();
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
  

}
