/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.worlds;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;

import chabernac.control.KeyCommandListener;
import chabernac.control.KeyMap;
import chabernac.control.KeyMapContainer;
import chabernac.control.SynchronizedEventManager;
import chabernac.space.Camera;
import chabernac.space.Command3dFactory;
import chabernac.space.Panel3D;
import chabernac.space.World;
import chabernac.space.shading.BumpShader;
import chabernac.space.shading.GouroudShading;
import chabernac.space.shading.TextureShader;
import chabernac.space.shading.iPixelShader;
import chabernac.space.shading.iVertexShader;

public abstract class AbstractWorld extends JFrame {
  private static final long serialVersionUID = -8099358160922769319L;
  
  protected World myWorld = null;
  protected Camera myCamera = null;
  protected Panel3D myPanel3D = null;
  protected KeyMapContainer myKeyMapContainer = null;
  protected SynchronizedEventManager myManager = null;


  public AbstractWorld(){
    init();
    buildGUI();
    buildWorld(myWorld);
    setupRendering();
    buildKeyMapContainer();
  }
  
  private final void init(){
    BasicConfigurator.configure();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(getPanelSize());
    setVisible(true);
    myWorld = new World();
    myCamera = new Camera();
  }
  
  private final void buildGUI(){
    myPanel3D = new Panel3D(myWorld, myCamera, new Dimension(getWidth(), getHeight()));
    myPanel3D.getGraphics3D().setDrawNormals(false);
    myPanel3D.getGraphics3D().setDrawRibs(false);
    myPanel3D.getGraphics3D().setDrawBackFacing(false);
    myPanel3D.getGraphics3D().setDrawPlanes(true);
    myPanel3D.getGraphics3D().setDrawLightSources( false );
    myPanel3D.getGraphics3D().setDrawTextureNormals( false );
    myPanel3D.getGraphics3D().setDrawVertexNormals( false);
    myPanel3D.getGraphics3D().setDrawTextureCoordinates(false);
    myPanel3D.getGraphics3D().setDrawCamZ(false);
    //myPanel3D.getGraphics3D().setBackGroundColor(new Color(100,100,200));
    myPanel3D.getGraphics3D().setBackGroundColor(new Color(0,0,0));
    myPanel3D.getGraphics3D().setShowDrawingAreas( false );
    myPanel3D.getGraphics3D().setUseClipping( true );
    myPanel3D.getGraphics3D().setVertexShaders( getVertexShaders() );
    myPanel3D.getGraphics3D().getGraphics3D2D().setPixelShaders( getPixelShaders() );
    
    //myPanel3D.setBorder(new TitledBorder("hallo"));
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(myPanel3D, BorderLayout.CENTER);
  }
  
  /**
   * override if you want to define other pixel shaders
   * @return
   */
  protected iPixelShader[] getPixelShaders(){
    return new iPixelShader[]{new TextureShader( ), new BumpShader( myWorld )};
  }
  
  
  /**
   * override if you want to change the fps rate
   * @return
   */
  protected int getFPS(){
    return 30;
  }
  /**
   * override if you want to define other pixel shaders
   * @return
   */
  protected iVertexShader[] getVertexShaders(){
    return new iVertexShader[]{new GouroudShading( (float)0.4 )};
  }
  
  private final void setupRendering(){
    myManager = new SynchronizedEventManager(getFPS());
    myManager.addSyncronizedEvent(myPanel3D);
    myManager.startManager();
  }
  
  private final void buildKeyMapContainer(){
    myKeyMapContainer = new KeyMapContainer();

    /*
    theContainer.addKeyMap(new KeyMap(KeyEvent.VK_D, Command3dFactory.strafeDown(mySynchronizedTimer, aCamera, 1000000),2));
    theContainer.addKeyMap(new KeyMap(KeyEvent.VK_E, Command3dFactory.strafeUp(mySynchronizedTimer, aCamera, 1000000),2)); 
    theContainer.addKeyMap(new KeyMap(KeyEvent.VK_S, Command3dFactory.strafeLeft(mySynchronizedTimer, aCamera, 1000000),2));
    theContainer.addKeyMap(new KeyMap(KeyEvent.VK_F, Command3dFactory.strafeRight(mySynchronizedTimer, aCamera, 1000000),2));
     */

    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_D, Command3dFactory.strafeDown(myManager, myCamera, 5),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_E, Command3dFactory.strafeUp(myManager, myCamera, 5),2)); 
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_S, Command3dFactory.strafeLeft(myManager, myCamera, 5),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_F, Command3dFactory.strafeRight(myManager, myCamera, 5),2));


    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_SPACE, Command3dFactory.forward(myManager, myCamera, 5),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_ALT, Command3dFactory.backward(myManager, myCamera, 5),2));

    myKeyMapContainer.addKeyMap(new KeyMap(new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_NUMPAD4}, Command3dFactory.left(myManager, myCamera, (float)Math.PI/144)));
    myKeyMapContainer.addKeyMap(new KeyMap(new int[]{KeyEvent.VK_RIGHT, KeyEvent.VK_NUMPAD6}, Command3dFactory.right(myManager, myCamera, (float)Math.PI/144)));
    myKeyMapContainer.addKeyMap(new KeyMap(new int[]{KeyEvent.VK_DOWN, KeyEvent.VK_NUMPAD2}, Command3dFactory.down(myManager, myCamera, (float)Math.PI/144)));
    myKeyMapContainer.addKeyMap(new KeyMap(new int[]{KeyEvent.VK_UP, KeyEvent.VK_NUMPAD8}, Command3dFactory.up(myManager, myCamera, (float)Math.PI/144)));

    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_NUMPAD7, Command3dFactory.rollLeft(myManager, myCamera, (float)Math.PI/144),2));
    myKeyMapContainer.addKeyMap(new KeyMap(KeyEvent.VK_NUMPAD9, Command3dFactory.rollRight(myManager, myCamera, (float)Math.PI/144),2));
    
    myPanel3D.addKeyListener(new KeyCommandListener(myKeyMapContainer));
    myPanel3D.setFocusable(true);
    myPanel3D.requestFocus();
  }
  
  protected abstract Dimension getPanelSize();
  protected abstract void buildWorld(World aWorld);
}
