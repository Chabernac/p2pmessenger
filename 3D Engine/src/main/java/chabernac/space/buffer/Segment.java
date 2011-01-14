package chabernac.space.buffer;

import org.apache.log4j.Logger;

import chabernac.space.geom.Point2D;
import chabernac.space.geom.Vector2D;
import chabernac.space.geom.Vertex2D;
import chabernac.space.texture.Texture2;

public class Segment {
  private static final int POOL_SIZE = 10;
  private static final Segment[] STACK = new Segment[POOL_SIZE];
  private static int countFree;

  private static Logger LOGGER = Logger.getLogger(Segment.class);

  private Texture2 texture = null;
  private Vertex2D start = null;
  private Vertex2D end = null;
  private Pixel myPixel = new Pixel( );

  private float dz;
  private float du;
  private float dv;
  private float dx;
  private float dl;
  
  private float perspectiveCorrectedU;
  private float perspectiveCorrectedV;
  
  public Segment(){
  }

  private void repositionStartEnd(){
    dx = end.getPoint().x - start.getPoint().x; 
    
    dz = (end.getInverseDepth() - start.getInverseDepth()) / dx;
    du = (end.getPerspectiveCorrectTexturePoint().x - start.getPerspectiveCorrectTexturePoint().x) / dx;
    dv = (end.getPerspectiveCorrectTexturePoint().y - start.getPerspectiveCorrectTexturePoint().y) / dx;
    dl = (end.getLightning() - start.getLightning()) / dx; 
    
    myPixel.x = (int)start.getPoint().x;
    myPixel.invZ = start.getInverseDepth();
    myPixel.light = start.getLightning();
    
    perspectiveCorrectedU = start.getPerspectiveCorrectTexturePoint().x;
    perspectiveCorrectedV = start.getPerspectiveCorrectTexturePoint().y;
  }

  public boolean hasNext(){
    return myPixel.x < end.getPoint().x;
  }

  public void next(){
    perspectiveCorrectedU += du;
    perspectiveCorrectedV += dv;
    myPixel.invZ += dz;
    
    //TODO we accually do not need this try to remove it
    myPixel.z = 1 / myPixel.invZ;
    
    //we do not use the x...
    myPixel.x++;
    myPixel.index++;
    myPixel.light += dl;
    
    myPixel.u = perspectiveCorrectedU / myPixel.invZ;
    myPixel.v = perspectiveCorrectedV / myPixel.invZ;
  }
  

  public Texture2 getTexture() {
    return texture;
  }
  public void setTexture(Texture2 texture) {
    this.texture = texture;
  }

  public static Segment getInstance(Vertex2D aStartVertex, Vertex2D anEndVertex, int aPixelIndex, Texture2 aTexture, int anXStep){
    Segment result;
    if (countFree == 0) {
      result = new Segment();
    } else {
      result = STACK[--countFree];
    }
    result.start = aStartVertex;
    result.end = anEndVertex;
    result.texture = aTexture;
    result.myPixel.texture = aTexture;
    result.myPixel.index = aPixelIndex;
    
    result.repositionStartEnd();

    return result;
  }

  public static void freeInstance(Segment aSegment) {
    if (countFree < POOL_SIZE) {
      STACK[countFree++] = aSegment;
    }
  }
  public Pixel getPixel(){
    return myPixel;
  }

}
