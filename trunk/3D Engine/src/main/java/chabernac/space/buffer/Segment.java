package chabernac.space.buffer;

import org.apache.log4j.Logger;

import chabernac.space.World;
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
  private int xStart, xEnd;
  private float invzStart, invzEnd, lStart, lEnd, l = 0;
  private float zdiff, xDiff, invzRico, udiff, vdiff, urico, vrico, lRico = 0;
//  private int c = 0;
  private Vertex2D start = null;
  private Vertex2D end = null;
  private boolean isTexture = false;
  private boolean isAffine = false;
  private static final float AFFINEBORDER = (float)Math.tan(Math.PI / 10);
  private static boolean FORCEAFFINE = true;
  private static boolean FORCENOTAFFINE = false;
  private Pixel myPixel = new Pixel( );


  public Segment(){

  }

  public Segment(Vertex2D aStartVertex, Vertex2D anEndVertex, Texture2 aTexture){
    //this(aStartVertex, anEndVertex, Color.black.getRGB(), aTexture);
    start = aStartVertex;
    end = anEndVertex;
    texture = aTexture;
    isTexture = aTexture != null;
    myPixel = new Pixel( aTexture );
    myPixel.texture = aTexture;
    calculateRicos();
    repositionStartEnd();
  }

  private void calculateRicos() {
    Point2D theStartPoint = start.getPoint();
    Point2D theEndPoint = end.getPoint();

    xDiff = theEndPoint.x - theStartPoint.x;
    float yDiff = theEndPoint.y - theStartPoint.y;
    zdiff = end.getDepth() - start.getDepth();

    //TODO very inperformant
    float length = (float)Math.sqrt(xDiff * xDiff + yDiff * yDiff); 

    float tanangle = Math.abs(zdiff) / length;

    invzRico = (end.getInverseDepth() - start.getInverseDepth()) / xDiff;
    lRico = (end.getLightning() - start.getLightning()) / xDiff;

    if(isTexture){
      Point2D theTextureStartPoint = start.getTexturePoint();
      Point2D theTextureEndPoint = end.getTexturePoint();

      Vector2D theDistance = Texture2.distance(texture, theTextureStartPoint, theTextureEndPoint);
      udiff = theDistance.x;
      vdiff = theDistance.y;
      Vector2D.freeInstance(theDistance);

      if(!FORCENOTAFFINE && (FORCEAFFINE || tanangle < AFFINEBORDER)){
        //zdiff too small for correct interpolation with z
        isAffine = true;
        vrico = vdiff / xDiff;
        urico = udiff / xDiff;
      } else {
        isAffine = false;
        vrico = vdiff / zdiff;
        urico = udiff / zdiff;
      }
    }
  }

  private void repositionStartEnd(){
    xStart = (int)Math.floor(start.getPoint().x);
    xEnd = (int)Math.ceil(end.getPoint().x);
    invzStart = getZ(xStart);
    invzEnd = getZ(xEnd);
    lStart = getL(xStart);

    myPixel.x = xStart;
    myPixel.invZ = invzStart;
    l = lStart;
    
    myPixel.u = start.getTexturePoint().x;
    myPixel.v = start.getTexturePoint().y;
  }

  public boolean hasNext(){
    return myPixel.x < xEnd;
  }

  public void next(){
    myPixel.x++;
    myPixel.invZ += invzRico;
    l += lRico;
    myPixel.light = l;
    myPixel.camPoint = null;
    myPixel.normal = null;

    if(isTexture){
      if(isAffine){
        myPixel.u += urico;
        myPixel.v += vrico;
      } else {
        myPixel.z = 1 / myPixel.invZ;
        myPixel.u = getU(myPixel.z);
        myPixel.v = getV(myPixel.z);
      }
    }
  }
  
  private float getU(float z){
    return start.getTexturePoint().x + (z - start.getDepth()) * urico;
  }

  private float getV(float z){
    return start.getTexturePoint().y + (z - start.getDepth()) * vrico;
  }

  private float getZ(float x){
    return start.getInverseDepth() + (x - start.getPoint().x) * invzRico;
  }

  private float getL(float x){
    return start.getLightning() + (x - start.getPoint().x) * lRico;
  }

  public int getXEnd() {
    return xEnd;
  }
  public int getXStart() {
    return xStart;
  }

  public float getLRico() {
    return lRico;
  }
  public void setLRico(float rico) {
    lRico = rico;
  }
  public float getXDiff() {
    return xDiff;
  }
  public Texture2 getTexture() {
    return texture;
  }
  public void setTexture(Texture2 texture) {
    this.texture = texture;
  }

  public static Segment getInstance(Vertex2D aStartVertex, Vertex2D anEndVertex, Texture2 aTexture){
    Segment result;
    if (countFree == 0) {
      result = new Segment();
    } else {
      result = STACK[--countFree];
    }
    result.start = aStartVertex;
    result.end = anEndVertex;
    result.texture = aTexture;
    result.isTexture = aTexture != null;
    result.myPixel.texture = aTexture;

    result.calculateRicos();
    result.repositionStartEnd();
    return result;
  }

  public static void freeInstance(Segment aSegment) {
    if (countFree < POOL_SIZE) {
      STACK[countFree++] = aSegment;
    }
  }

  public static void main(String args[]){
    //  Segment theSegment1 = new Segment(26,30,8,4,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(25,38,6,6,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(18,24,8,4,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(18,20,5,5,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,6,4,4,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(2,4,2,2,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,6,4,4,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(4,6,2,2,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,4,4,4,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(2,6,2,2,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(4,6,4,4,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(2,6,2,2,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,6,4,4,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(2,6,2,2,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,6,4,2,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(2,5,2,4,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,6,4,2,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(3,6,2,4,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,6,4,2,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(2,4,2,3,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,6,4,2,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(4,6,3,4,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,6,4,2,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(2,6,2,4,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,6,2,4,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(2,5,4,2,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,6,2,4,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(3,6,4,2,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,6,2,4,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(2,4,4,3,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(2,6,2,4,0,0,Color.black.getRGB());
    //  Segment theSegment2 = new Segment(4,6,3,2,0,0,Color.black.getRGB());
    //  Segment theSegment1 = new Segment(325.9700513412126,333.3445947198124,2441.126174469844,2485.003620332541,0,0,Color.black.getRGB(), null);
    //  Segment theSegment2 = new Segment(333.3445947198124,334.92723657183296,2485.003620332543,2553.52800740042,0,0,Color.black.getRGB(), null);


    //  Segment[] theSegments  = theSegment1.intersect(theSegment2);
    //  Segment[] theSegments  = theSegment2.intersect(theSegment1);
    /*
		 for(int i=0;i<theSegments.length;i++){
		 System.out.println("Segment " + i + ": " + theSegments[i].toString());
		 }
     */

    //  Segment theSegment1 = new Segment(0,10,10,0, 0, 10, Color.black.getRGB());
    //  Segment theSegment1 = new Segment(0,10,10,0, 0, 10, Color.black.getRGB());
    //  Segment theSegment2 = new Segment(2,12,0,10, 0, 10, Color.black.getRGB());
    //  Segment[] theSegments  = null;
    //  long time1 = System.currentTimeMillis();
    //  int times = 1000000;
    //  for(int i=0;i<times;i++){
    //  theSegments = theSegment1.intersect(theSegment2);
    //  }
    //  long time2 = System.currentTimeMillis();
    //  System.out.println("time per intersect: " + ((float)time2 - (float)time1) / (float)times);
    //  for(int i=0;i<theSegments.length;i++){
    //  System.out.println("Segment " + i + ": " + theSegments[i].toString());
    //  }
  }
  
  public Pixel getPixel(){
    return myPixel;
  }

}
