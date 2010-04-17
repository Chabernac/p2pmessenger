/*
 * Created on 13-jun-2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.hollowworld;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

import chabernac.GUI.components.DefaultExitFrame;
import chabernac.GUI.utils.GUIUtils;
import chabernac.control.KeyCommand;
import chabernac.control.KeyCommandListener;
import chabernac.control.KeyConfigurationDialog;
import chabernac.control.KeyMap;
import chabernac.control.KeyMapContainer;
import chabernac.control.MouseCommandListener;
import chabernac.control.SynchronizedEventManager;
import chabernac.math.MatrixException;
import chabernac.space.AxisRotationManager;
import chabernac.space.Camera;
import chabernac.space.CameraMouseCommand;
import chabernac.space.Command3dFactory;
import chabernac.space.Frame3D;
import chabernac.space.LightSource;
import chabernac.space.PolygonException;
import chabernac.space.Shape;
import chabernac.space.ShapeFactory;
import chabernac.space.World;
import chabernac.space.buffer.iBufferStrategy;
import chabernac.space.geom.GVector;
import chabernac.space.geom.Line3D;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;
import chabernac.space.shapes.Cube;
import chabernac.space.shapes.TexturePolygonShape;
import chabernac.utils.Debug;
import chabernac.utils.TimeTracker;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class HollowWorld extends DefaultExitFrame{
  private JButton myKeysButton = null;
  private JButton myStartButton = null;
  private Camera myCamera = null;
  private KeyMapContainer myKeyMapContainer = null;
  private ExitKeyCommand myExitKeyCommand = null;
  private SynchronizedEventManager myManager = null;

  public HollowWorld(){
    try{
      Debug.setDebug(true);
      myManager = new SynchronizedEventManager(50);
      myCamera = new Camera(new Point3D(0,0,-1000),new Rotation(0,0,0),1F);
      buildKeyMapContainer();
      initialize();
      buildGUI();
    }catch(MatrixException e){
      Debug.log(this, "Could not start hollowworld", e);
    }
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

    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_NUMPAD4, Command3dFactory.left(myManager, myCamera, (float)Math.PI/144),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_NUMPAD6, Command3dFactory.right(myManager, myCamera, (float)Math.PI/144),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_NUMPAD8, Command3dFactory.down(myManager, myCamera, (float)Math.PI/144),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_NUMPAD2, Command3dFactory.up(myManager, myCamera, (float)Math.PI/144),2));

    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_NUMPAD7, Command3dFactory.rollLeft(myManager, myCamera, (float)Math.PI/144),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_NUMPAD9, Command3dFactory.rollRight(myManager, myCamera, (float)Math.PI/144),2));

    myExitKeyCommand = new ExitKeyCommand(this);
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_Q, myExitKeyCommand,2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_P, new PauseKeyCommand(myManager), 2));

  }

  private void initialize(){
    myStartButton = new JButton("Start engine");
    myKeysButton = new JButton("Configure keys");

    ButtonListener theButtonListener = new ButtonListener(); 
    myStartButton.addActionListener(theButtonListener);
    myKeysButton.addActionListener(theButtonListener);
  }

  private void buildGUI(){
    GridBagLayout theLayout = new GridBagLayout();
    getContentPane().setLayout( theLayout );

    GUIUtils.addMyComponent(getContentPane(), theLayout, GridBagConstraints.WEST, new Insets(0,0,0,0), GridBagConstraints.BOTH, 1, 1, 0, 0, 1, 1, myKeysButton, Color.white);
    GUIUtils.addMyComponent(getContentPane(), theLayout, GridBagConstraints.WEST, new Insets(0,0,0,0), GridBagConstraints.BOTH, 1, 1, 0, 1, 1, 1, myStartButton, Color.white);

    setSize(new Dimension(150,150));
    setVisible(true);
  }

  private void startEngineFullScreen(){
    setVisible(false);
    //Frame3D theFrame = new Frame3D(buildWorld(), myCamera, new Dimension(200,200));
    Frame3D theFrame = new Frame3D(buildSinglePolygonWorld(), myCamera, new Dimension(800,600));
    theFrame.setLocation(new Point(200,200));
    theFrame.setVisible(true);

    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_L, new DebugKeyCommand(theFrame.getGraphics3D().getBufferStrategy()), 2));
    myManager.addSyncronizedEvent(theFrame);
    myExitKeyCommand.setFullScreenComponent(theFrame);
    theFrame.addKeyListener(new KeyCommandListener(myKeyMapContainer));
    try {
      new MouseCommandListener(theFrame, new CameraMouseCommand(myCamera));
    } catch (AWTException e) {
      Debug.log(this,"could not attach mouse listener", e);
    }
    myManager.startManager();
  }


  private World buildSinglePolygonWorld(){
    World theWorld = new World(5, 0);

    /*
    SinglePolygonShape thePolygonShape = new SinglePolygonShape(new Point3D(500,0,500), 300, 300);
    thePolygonShape.setColor(new Color(100,100,200));
    thePolygonShape.setTexture("leslie");
    theWorld.addShape(thePolygonShape);
    
    thePolygonShape = new SinglePolygonShape(new Point3D(500,500,500), 300, 300);
    thePolygonShape.setColor(new Color(100,100,200));
    thePolygonShape.setTexture("leslie");
    theWorld.addShape(thePolygonShape);
    */
    
    
    /*
    Cube theCube = new Cube(new Point3D(700,100,200), 200,100,1000);
    theCube.setTexture("marb002");
    theCube.setColor(Color.blue);
    theWorld.addShape(theCube);
    
    */
    
    Cube theCube = new Cube(new Point3D(500,0,500), 1000,700,300);
    theCube.setTexture("bricks");
    theCube.setColor(Color.blue);
    theWorld.addShape(theCube);
    
    
    TexturePolygonShape theTexturePolygonShape = new TexturePolygonShape("lhermitte2", true, new Point3D(700,700,200));
    theWorld.addShape(theTexturePolygonShape);
    
    theTexturePolygonShape = new TexturePolygonShape("leslie2", true,  new Point3D(700,700,100));
    theWorld.addShape(theTexturePolygonShape);
    
    /*
     theCube = new Cube(new Point3D(0,0,500), 500,500,500);
    theCube.setDoubleSidedPolygons(true);
    theCube.setTexture("lhermitte2");
    theCube.setColor(Color.blue);
    theWorld.addShape(theCube);
    */
    
    
    
