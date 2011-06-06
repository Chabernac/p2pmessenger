package chabernac.worlds;

import java.awt.Color;
import java.awt.Dimension;

import chabernac.control.iSynchronizedEvent;
import chabernac.space.LightSource;
import chabernac.space.RotationManager;
import chabernac.space.ShapeFactory;
import chabernac.space.World;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Shape;
import chabernac.space.shading.GouroudShading;
import chabernac.space.shading.TextureShader;
import chabernac.space.shading.iPixelShader;
import chabernac.space.shading.iVertexShader;

public class SinglePlanePerformanceWorld extends AbstractWorld implements iSynchronizedEvent{
  private static final long serialVersionUID = 3362645587237603262L;
   
  private RotationManager myRotationManager = null;
  
  public SinglePlanePerformanceWorld(){
    super(new Dimension( 800, 600));
  }
  
  protected void buildWorld(World aWorld){
    
    aWorld.addLightSource(new LightSource(new Point3D(0,0,-200), 1000));
    
    Shape theShape = ShapeFactory.makeCube(new Point3D(-800,-800,500), 3400,3400,3400);
    theShape.setTexture("metal006","metal006", false, false);
    myWorld.addShape(theShape);
    myWorld.addShape(theShape);

    myPanel3D.getGraphics3D().setDrawNormals(false);
    myPanel3D.getGraphics3D().setDrawRibs(false);
    myPanel3D.getGraphics3D().setDrawBackFacing(false);
    myPanel3D.getGraphics3D().setDrawPlanes(true);
    myPanel3D.getGraphics3D().setDrawLightSources( false );
    myPanel3D.getGraphics3D().setDrawTextureNormals( false );
    myPanel3D.getGraphics3D().setDrawBumpVectors(false);
    myPanel3D.getGraphics3D().setDrawVertexNormals( false);
    myPanel3D.getGraphics3D().setDrawTextureCoordinates(false );
    myPanel3D.getGraphics3D().setDrawCamZ(false);
    //myPanel3D.getGraphics3D().setBackGroundColor(new Color(100,100,200));
    myPanel3D.getGraphics3D().setBackGroundColor(new Color(0,0,0));
    myPanel3D.getGraphics3D().setShowDrawingAreas( false );
    myPanel3D.getGraphics3D().setUseClipping( false );
    myPanel3D.getGraphics3D().getGraphics3D2D().setUsePartialClearing( false );
    myPanel3D.getGraphics3D().setVertexShaders( getVertexShaders() );
    myPanel3D.getGraphics3D().setDrawWorldOrigin(false);
    
  }
  
  public boolean isRecordable(){
    return false;
  }
  
  public static void main(String args[]){
    SinglePlanePerformanceWorld theCube = new SinglePlanePerformanceWorld();
  }
  
  /**
   * @return
   */
  protected iPixelShader[] getPixelShaders(){
    return new iPixelShader[]{
                              new TextureShader( ), 
//                              new BumpShader( myWorld ),
//                              new PhongShader( myWorld  )
//                              new DepthShader(5000)
                              };
  }
  
  /**
   * override if you want to define other pixel shaders
   * @return
   */
  protected iVertexShader[] getVertexShaders(){
    return new iVertexShader[]{
//                               new AmbientShading( 0.2F )
                               new GouroudShading( 0.8F )
                               };
  }
  
  protected int getFPS(){
    return 1000;
  }

  @Override
  public boolean executeEvent( long aCounter ) {
    return true;
  }
  

}
