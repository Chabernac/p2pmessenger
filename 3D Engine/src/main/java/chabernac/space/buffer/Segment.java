package chabernac.space.buffer;

import org.apache.log4j.Logger;

import chabernac.space.geom.Vertex2D;
import chabernac.space.texture.Texture2;
/**
 * currently calculating real u and v values for every 'step' pixels does result in a performance gain
 * we'll probably need to optimize other bottlenecks first 
 */
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
  
  private float light;

  private float perspectiveCorrectedU;
  private float perspectiveCorrectedV;

  private byte affineStep = 1;
  private byte counter = 0;


  private float myPerspectiveCorrectedUInStepPixels;
  private float myPerspectiveCorrectedVInStepPixels;
  private float myInverseZInStepPixels;

  private float myUInStepPixels;
  private float myVInStepPixels;

  //delte affine u
  private float dau;
  //delta affine v
  private float dav;
  //delta affin z;
  private float daz;


  public Segment(){
  }

  private void repositionStartEnd(){
    counter = 0;

    dx = end.getPoint().x - start.getPoint().x; 

    dz = (end.getInverseDepth() - start.getInverseDepth()) / dx;
    du = (end.getPerspectiveCorrectTexturePoint().x - start.getPerspectiveCorrectTexturePoint().x) / dx;
    dv = (end.getPerspectiveCorrectTexturePoint().y - start.getPerspectiveCorrectTexturePoint().y) / dx;
    dl = (end.getLightning() - start.getLightning()) / dx; 

    myPixel.x = (int)start.getPoint().x;
    myInverseZInStepPixels = start.getInverseDepth();
    light = start.getLightning();

    myPerspectiveCorrectedUInStepPixels = start.getPerspectiveCorrectTexturePoint().x;
    myPerspectiveCorrectedVInStepPixels = start.getPerspectiveCorrectTexturePoint().y;

    myUInStepPixels = myPerspectiveCorrectedUInStepPixels / myInverseZInStepPixels;
    myVInStepPixels = myPerspectiveCorrectedVInStepPixels / myInverseZInStepPixels;

    if(affineStep == 1){
      myPixel.invZ = start.getInverseDepth();
      perspectiveCorrectedU = start.getPerspectiveCorrectTexturePoint().x;
      perspectiveCorrectedV = start.getPerspectiveCorrectTexturePoint().y;
    }
  }

  public boolean hasNext(){
    return myPixel.x < end.getPoint().x;
  }

  public void next(){
    if(affineStep == 1){
      //just the basic routine if we calculate the real u for each pixel
      myPixel.u = perspectiveCorrectedU / myPixel.invZ;
      myPixel.v = perspectiveCorrectedV / myPixel.invZ;

      perspectiveCorrectedU += du;
      perspectiveCorrectedV += dv;
      myPixel.invZ += dz;
    } else {
      if(counter == 0){
        myPixel.u = myUInStepPixels;
        myPixel.v = myVInStepPixels;
        myPixel.invZ = myInverseZInStepPixels;
        calculateAffineStep();
      } else {
        myPixel.u += dau;
        myPixel.v += dav;
        myPixel.invZ += daz;
      }
    }


    myPixel.x++;
    myPixel.index++;
    light += dl;
    
    myPixel.light = light;

    myPixel.normal = null;
    myPixel.camPoint = null;

    if(++counter >= affineStep){
      //reset counter
      counter = 0;
    }
  }

  private void calculateAffineStep(){
    perspectiveCorrectedU = myPerspectiveCorrectedUInStepPixels;
    perspectiveCorrectedV = myPerspectiveCorrectedVInStepPixels;

    myPerspectiveCorrectedUInStepPixels = perspectiveCorrectedU + affineStep * du;
    myPerspectiveCorrectedVInStepPixels = perspectiveCorrectedV + affineStep * dv;
    myInverseZInStepPixels = myPixel.invZ + affineStep * dz;

    myUInStepPixels = myPerspectiveCorrectedUInStepPixels / myInverseZInStepPixels;
    myVInStepPixels = myPerspectiveCorrectedVInStepPixels / myInverseZInStepPixels;

    dau = (myUInStepPixels - myPixel.u) / affineStep;
    dav = (myVInStepPixels - myPixel.v) / affineStep;
    daz = (myInverseZInStepPixels - myPixel.invZ) / affineStep;
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
