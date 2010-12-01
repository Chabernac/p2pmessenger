package chabernac.space.buffer;

import org.apache.log4j.Logger;

import chabernac.space.LightSource;
import chabernac.space.Vector2D;
import chabernac.space.Vertex2D;
import chabernac.space.World;
import chabernac.space.geom.Point2D;
import chabernac.space.geom.Point3D;
import chabernac.space.texture.Texture2;

public class Segment {
  private static final int POOL_SIZE = 10;
  private static final Segment[] STACK = new Segment[POOL_SIZE];
  private static int countFree;

  private static Logger LOGGER = Logger.getLogger(Segment.class);

  private Texture2 texture = null;
  private int xStart, xEnd, x;
  private double invzStart, invzEnd, lStart, lEnd = 0, invz, z, u, v, l;
  private double zdiff, xDiff, invzRico, udiff, vdiff, urico, vrico, lRico = 0;
//  private int c = 0;
  public int color = 0;
  private Vertex2D start = null;
  private Vertex2D end = null;
  private boolean isTexture = false;
  private boolean isAffine = false;
  private static final double AFFINEBORDER = Math.tan(Math.PI / 10);
  private static boolean FORCEAFFINE = true;
  private static boolean FORCENOTAFFINE = false;
  private World myWorld = null;


  public Segment(){

  }

  public Segment(World aWorld, Vertex2D aStartVertex, Vertex2D anEndVertex, int aColor, Texture2 aTexture){
    //this(aStartVertex, anEndVertex, Color.black.getRGB(), aTexture);
    start = aStartVertex;
    end = anEndVertex;
    texture = aTexture;
    isTexture = aTexture != null;
    color = aColor;
    myWorld = aWorld;
    calculateRicos();
    repositionStartEnd();
  }

  private void calculateRicos() {
    Point2D theStartPoint = start.getPoint();
    Point2D theEndPoint = end.getPoint();

    xDiff = theEndPoint.x - theStartPoint.x;
    double yDiff = theEndPoint.y - theStartPoint.y;
    zdiff = end.getDepth() - start.getDepth();

    //TODO very inperformant
    double length = Math.sqrt(xDiff * xDiff + yDiff * yDiff); 

    double tanangle = Math.abs(zdiff) / length;

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

    x = xStart;
    invz = invzStart;
    l = lStart;
    u = start.getTexturePoint().x;
    v = start.getTexturePoint().y;
  }

  public boolean hasNext(){
    return x < xEnd;
  }

  public void next(){
    x++;
    invz += invzRico;
    l += lRico;

    if(isTexture){
      if(isAffine){
        u += urico;
        v += vrico;
      } else {
        z = 1 / invz;
        u = getU(z);
        v = getV(z);
      }
    }
  }

  public int applyShading(){
    int theU = (int)u;
    int theV = (int)v;
    double theL = l;
    int theColor;

    if(isTexture){
      //texture color
      theColor = texture.getColor(theU, theV);
      
      //bump mapping
      if(texture.getBumpMap() != null){
        chabernac.space.geom.GVector theCamNormalVector = texture.getNormalVector(theU, theV);
        Point3D theCamPoint = texture.getSystem().getTransformator().inverseTransform(new Point3D(theU, theV, 0.0D));
        theL += LightSource.calculateLight(myWorld, theCamPoint, theCamNormalVector);
        theL /= 2D;
      }
    } else {
      theColor = color;
    }

    //lightning
    int alpha = theColor >> 24 & 0xff;
    int red=  (int)(theL * (  theColor >> 16 & 0xff));
    int green= (int) (theL * (  theColor >> 8 & 0xff));
    int blue= (int) (theL * (  theColor & 0xff));
    
    if(red > 255) red = 255;
    if(green > 255) green = 255;
    if(blue > 255) blue = 255;
    if(red < 0 ) red = 0;
    if(green < 0) green = 0;
    if(blue < 0) blue = 0;

    return (alpha << 24 & 0xFF000000) | (red << 16 & 0x00FF0000) | (green << 8 & 0x0000FF00) | (blue << 0 & 0x000000FF);
  }

  public int getX(){
    return x;
  }

  private double getU(double z){
    return start.getTexturePoint().x + (z - start.getDepth()) * urico;
  }

  private double getV(double z){
    return start.getTexturePoint().y + (z - start.getDepth()) * vrico;
  }

  private double getZ(double x){
    return start.getInverseDepth() + (x - start.getPoint().x) * invzRico;
  }

  private double getL(double x){
    return start.getLightning() + (x - start.getPoint().x) * lRico;
  }

  public Point2D getTextureCoordinate(){
    return new Point2D(u, v);
  }

  public double getInverseZ(){
    return invz;
  }

  public double getLightning(){
    return l;
  }

  
  public double getLEnd() {
    return lEnd;
  }
  public void setLEnd(double end) {
    lEnd = end;
  }
  public double getLStart() {
    return lStart;
  }
  public void setLStart(double start) {
    lStart = start;
  }

  public int getXEnd() {
    return xEnd;
  }
  public int getXStart() {
    return xStart;
  }

  public double getLRico() {
    return lRico;
  }
  public void setLRico(double rico) {
    lRico = rico;
  }
  public double getXDiff() {
    return xDiff;
  }
  public Texture2 getTexture() {
    return texture;
  }
  public void setTexture(Texture2 texture) {
    this.texture = texture;
  }

  public String toString(){
    return "";
    //return "<Segment p0=(" + xStart + "," + zStart + ") p1=(" + xEnd + "," + zEnd + ")>";
  }

  public static Segment getInstance(World aWorld, Vertex2D aStartVertex, Vertex2D anEndVertex, int aColor, Texture2 aTexture){
    Segment result;
    if (countFree == 0) {
      result = new Segment();
    } else {
      result = STACK[--countFree];
    }
    result.start = aStartVertex;
    result.end = anEndVertex;
    result.color = aColor;
    result.texture = aTexture;
    result.isTexture = aTexture != null;
    result.myWorld = aWorld;

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
    //  System.out.println("time per intersect: " + ((double)time2 - (double)time1) / (double)times);
    //  for(int i=0;i<theSegments.length;i++){
    //  System.out.println("Segment " + i + ": " + theSegments[i].toString());
    //  }
  }

}