//    Cube theCube = new Cube(new Point3D(0,0,500), 800,500,50);
//    theCube.setTexture("S_S_Mstair");
//    theCube.setColor(Color.blue);
//    theWorld.addShape(theCube);
    
//    theCube = new Cube(new Point3D(800,0,500), 50,500,800);
//    theCube.setTexture("bricks");
//    theCube.setColor(Color.blue);
//    theWorld.addShape(theCube);
    
    
    theCube = new Cube(new Point3D(500,500,500), 300,300,300);
    theCube.setTexture("cloth2");
    theCube.setColor(Color.blue);
    theWorld.addShape(theCube);
    
    /*
    PathTranslateManager theTranslateManager = new PathTranslateManager(new Point3D[]{new Point3D(400,400,400), new Point3D(200,200,200), new Point3D(200,200,400)}, 20);
    theTranslateManager.addTranslatable(theCube);
    theWorld.getTranslateManagerContainer().addTranslateManager(theTranslateManager);
    
    RotationManager theRotationManager = new RotationManager(new Rotation(0,0,Math.PI/60));
    theRotationManager.addTranslatable(theCube);
    theWorld.getTranslateManagerContainer().addTranslateManager(theRotationManager);
    */
    AxisRotationManager theAxisRotationManager = new AxisRotationManager(new Line3D(new Point3D(500,500,500), new GVector(1,0,0)), Math.PI / 60);
    theAxisRotationManager.addTranslatable(theCube);
    theWorld.getTranslateManagerContainer().addTranslateManager(theAxisRotationManager);
    
    
    
    theWorld.done();
    
    theWorld.addLightSource(new LightSource(new Point3D(0,0, 0), 5000));
    theWorld.addLightSource(new LightSource(new Point3D(800,0, 0), 1000));

    return theWorld;

  }

  private World buildWorld() {
    World theWorld = new World(5, 0);

    try{

      Cube theCube1 = new Cube(new Point3D(0,0,1000),500,500,500);
      theCube1.setColor(new Color(0,200,0));
      theWorld.addShape(theCube1);

//    SinglePolygonShape thePolygonShape = new SinglePolygonShape(new Point3D(0,0,500), 90, 98);
//    thePolygonShape.setColor(new Color(100,100,200));
//    theWorld.addShape(thePolygonShape);

      /*
			Cube theCube2 = new Cube(new Point3D(200,-200,1200),300,1200,300);
			theCube2.setColor(new Color(200,0,0));
			theWorld.addShape(theCube2);
       */

//    Cube theCube3 = new Cube(new Point3D(600,-200,1200),300,1200,300);
//    theCube3.setColor(new Color(200,0,0));
//    theWorld.addShape(theCube3);



      /*
			theWorld.addShape(new Cube(new Point3D(100,100,800),500,100,200));
			theWorld.addShape(new Cube(new Point3D(100,300,1500),100,300,400));
			theWorld.addShape(new Cube(new Point3D(0,0,200),300,300,300));
			theWorld.addShape(new Cube(new Point3D(0,1000,3000),1000,2000,4000));
       */


      /*
			Cylinder theCylinder1 = new Cylinder(new Point3D(1000,2000,1000),new GVector(1000,0,0), 300);
			theCylinder1.setColor(new Color(100,120,30));
			theWorld.addShape( theCylinder1 );

			Cylinder theCylinder3 = new Cylinder(new Point3D(1500,2000,500),new GVector(0,0,1000), 295);
			theCylinder3.setColor(new Color(200,120,30));
			theWorld.addShape( theCylinder3 );


			Cylinder theCylinder2 = new Cylinder(new Point3D(0,0,0),new GVector(500,0,1000), 200);
			Camera theCamera = new Camera(new Point3D(0,0,0), new Rotation(0,0, Math.PI / 4), 1);
			theCylinder2.translate(theCamera);
			theCylinder2.setColor(new Color(250,0,50));
			theWorld.addShape( theCylinder2 );
       */


      //theWorld.addLightSource(new LightSource(new Point3D(0,-10000,0), 10000));
      //theWorld.addLightSource(new LightSource(new Point3D(0,10000, 0), 10000));

      //theWorld.addLightSource(new LightSource(new Point3D(-10000,0, 0), 10000));
      theWorld.addLightSource(new LightSource(new Point3D(-1000,0, 0), 5000));
      theWorld.addLightSource(new LightSource(new Point3D(0,0, 0), 1000));



      //theWorld.addLightSource(new LightSource(new Point3D(-2000,10000, -3000), 10000));
      //theWorld.addLightSource(new LightSource(new Point3D(2000,4000,3000), 2000));
      //makeRotatingShape(theWorld);
      theWorld.done();

      /*
			TranslateManagerContainer theContainer = theWorld.getTranslateManagerContainer();
			TranslateManager theRotationManger = new RotationManager(new Rotation(0,0,Math.PI / 36));
			theRotationManger.addTranslatable(theCube1);
			theRotationManger.addTranslatable(theCube2);
			theContainer.addTranslateManager(theRotationManger);
       */

      /*
			CylinderRotationManager theCylinderRotationManager = new CylinderRotationManager(Math.PI/180);
			theCylinderRotationManager.addTranslatable(theCylinder2);
			theCylinderRotationManager.addTranslatable(theCylinder3);
			theContainer.addTranslateManager(theCylinderRotationManager);
       */


    }catch(PolygonException e){
      Debug.log(this,"Could not create world", e);
    }
    catch (MatrixException e) {
      Debug.log(this,"Could not create world", e);
    }

    return theWorld;
  }

  private void makeRotatingShape(World aWorld) throws PolygonException, MatrixException{
    Shape theShape = ShapeFactory.makeCube(new Point3D(200,400,500),200,400,300);
    theShape.done();

    Camera theCameras[] = new Camera[3];
    theCameras[0] = new Camera(theShape.myCenterPoint, new Rotation(0F, 0F, 0F), 1F);
    theCameras[1] = new Camera(new Point3D(0,0,0), new Rotation((float)Math.PI/720,(float)Math.PI/720,(float)Math.PI/720), 1F);
    Point3D theInvPoint = (Point3D)theShape.myCenterPoint.clone();
    //theInvPoint.invert();
    //theInvPoint.x -= 1;
    theCameras[2] = new Camera(theInvPoint, new Rotation(0F, 0F, 0F), 1F);
    //theShape.myTranslationCameras = theCameras;

    aWorld.addShape(theShape);
  }

  private class ButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent evt){
      if(evt.getSource() == myStartButton){
        startEngineFullScreen();
      } else if (evt.getSource() == myKeysButton){
        KeyConfigurationDialog thePanel = new KeyConfigurationDialog(HollowWorld.this, "Key configuration", true, myKeyMapContainer); 
        thePanel.setVisible(true);
      }
    }
  }

  public static void main(String args[]){
    TimeTracker.setEnabled(false);
    new HollowWorld();
  }

  private class ExitKeyCommand extends KeyCommand{
    private Frame3D myFullScreenComponent;
    private Component myReturnComponent; 

    public ExitKeyCommand(Component aReturnComponent){
      this(null, aReturnComponent);
    }

    public ExitKeyCommand(Frame3D aFullScreenComponent, Component aReturnComponent){
      super("Exit full screen");
      myFullScreenComponent = aFullScreenComponent;
      myReturnComponent = aReturnComponent;
    }

    public void setFullScreenComponent(Frame3D aFrame3D){
      myFullScreenComponent = aFrame3D;
    }

    public void keyPressed() {
      Debug.log(this,"leaving full screen");
      //myFullScreenComponent.stopRendering();
      myFullScreenComponent.dispose();
      myFullScreenComponent = null;
      myManager.stopManager();
      myReturnComponent.setVisible(true);
    }
    public void keyDown() {}
    public void keyReleased() {}

  }

  private class PauseKeyCommand extends KeyCommand{
    SynchronizedEventManager myManager = null;


    public PauseKeyCommand(SynchronizedEventManager aManager){
      super("Pause");
      myManager = aManager;
    }

    public void keyPressed() {
      if(myManager.isRunning()) myManager.stopManager();
      else myManager.startManager();
    }

    public void keyDown() {}
    public void keyReleased() {}

  }

  private class DebugKeyCommand extends KeyCommand{
    private iBufferStrategy myBufferStrategy = null;

    public DebugKeyCommand(iBufferStrategy aBuffer){
      super("Debug");
      myBufferStrategy = aBuffer;
    }

    public void keyPressed() {
      /*
      if(myBufferStrategy.getDebugMode() == 0) myBufferStrategy.setDebugMode(SBuffer.DRAW_POLYGONS);
      else if (myBufferStrategy.getDebugMode() == SBuffer.DRAW_POLYGONS) myBufferStrategy.setDebugMode(SBuffer.DRAW_POLYGONS_SLOW);
      //else if (myBufferStrategy.getDebugMode() == SBuffer.DRAW_POLYGONS_1_BY_1) myBufferStrategy.setDebugMode(SBuffer.DRAW_POLYGONS_SLOW);
      else if (myBufferStrategy.getDebugMode() == SBuffer.DRAW_POLYGONS_SLOW) myBufferStrategy.setDebugMode(0);
      */
    }

    public void keyDown() {}
    public void keyReleased() {}

  }
}



